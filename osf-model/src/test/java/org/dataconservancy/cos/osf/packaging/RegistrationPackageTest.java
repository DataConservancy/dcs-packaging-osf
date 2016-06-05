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
package org.dataconservancy.cos.osf.packaging;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.model.Ontology;
import org.dataconservancy.cos.osf.packaging.support.AnnotatedElementPair;
import org.dataconservancy.cos.osf.packaging.support.OwlAnnotationProcessor;
import org.dataconservancy.cos.rdf.annotations.AnonIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.Rdf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.dataconservancy.cos.osf.packaging.support.Util.asResource;
import static org.dataconservancy.cos.osf.packaging.support.Util.relativeId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Created by esm on 6/1/16.
 */
public class RegistrationPackageTest extends AbstractMockServerTest {

    private String baseUri = getBaseUri().toString();

    private Ontology ontology = new Ontology();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateRegistrationPackage() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(testName, RegistrationPackageTest.class, getBaseUri()));

        Registration r = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(r);
        String dateRegistered = r.getDate_registered();
        String embargoEndDate = r.getEmbargo_end_date();
        String withdrawJustification = r.getWithdrawal_justification();
        boolean isPendingWithdrawl = (r.isPending_withdrawal() != null ? r.isPending_withdrawal().booleanValue() : false);
        String projectRegisteredFrom = r.getRegistered_from();
        boolean isDashboard = (r.isDashboard() != null ? r.isDashboard().booleanValue() : false);
