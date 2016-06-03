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

import com.diffplug.common.base.Errors;
import com.diffplug.common.base.Throwing;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.model.Ontology;
import org.dataconservancy.cos.osf.packaging.support.Rdf;
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

import javax.swing.text.html.HTMLDocument;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.dataconservancy.cos.osf.packaging.support.Rdf.DatatypeProperty.*;
import static org.dataconservancy.cos.osf.packaging.support.Rdf.ObjectProperty.*;
import static org.dataconservancy.cos.osf.packaging.support.Util.asResource;
import static org.dataconservancy.cos.osf.packaging.support.Util.relativeId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
    public void testCreateRegistrationPackageAnnotation() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(testName, RegistrationPackageTest.class, getBaseUri()));
        Registration registration = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(registration);


        // get the owl class to use from the instance

        OwlClasses owlClass = getOwlClass(registration);
        assertNotNull(owlClass);
        assertSame(OwlClasses.OSF_REGISTRATION, owlClass);

        // get the id to use for the individual from the instance; note, comes from super class
        Object owlIndividualId = getIndividualId(registration);
        assertNotNull(owlIndividualId);

        // create the individual
        Individual registrationIndividual = newIndividual(owlClass, owlIndividualId);
        assertNotNull(registrationIndividual);

        // get the properties (recursively up the class hierarchy) used for OWL

        List<Field> owlPropertyFields = new ArrayList<>();
        Map<Field, AnnotationAttributes> fieldAnnotationAttrs = new HashMap<>();
        doWithFields(registration.getClass(),
                f -> {
                    f.setAccessible(true);
                    owlPropertyFields.add(f);
                    AnnotationAttributes annotationAttributes = AnnotationUtils.getAnnotationAttributes(f, f.getAnnotation(OwlProperty.class));
                    fieldAnnotationAttrs.put(f, annotationAttributes);
                },
                f -> f.getDeclaredAnnotation(OwlProperty.class) != null);
        assertEquals(23, owlPropertyFields.size());
        assertTrue(owlPropertyFields.contains(NodeBase.class.getDeclaredField("id")));

        // for each property:
        //  - determine Datatype vs Object property
        //  - for each Datatype property, add a literal to the individual

        fieldAnnotationAttrs.forEach((field, attributes) -> {
            OwlProperties property = attributes.getEnum("value");
            Object fieldValue = null;
            try {
                fieldValue = field.get(registration);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            if (fieldValue == null) {
                System.err.println(String.format("Skipping null property %s", property.localname()));
                return;
            }

            Class<Function<Object, String>> transformClass = (Class<Function<Object, String>>) attributes.getClass("transform");
            Function<Object, String> transformer;
            try {
                transformer = transformClass.newInstance();
            } catch (InstantiationException|IllegalAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            String transformedValue = transformer.apply(fieldValue);
            System.err.println(String.format("Adding property %s %s", property.localname(), transformedValue));

            if (!property.object()) {
                registrationIndividual.addLiteral(
                        ontology.datatypeProperty(property.ns(), property.localname()), transformedValue);
            } else {
                System.err.println(String.format("Adding property %s %s", property.localname(), transformedValue));
                if (isCollection(field.getType())) {
                    // repeat the property for each element in the list
                    ((Collection) fieldValue).forEach(o -> {
                        String transformed = transformer.apply(o);
                        // Obtain the OwlIndividual for the object
                        // Obtain the IndividualId for the object
                        Individual i = newIndividual(getOwlClass(o), getIndividualId(o));

                        registrationIndividual.addProperty(
                                ontology.objectProperty(property.ns(), property.localname()), i);

                    });

                } else {
                    // not a collection, so we just add a single property
                    registrationIndividual.addProperty(
                            ontology.objectProperty(property.ns(), property.localname()),
                            asResource(transformedValue));
                }
            }
        });

        // for each objecttype property:
        // - determine if it is a collection
        // - determine if it is another domain model type, which may be another owl individual
        // - determine if it is a resource

        writeModel(onlyIndividuals(ontology.getOntModel()));
//        writeModel(ontology.getOntModel());
    }

    private boolean isCollection(Class<?> candidate) {
        if (Collection.class.isAssignableFrom(candidate)) {
            return true;
        }

        return false;
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
        OwlClasses owlClass = (OwlClasses) AnnotationUtils.getValue(o.getClass().getAnnotation(OwlIndividual.class));;
        assertNotNull(owlClass);
        return owlClass;
    }
    private Object getIndividualId(Object o) {
        Map<Field, Function<Object, String>> individualUriFields = new HashMap<>();
        doWithFields(o.getClass(), f -> {
            f.setAccessible(true);
                    Class<Function> transformClass = (Class<Function>)AnnotationUtils.getValue(f.getAnnotation(IndividualUri.class), "transform");

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

    private Individual newIndividual(OwlClasses owlClass, Object individualId) {
        return ontology.individual(individualId.toString(), owlClass.ns(), owlClass.localname());
    }


}
