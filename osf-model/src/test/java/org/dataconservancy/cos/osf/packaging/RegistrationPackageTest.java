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
import org.apache.jena.graph.Node;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.RegistrationProcessor;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.NodeBase;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.ont.OntologyManager;
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

    private OntologyManager ontologyManager = new OntologyManager();

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
        assertFalse(ontologyManager.getOntModel().listSubModels().hasNext());
        assertFalse(ontologyManager.getOntModel().listIndividuals().hasNext());


        Individual registration = ontologyManager.individual(relativeId(r.getId()), OwlClasses.OSF_REGISTRATION.ns(), OwlClasses.OSF_REGISTRATION.localname());

        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_DATEREGISTERED.fqname()), dateRegistered);
        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_REGISTRATION.fqname()), true);
        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_WITHDRAWN.fqname()), isRegistrationWithdrawn);
        registration.addProperty(ontologyManager.objectProperty(OwlProperties.OSF_REGISTERED_BY.fqname()), asResource(relativeId("a3q2g")));
        registration.addProperty(ontologyManager.objectProperty(OwlProperties.OSF_REGISTERED_FROM.fqname()), asResource(projectRegisteredFrom));

        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL.fqname()), isPendingEmbargoApproval);
        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL.fqname()), isPendingRegistrationApproval);
        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_DASHBOARD.fqname()), isDashboard);
        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_PENDINGWITHDRAWL.fqname()), isPendingWithdrawl);
        registration.addLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT.fqname()), registrationSupplement);

        assertFalse(ontologyManager.getOntModel().listSubModels().hasNext());
        assertTrue(ontologyManager.getOntModel().isInBaseModel(registration));
        assertTrue(ontologyManager.getOntModel().listIndividuals().hasNext());

        Model allIndividuals = ModelFactory.createDefaultModel();
        allIndividuals.setNsPrefixes(Rdf.Ns.PREFIXES);

        ontologyManager.getOntModel().listIndividuals().forEachRemaining(i -> allIndividuals.add(i.listProperties()));
        writeModel(allIndividuals);

    }

    @Test
    public <T, R> void testCreateRegistrationPackageAnnotation() throws Exception {
        PackageGraph packageGraph = new PackageGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(testName, RegistrationPackageTest.class, getBaseUri()));
        Registration registration = factory.getOsfService(OsfService.class).registration("eq7a4").execute().body();
        assertNotNull(registration);
        User user = factory.getOsfService(OsfService.class).user(registration.getContributors().iterator().next().getId()).execute().body();

        RegistrationProcessor rp = new RegistrationProcessor(registration, packageGraph);
        String registrationIndividualUri = rp.process();
        String userIndividualUri = rp.process(user);

        writeModel(onlyIndividuals(ontologyManager.getOntModel()));

        Individual registrationIndividual = ontologyManager.getOntModel().getIndividual(registrationIndividualUri);
        assertEquals("PROJECT", registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_CATEGORY.fqname())).toString());
        assertEquals(ResourceFactory.createResource("vae86"), registrationIndividual.getPropertyResourceValue(ontologyManager.objectProperty(OwlProperties.OSF_HAS_CHILD.fqname())));
        Set<RDFNode> contributorNodes = registrationIndividual.listPropertyValues(ontologyManager.objectProperty(OwlProperties.OSF_HAS_CONTRIBUTOR.fqname())).toSet();
        // TODO verify both contributors
        assertEquals(2, contributorNodes.size());
        RDFNode contributorNode = contributorNodes.iterator().next();
        assertTrue(contributorNode.isAnon());
