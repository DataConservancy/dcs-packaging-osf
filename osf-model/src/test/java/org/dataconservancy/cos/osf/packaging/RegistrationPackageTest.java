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

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.model.Ontology;
import org.dataconservancy.cos.osf.packaging.support.Rdf;
import org.dataconservancy.cos.rdf.annotations.AnonIndividual;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import sun.security.tools.keytool.Resources_fr;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_HAS_CATEGORY;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_HAS_DATEREGISTERED;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_HAS_REGISTRATIONSUPPLEMENT;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_IS_DASHBOARD;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_IS_PENDINGEMBARGOAPPROVAL;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_IS_PENDINGREGISTRATIONAPPROVAL;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_IS_PENDINGWITHDRAWL;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_IS_REGISTRATION;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.OSF_IS_WITHDRAWN;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.ObjectProperty.OSF_REGISTERED_BY;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.ObjectProperty.OSF_REGISTERED_FROM;
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


        Individual registration = ontology.individual(relativeId(r.getId()), Rdf.Ns.OSF, Rdf.OwlClass.OSF_REGISTRATION);

        registration.addLiteral(ontology.property(OSF_HAS_DATEREGISTERED), dateRegistered);
        registration.addLiteral(ontology.property(OSF_IS_REGISTRATION), true);
        registration.addLiteral(ontology.property(OSF_IS_WITHDRAWN), isRegistrationWithdrawn);
        registration.addProperty(ontology.property(OSF_REGISTERED_BY), asResource(relativeId("a3q2g")));
        registration.addProperty(ontology.property(OSF_REGISTERED_FROM), asResource(projectRegisteredFrom));

        registration.addLiteral(ontology.property(OSF_IS_PENDINGEMBARGOAPPROVAL), isPendingEmbargoApproval);
        registration.addLiteral(ontology.property(OSF_IS_PENDINGREGISTRATIONAPPROVAL), isPendingRegistrationApproval);
        registration.addLiteral(ontology.property(OSF_IS_DASHBOARD), isDashboard);
        registration.addLiteral(ontology.property(OSF_IS_PENDINGWITHDRAWL), isPendingWithdrawl);
        registration.addLiteral(ontology.property(OSF_HAS_REGISTRATIONSUPPLEMENT), registrationSupplement);

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
        getAnnotationsForClass(registration.getClass(), annotationAttributesMap);
        assertEquals(44, annotationAttributesMap.size());

        // get the owl class to use from the instance

        OwlClasses owlClass = getOwlClass(registration, annotationAttributesMap);
        assertNotNull(owlClass);
        assertSame(OwlClasses.OSF_REGISTRATION, owlClass);

        // get the id to use for the individual from the instance; note, comes from super class
        Object owlIndividualId = getIndividualId(registration, annotationAttributesMap);
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

                            if (annotationAttributesMap.get(AnnotatedElementPair.forAnnotatedElement(field, AnonIndividual.class)) != null) {
                                OwlClasses anonClass = annotationAttributesMap.get(AnnotatedElementPair.forAnnotatedElement(field, AnonIndividual.class)).getEnum("value");
                                i = newIndividual(anonClass);
                                registrationIndividual.addProperty(
                                        ontology.objectProperty(owlProperty.ns(), owlProperty.localname()), i);
                                // need to recurse and add the properties of the anonymous individual
                            } else if (annotationAttributesMap.containsKey(AnnotatedElementPair.forAnnotatedElement(owlObject.getClass(), OwlIndividual.class))) {
                                // Obtain the OwlIndividual for the object
                                // Obtain the IndividualId for the object
                                i = newIndividual(getOwlClass(owlObject), getIndividualId(owlObject));
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




//            if (!owlProperty.object()) {
//
//                if (owlObjects != null) {
//                    owlObjects.forEach(element -> {
//                        // todo: does the tranform apply to each element of a collection, or to the collection as a whole?
//                        registrationIndividual.addLiteral(
//                                ontology.datatypeProperty(owlProperty.ns(), owlProperty.localname()), element);
//                    });
//                } else {
//                    registrationIndividual.addLiteral(
//                            ontology.datatypeProperty(owlProperty.ns(), owlProperty.localname()), transformedValue);
//                }
//            } else {
//                System.err.println(String.format("Adding property %s %s", owlProperty.localname(), transformedValue));
//
//                if (owlObjects != null) {
//                    // repeat the property for each element in the list
//                    (owlObjects).forEach(o -> {
//                        R transformed = transformer.apply((T) o);
//                        Individual i;
//
//                        // if the object property is annotated with anonindividual create an an anonymous individual
//                        // otherwise, create an individual with an id.
//
//                        if (annotationAttributesMap.get(AnnotatedElementPair.forAnnotatedElement(field, AnonIndividual.class)) != null) {
//                            OwlClasses anonClass = annotationAttributesMap.get(AnnotatedElementPair.forAnnotatedElement(field, AnonIndividual.class)).getEnum("value");
//                            i = newIndividual(anonClass);
//                            // need to recurse and add the properties of the anonymous individual
//                        } else {
//                            // Obtain the OwlIndividual for the object
//                            // Obtain the IndividualId for the object
//                            i = newIndividual(getOwlClass(o), getIndividualId(o));
//                        }
//
//                        registrationIndividual.addProperty(
//                                ontology.objectProperty(owlProperty.ns(), owlProperty.localname()), i);
//
//                    });
//
//                } else {
//                    // not a collection, so we just add a single property
//
//                    // if the object property is annotated with anonindividual create an an anonymous individual
//                    // otherwise, add a resource
//
//                    if (annotationAttributesMap.get(AnnotatedElementPair.forAnnotatedElement(field, AnonIndividual.class)) != null) {
//                        OwlClasses anonClass = annotationAttributesMap.get(AnnotatedElementPair.forAnnotatedElement(field, AnonIndividual.class)).getEnum("value");
//                        registrationIndividual.addProperty(
//                                ontology.objectProperty(owlProperty.ns(), owlProperty.localname()), newIndividual(anonClass));
//
//                        // need to recurse and add the properties of the anonymous individual
//                    } else {
//                        registrationIndividual.addProperty(
//                                ontology.objectProperty(owlProperty.ns(), owlProperty.localname()),
//                                asResource(transformedValue.toString()));
//                    }
//
//
//                }
//            }
//        });

        // for each objecttype property:
        // - determine if it is a collection
        // - determine if it is another domain model type, which may be another owl individual
        // - determine if it is a resource

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
        getAnnotationsForClass(r.getClass(), result);
        assertEquals(44, result.size());

        AnnotatedElementPair aep1 = new AnnotatedElementPair(r.getClass(), OwlIndividual.class);
        AnnotatedElementPair aep2 = new AnnotatedElementPair(r.getClass(), OwlIndividual.class);
        assertEquals(aep1, aep2);

        AnnotationAttributes attribs = result.get(AnnotatedElementPair.forAnnotatedElement(r.getClass(), OwlIndividual.class));
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

    private void getAnnotationsForClass(Class clazz, Map<AnnotatedElementPair, AnnotationAttributes> result) {
        System.err.println("Getting annotations for " + clazz.getName());
        // Get class-level annotations
        getAnnotations(clazz, result);

        // Recurse through its fields
        Stream.of(clazz.getDeclaredFields()).forEach(f -> getAnnotations(f, result));

        // Recurse the class hierarchy
        Class superclass = clazz.getSuperclass();
        if (superclass == Object.class) {
            return;
        }

        getAnnotationsForClass(superclass, result);
    }


    private void getAnnotations(AnnotatedElement annotatedElement, Map<AnnotatedElementPair, AnnotationAttributes> result) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        Stream.of(annotations).forEach(annotation -> {
                    AnnotatedElementPair aep = new AnnotatedElementPair(annotatedElement, annotation.annotationType());
                    result.put(aep, AnnotationUtils.getAnnotationAttributes(annotatedElement, annotation));
                }
        );
    }

    private <T, R> R performTransform(Function<T, R> fn, T foo) {
        return fn.apply(foo);
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

    private OwlClasses getOwlClass(Object o) {
        OwlClasses owlClass = (OwlClasses) AnnotationUtils.getValue(o.getClass().getAnnotation(OwlIndividual.class));
        assertNotNull(owlClass);
        return owlClass;
    }

    private OwlClasses getOwlClass(Object o, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        Class<OwlIndividual> annotationClass = OwlIndividual.class;
        String valueAttr = "value";

        AnnotatedElementPair aep = AnnotatedElementPair.forAnnotatedElement(o.getClass(), annotationClass);
        if (!attributesMap.containsKey(aep)) {
            throw new IllegalArgumentException(String.format("Could not find annotation %s on %s", annotationClass.getSimpleName(), o.getClass().getSimpleName()));
        }

        AnnotationAttributes attrs = attributesMap.get(aep);
        OwlClasses owlClass = attrs.getEnum(valueAttr);
        if (owlClass == null) {
            throw new IllegalArgumentException(String.format("Annotation %s missing required attribute %s on class %s", annotationClass.getSimpleName(), valueAttr, o.getClass().getSimpleName()));
        }

        return owlClass;
    }

    private Object getIndividualId(Object o, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        Class<OwlIndividual> owlIndividualClass = OwlIndividual.class;
        Class<IndividualUri> individualUriClass = IndividualUri.class;
        String transformAttr = "transform";
        List<Field> annotatedFields = new ArrayList<>();

        ReflectionUtils.doWithFields(o.getClass(),
                annotatedFields::add,
                field -> field.getDeclaredAnnotation(individualUriClass) != null);

        if (annotatedFields.size() == 0) {
            // The enclosing class may not be an OwlIndividual, therefore it wouldn't have an IndividualUri

            if (!attributesMap.containsKey(AnnotatedElementPair.forAnnotatedElement(o.getClass(), owlIndividualClass))) {
                throw new IllegalArgumentException(String.format("Annotation %s not found on %s.  Is %s an OwlIndividual?", individualUriClass.getSimpleName(), o.getClass().getSimpleName(), o.getClass().getSimpleName()));
            }

            throw new IllegalArgumentException(String.format("Missing required annotation %s on %s, an OwlIndividual.", individualUriClass.getSimpleName(), o.getClass().getSimpleName()));
        }

        if (annotatedFields.size() > 1) {
            throw new IllegalArgumentException(String.format("Found %s fields (%s) on %s annotated with %s.  Only one field may be annotated with %s", annotatedFields.size(), annotatedFields.stream().map(Field::getName).collect(Collectors.joining(", ")), o.getClass().getSimpleName(), individualUriClass.getSimpleName(), individualUriClass.getSimpleName()));
        }

        Field annotatedField = annotatedFields.get(0);
        AnnotatedElementPair aep = AnnotatedElementPair.forAnnotatedElement(annotatedField, individualUriClass);
        Class<Function> transformClass = (Class<Function>) attributesMap.get(aep).getClass(transformAttr);

        Function transformFn = null;
        try {
            transformFn = transformClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        Object individualUriValue = null;
        try {
            annotatedField.setAccessible(true);
            individualUriValue = annotatedField.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (individualUriValue == null) {
            throw new IllegalArgumentException(String.format("IndividualUri field %s on class %s is null", annotatedFields.get(0).getName(), o.getClass().getSimpleName()));
        }

        return transformFn.apply(individualUriValue);
    }

    private Object getIndividualId(Object o) {
        Map<Field, Function<Object, String>> individualUriFields = new HashMap<>();
        doWithFields(o.getClass(), f -> {
                    f.setAccessible(true);
                    Class<Function> transformClass = (Class<Function>) AnnotationUtils.getValue(f.getAnnotation(IndividualUri.class), "transform");

                    try {
                        individualUriFields.put(f, transformClass.newInstance());
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                },
                f -> f.getDeclaredAnnotation(IndividualUri.class) != null);

        assertEquals(1, individualUriFields.size());
        Object owlIndividualId = null;
        try {
            Field f = individualUriFields.keySet().iterator().next();
            Object fieldValue = f.get(o);
            Function<Object, String> fn = individualUriFields.get(f);
            owlIndividualId = fn.apply(fieldValue);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        assertNotNull(owlIndividualId);
        return owlIndividualId;
    }

    private Individual newIndividual(OwlClasses owlClass) {
        return ontology.individual(owlClass.ns(), owlClass.localname());
    }

    private Individual newIndividual(OwlClasses owlClass, Object individualId) {
        return ontology.individual(individualId.toString(), owlClass.ns(), owlClass.localname());
    }


    private static class AnnotatedElementPair {
        AnnotatedElement annotatedElement;
        Class<? extends Annotation> annotationClass;

        public AnnotatedElementPair(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationClass) {
            this.annotatedElement = annotatedElement;
            this.annotationClass = annotationClass;
        }

        static AnnotatedElementPair forAnnotatedElement(AnnotatedElement e, Class<? extends Annotation> annotationClass) {
            return new AnnotatedElementPair(e, annotationClass);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AnnotatedElementPair that = (AnnotatedElementPair) o;

            if (!annotatedElement.equals(that.annotatedElement)) return false;
            return annotationClass.equals(that.annotationClass);

        }

        @Override
        public int hashCode() {
            int result = annotatedElement.hashCode();
            result = 31 * result + annotationClass.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "AnnotatedElementPair{" +
                    "annotatedElement=" + annotatedElement +
                    ", annotationClass=" + annotationClass +
                    '}';
        }
    }
}