//        String userRegisteredBy =
        boolean isRegistrationWithdrawn = r.isWithdrawn();
        boolean isPendingRegistrationApproval = (r.isPending_registration_approval() != null ? r.isPending_registration_approval().booleanValue() : false);
        boolean isPendingEmbargoApproval = (r.isPending_embargo_approval() != null ? r.isPending_embargo_approval().booleanValue() : false);
        String registrationSupplement = r.getRegistration_supplement();
        assertFalse(ontology.getOntModel().listSubModels().hasNext());
        assertFalse(ontology.getOntModel().listIndividuals().hasNext());


        Individual registration = ontology.individual(relativeId(r.getId()), OwlClasses.OSF_REGISTRATION.ns(), OwlClasses.OSF_REGISTRATION.localname());

        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_HAS_DATEREGISTERED.fqname()), dateRegistered);
        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_IS_REGISTRATION.fqname()), true);
        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_IS_WITHDRAWN.fqname()), isRegistrationWithdrawn);
        registration.addProperty(ontology.objectProperty(OwlProperties.OSF_REGISTERED_BY.fqname()), asResource(relativeId("a3q2g")));
        registration.addProperty(ontology.objectProperty(OwlProperties.OSF_REGISTERED_FROM.fqname()), asResource(projectRegisteredFrom));

        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL.fqname()), isPendingEmbargoApproval);
        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL.fqname()), isPendingRegistrationApproval);
        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_IS_DASHBOARD.fqname()), isDashboard);
        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_IS_PENDINGWITHDRAWL.fqname()), isPendingWithdrawl);
        registration.addLiteral(ontology.datatypeProperty(OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT.fqname()), registrationSupplement);

        assertFalse(ontology.getOntModel().listSubModels().hasNext());
        assertTrue(ontology.getOntModel().isInBaseModel(registration));
        assertTrue(ontology.getOntModel().listIndividuals().hasNext());

        Model allIndividuals = ModelFactory.createDefaultModel();
        allIndividuals.setNsPrefixes(Rdf.Ns.PREFIXES);

        ontology.getOntModel().listIndividuals().forEachRemaining(i -> allIndividuals.add(i.listProperties()));
        writeModel(allIndividuals);

    }

    @Test
    public <T, R> void testCreateRegistrationPackageAnnotation() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(testName, RegistrationPackageTest.class, getBaseUri()));
        Registration registration = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(registration);

        Map<AnnotatedElementPair, AnnotationAttributes> annotationAttributesMap = new HashMap<>();
        OwlAnnotationProcessor.getAnnotationsForClass(registration.getClass(), annotationAttributesMap);
        assertEquals(44, annotationAttributesMap.size());

        // get the owl class to use from the instance

        OwlClasses owlClass = OwlAnnotationProcessor.getOwlClass(registration, annotationAttributesMap);
        assertNotNull(owlClass);
        assertSame(OwlClasses.OSF_REGISTRATION, owlClass);

        // get the id to use for the individual from the instance; note, comes from super class
        Object owlIndividualId = OwlAnnotationProcessor.getIndividualId(registration, annotationAttributesMap);
        assertNotNull(owlIndividualId);

        // create the individual
        Individual registrationIndividual = newIndividual(owlClass, owlIndividualId);
        assertNotNull(registrationIndividual);

        // get the properties (recursively up the class hierarchy) used for OWL

        List<Field> owlPropertyFields = new ArrayList<>();
        List<Field> anonIndividualFields = new ArrayList<>();
        Map<Field, AnnotationAttributes> owlPropertyAnnotations = getFieldAnnotationAttribute(registration, owlPropertyFields, OwlProperty.class);
        Map<Field, AnnotationAttributes> anonIndividiualAnnotations = getFieldAnnotationAttribute(registration, anonIndividualFields, AnonIndividual.class);
        assertEquals(26, owlPropertyFields.size());
        assertTrue(owlPropertyFields.contains(NodeBase.class.getDeclaredField("id")));
        assertEquals(1, anonIndividiualAnnotations.size());


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
                    registrationIndividual.addLiteral(
                            ontology.datatypeProperty(owlProperty.ns(), owlProperty.localname()), transformedObject);
                } else {
                    Individual i;

                    // if the object property is annotated with @AnonIndividual create an anonymous individual

                    // if the object property is an instance of a Java class with an @OwlIndividual, create an
                    // Owl identifier for the individual

                    // otherwise, create a resource

                    if (annotationAttributesMap.get(AnnotatedElementPair.forPair(field, AnonIndividual.class)) != null) {
                        OwlClasses anonClass = annotationAttributesMap.get(AnnotatedElementPair.forPair(field, AnonIndividual.class)).getEnum("value");
                        i = newIndividual(anonClass);
                        registrationIndividual.addProperty(
                                ontology.objectProperty(owlProperty.ns(), owlProperty.localname()), i);
                        // need to recurse and add the properties of the anonymous individual
                    } else if (annotationAttributesMap.containsKey(AnnotatedElementPair.forPair(owlObject.getClass(), OwlIndividual.class))) {
                        // Obtain the OwlIndividual for the object
                        // Obtain the IndividualId for the object
                        i = newIndividual(OwlAnnotationProcessor.getOwlClass(owlObject, annotationAttributesMap), OwlAnnotationProcessor.getIndividualId(owlObject, annotationAttributesMap));
                        registrationIndividual.addProperty(
                                ontology.objectProperty(owlProperty.ns(), owlProperty.localname()), i);
                    } else {
                        registrationIndividual.addProperty(
                                ontology.objectProperty(owlProperty.ns(), owlProperty.localname()),
                                asResource(transformedObject.toString()));
                    }
                }
            });
        });


        writeModel(onlyIndividuals(ontology.getOntModel()));

        assertEquals("PROJECT", registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_CATEGORY.fqname())).toString());
        assertEquals(ResourceFactory.createResource("zgbd5"), registrationIndividual.getPropertyResourceValue(ontology.objectProperty(OwlProperties.OSF_HAS_CHILD.fqname())));
        // TODO hasContributor
        assertEquals(ResourceFactory.createTypedLiteral("2016-04-19T17:06:25.038Z", XSDDatatype.XSDdateTime), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_DATECREATED.fqname())));
        assertEquals(ResourceFactory.createTypedLiteral("2016-05-31T23:38:18.983Z", XSDDatatype.XSDdateTime), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_DATEMODIFIED.fqname())));
        assertEquals(ResourceFactory.createTypedLiteral("2016-05-31T23:38:58.952Z", XSDDatatype.XSDdateTime), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_DATEREGISTERED.fqname())));
        assertEquals("My first project created through the OSF UI", registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_DESCRIPTION.fqname())).toString());
        assertEquals("y6cx7", registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_ID.fqname())).toString());
        assertEquals("Open-Ended Registration", registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT.fqname())).toString());
        assertEquals(ResourceFactory.createResource("y6cx7"), registrationIndividual.getPropertyResourceValue(ontology.objectProperty(OwlProperties.OSF_HAS_ROOT.fqname())));
        assertEquals("First Proj", registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_HAS_TITLE.fqname())).toString());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_COLLECTION.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_FORK.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_PUBLIC.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_REGISTRATION.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontology.datatypeProperty(OwlProperties.OSF_IS_WITHDRAWN.fqname())).asLiteral());
        assertEquals(ResourceFactory.createResource("a3q2g"), registrationIndividual.getPropertyResourceValue(ontology.objectProperty(OwlProperties.OSF_REGISTERED_BY.fqname())));
        assertEquals(ResourceFactory.createResource("r5s4u"), registrationIndividual.getPropertyResourceValue(ontology.objectProperty(OwlProperties.OSF_REGISTERED_FROM.fqname())));

        Set<RDFNode> tags = registrationIndividual.listPropertyValues(ontology.datatypeProperty(OwlProperties.OSF_HAS_TAG.fqname())).toSet();
        assertEquals(2, tags.size());

        assertTrue(tags.contains(ResourceFactory.createPlainLiteral("firstproject")));
        assertTrue(tags.contains(ResourceFactory.createPlainLiteral("newbie")));

        assertTrue(ontology.individual("y6cx7").hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertTrue(ontology.individual("zgbd5").hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertTrue(ontology.individual("a3q2g").hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertTrue(ontology.individual("r5s4u").hasOntClass(OwlClasses.OSF_NODE.fqname()));

//        writeModel(ontology.getOntModel());
    }

    @Test
    public void testGetAnnotations() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(testName, RegistrationPackageTest.class, getBaseUri()));
        Registration r = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(r);

        Map<AnnotatedElementPair, AnnotationAttributes> result = new HashMap<>();
        OwlAnnotationProcessor.getAnnotationsForClass(r.getClass(), result);
        assertEquals(44, result.size());

        AnnotatedElementPair aep1 = new AnnotatedElementPair(r.getClass(), OwlIndividual.class);
        AnnotatedElementPair aep2 = new AnnotatedElementPair(r.getClass(), OwlIndividual.class);
        assertEquals(aep1, aep2);

        AnnotationAttributes attribs = result.get(AnnotatedElementPair.forPair(r.getClass(), OwlIndividual.class));
        assertNotNull(attribs);
        assertEquals(OwlClasses.OSF_REGISTRATION, attribs.getEnum("value"));


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

    private void writeModel(Model m) {
        System.err.println();
        System.err.println("---");
        RDFDataMgr.write(System.err, m, RDFFormat.TURTLE_PRETTY);
        System.err.println("---");
        System.err.println();
    }

    private Model onlyIndividuals(OntModel m) {
        Model individuals = ModelFactory.createDefaultModel();
        individuals.setNsPrefixes(Rdf.Ns.PREFIXES);

        m.listIndividuals().forEachRemaining(i -> individuals.add(i.listProperties()));
        return individuals;
    }


    private Individual newIndividual(OwlClasses owlClass) {
        return ontology.individual(owlClass.ns(), owlClass.localname());
    }

    private Individual newIndividual(OwlClasses owlClass, Object individualId) {
        return ontology.individual(individualId.toString(), owlClass.ns(), owlClass.localname());
    }

}
