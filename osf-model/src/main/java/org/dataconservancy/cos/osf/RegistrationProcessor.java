/*
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.cos.osf;

import org.apache.jena.ontology.Individual;
import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.PackageGraph;
import org.dataconservancy.cos.osf.packaging.support.AnnotatedElementPair;
import org.dataconservancy.cos.osf.packaging.support.OwlAnnotationProcessor;
import org.dataconservancy.cos.rdf.annotations.AnonIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.dataconservancy.cos.osf.packaging.support.Util.asResource;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Created by esm on 6/5/16.
 */
public class RegistrationProcessor {

    private Registration registration;

    private PackageGraph packageGraph;

    public RegistrationProcessor(Registration registration, PackageGraph packageGraph) {
        this.registration = registration;
        this.packageGraph = packageGraph;
    }

    public String process() {

        Map<AnnotatedElementPair, AnnotationAttributes> annotationAttributes = getAnnotationAttributes(registration);

        String registrationUri = newRegistration(registration, annotationAttributes, packageGraph);

        List<Field> owlPropertyFields = new ArrayList<>();
        List<Field> anonIndividualFields = new ArrayList<>();
        Map<Field, AnnotationAttributes> owlPropertyAnnotations = getFieldAnnotationAttribute(registration, owlPropertyFields, OwlProperty.class);
        Map<Field, AnnotationAttributes> anonIndividiualAnnotations = getFieldAnnotationAttribute(registration, anonIndividualFields, AnonIndividual.class);

        processOwlProperties(registrationUri, registration, packageGraph, owlPropertyAnnotations, anonIndividiualAnnotations, annotationAttributes);
        return registrationUri;
    }


    Map<AnnotatedElementPair, AnnotationAttributes> getAnnotationAttributes(Registration registration) {
        Map<AnnotatedElementPair, AnnotationAttributes> attributesMap = new HashMap<>();
        OwlAnnotationProcessor.getAnnotationsForClass(registration.getClass(), attributesMap);
        return attributesMap;
    }

    String newRegistration(Registration registration, Map<AnnotatedElementPair, AnnotationAttributes> annotationAttributes, PackageGraph packageGraph) {
        OwlClasses owlClass = OwlAnnotationProcessor.getOwlClass(registration, annotationAttributes);

        // get the id to use for the individual from the instance.  Note this may come from a super class.
        Object owlIndividualId = OwlAnnotationProcessor.getIndividualId(registration, annotationAttributes);

        // create the individual
        return packageGraph.newIndividual(owlClass, owlIndividualId);
    }

    <T, R> void processOwlProperties(String registrationIndividualUri, Object registration, PackageGraph packageGraph, Map<Field, AnnotationAttributes> owlPropertyAnnotations, Map<Field, AnnotationAttributes> anonIndividiualAnnotations, Map<AnnotatedElementPair, AnnotationAttributes> annotationAttributes) {


        // for each property:
        //  - determine Datatype vs Object property
        //  - for each Datatype property, add a literal to the individual
        //  - for each Object property, add a resource or an anonymous individual

        owlPropertyAnnotations.forEach((field, attributes) -> {

            // The OWL property we are adding to the individual
            OwlProperties owlProperty = attributes.getEnum("value");

            // The objects of the OWL property.  If the field is a Collection or an array type, there may be multiple
            // objects.
            Stream<T> owlObjects = null;

            try {
                Object fieldValue = null;
                fieldValue = field.get(registration);
                if (fieldValue == null) {
                    System.err.println(String.format("Skipping null property %s", owlProperty.localname()));
                    return;
                }
                if (isCollection(field.getType())) {
                    owlObjects = ((Collection) fieldValue).stream();
                } else if (isArray(field.getType())) {
                    owlObjects = Stream.of(((T[]) fieldValue));
                } else {
                    owlObjects = Stream.of((T) fieldValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // Create an instance of the transformer that is applied to each owlObject.

            Class<Function<T, R>> transformClass = (Class<Function<T, R>>) attributes.getClass("transform");
            Function<T, R> transformer;
            try {
                transformer = transformClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            owlObjects.forEach(owlObject -> {
                System.err.println(String.format("Transforming property %s (type %s) with %s", field.getName(), field.getType(), transformClass.getSimpleName()));
                R transformedObject = transformer.apply(owlObject);
                System.err.println(String.format("Adding property %s %s (type %s)", owlProperty.localname(), transformedObject, transformedObject.getClass().getSimpleName()));

                if (!owlProperty.object()) {
                    packageGraph.addLiteral(registrationIndividualUri, owlProperty.fqname(), transformedObject);
                } else {
                    String individualUri;

                    // if the object property is annotated with @AnonIndividual create an anonymous individual

                    // if the object property is an instance of a Java class with an @OwlIndividual, create an
                    // Owl identifier for the individual

                    // otherwise, create a resource

                    if (annotationAttributes.get(AnnotatedElementPair.forPair(field, AnonIndividual.class)) != null) {
                        OwlClasses anonClass = annotationAttributes.get(AnnotatedElementPair.forPair(field, AnonIndividual.class)).getEnum("value");
//                        i = newIndividual(anonClass);
                        Individual anonIndividual = packageGraph.newIndividual(anonClass);
                        // need to recurse and add the properties of the anonymous individual

                        packageGraph.addAnonIndividual(registrationIndividualUri, owlProperty.fqname(), anonIndividual);
                    } else if (annotationAttributes.containsKey(AnnotatedElementPair.forPair(owlObject.getClass(), OwlIndividual.class))) {
                        // Obtain the OwlIndividual for the object
                        // Obtain the IndividualId for the object
                        individualUri = packageGraph.newIndividual(
                                OwlAnnotationProcessor.getOwlClass(owlObject, annotationAttributes),
                                OwlAnnotationProcessor.getIndividualId(owlObject, annotationAttributes));

                        packageGraph.addIndividual(registrationIndividualUri, owlProperty.fqname(), individualUri);
                    } else {

                        packageGraph.addResource(registrationIndividualUri, owlProperty.fqname(), asResource(transformedObject.toString()));
                    }
                }
            });
        });
    }

    private Map<Field, AnnotationAttributes> getFieldAnnotationAttribute(Registration registration, List<Field> annotatedFields, Class<? extends Annotation> annotation) {
        Map<Field, AnnotationAttributes> fieldAnnotationAttrs = new HashMap<>();
        doWithFields(registration.getClass(),
                f -> {
                    f.setAccessible(true);
                    annotatedFields.add(f);
                    AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(f, f.getAnnotation(annotation));
                    fieldAnnotationAttrs.put(f, annotationAttributes);
                },
                f -> f.getDeclaredAnnotation(annotation) != null);
        return fieldAnnotationAttrs;
    }

    private boolean isCollection(Class<?> candidate) {
        return Collection.class.isAssignableFrom(candidate);
    }

    private boolean isArray(Class<?> candidate) {
        return candidate.isArray();
    }
}