// TODO       assertTrue(contributorNode.asResource().hasLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_BIBLIOGRAPHIC.fqname()), true));
        assertTrue(contributorNode.asResource().hasLiteral(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_PERMISSION.fqname()), "ADMIN"));
        // TODO double-check timezone and conversion from JodaTime to Calendar
        assertEquals(ResourceFactory.createTypedLiteral("2016-06-03T21:53:52.434Z", XSDDatatype.XSDdateTime), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_DATECREATED.fqname())));
        assertEquals(ResourceFactory.createTypedLiteral("2016-06-07T21:52:19.617Z", XSDDatatype.XSDdateTime), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_DATEMODIFIED.fqname())));
        assertEquals(ResourceFactory.createTypedLiteral("2016-06-07T21:53:10.603Z", XSDDatatype.XSDdateTime), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_DATEREGISTERED.fqname())));
        assertEquals("Test project Two.", registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_DESCRIPTION.fqname())).toString());
        assertEquals("eq7a4", registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_ID.fqname())).toString());
        assertEquals("Open-Ended Registration", registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT.fqname())).toString());
        assertEquals(ResourceFactory.createResource("eq7a4"), registrationIndividual.getPropertyResourceValue(ontologyManager.objectProperty(OwlProperties.OSF_HAS_ROOT.fqname())));
        assertEquals("Project Two", registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_TITLE.fqname())).toString());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_COLLECTION.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_FORK.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_PUBLIC.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_REGISTRATION.fqname())).asLiteral());
        assertEquals(ResourceFactory.createTypedLiteral("false", XSDDatatype.XSDboolean), registrationIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_WITHDRAWN.fqname())).asLiteral());
        assertEquals(ResourceFactory.createResource("qmdz6"), registrationIndividual.getPropertyResourceValue(ontologyManager.objectProperty(OwlProperties.OSF_REGISTERED_BY.fqname())));
        assertEquals(ResourceFactory.createResource("3e7rd"), registrationIndividual.getPropertyResourceValue(ontologyManager.objectProperty(OwlProperties.OSF_REGISTERED_FROM.fqname())));

        Set<RDFNode> tags = registrationIndividual.listPropertyValues(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_TAG.fqname())).toSet();
        assertEquals(1, tags.size());
        assertTrue(tags.contains(ResourceFactory.createPlainLiteral("newtag")));

        Set<RDFNode> perms = registrationIndividual.listPropertyValues(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_PERMISSION.fqname())).toSet();
        assertEquals(3, perms.size());
        assertTrue(perms.contains(ResourceFactory.createPlainLiteral("READ")));
        assertTrue(perms.contains(ResourceFactory.createPlainLiteral("WRITE")));
        assertTrue(perms.contains(ResourceFactory.createPlainLiteral("ADMIN")));

        assertTrue(ontologyManager.individual("eq7a4").hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertFalse(ontologyManager.individual("eq7a4").hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertTrue(ontologyManager.individual("vae86").hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertFalse(ontologyManager.individual("vae86").hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertTrue(ontologyManager.individual("qmdz6").hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertFalse(ontologyManager.individual("qmdz6").hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertTrue(ontologyManager.individual("3e7rd").hasOntClass(OwlClasses.OSF_NODE.fqname()));
        assertFalse(ontologyManager.individual("3e7rd").hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));

        Individual userIndividual = ontologyManager.getOntModel().getIndividual(userIndividualUri);
        assertEquals(ResourceFactory.createTypedLiteral("2016-06-03T21:52:35.4Z", XSDDatatype.XSDdateTime), userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_DATEUSERREGISTERED.fqname())));
        // TODO: not sure about these empty strings...
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_BAIDUID.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_TWITTER.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_IMPACTSTORY.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_MIDDLENAMES.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_PERSONALWEBSITE.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_RESEARCHGATE.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_RESEARCHERID.fqname())).toString());
        assertEquals("", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_SUFFIX.fqname())).toString());
        assertEquals("Elliot Metsger", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_FULLNAME.fqname())).toString());
        assertEquals("Elliot", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_GIVENNAME.fqname())).toString());
//        assertEquals("in/elliot-metsger-2455915", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_LINKEDIN.fqname())).toString());
        assertEquals("en_US", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_LOCALE.fqname())).toString());
//        assertEquals("QsELf4QAAAAJ", userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_HAS_SCHOLAR.fqname())).toString());
        // TODO map github in java model
        assertEquals(ResourceFactory.createTypedLiteral("true", XSDDatatype.XSDboolean), userIndividual.getPropertyValue(ontologyManager.datatypeProperty(OwlProperties.OSF_IS_ACTIVE.fqname())).asLiteral());


    }

    @Test
    public void testGetAnnotations() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(testName, RegistrationPackageTest.class, getBaseUri()));
        Registration r = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(r);

        Map<AnnotatedElementPair, AnnotationAttributes> result = new HashMap<>();
        OwlAnnotationProcessor.getAnnotationsForClass(r.getClass(), result);
        assertEquals(45, result.size());

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




}
