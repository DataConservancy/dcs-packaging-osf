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
import org.dataconservancy.cos.osf.client.model.Category;
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.Wiki;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.rdf.support.AnnotationsProcessor;
import org.dataconservancy.cos.rdf.support.OntologyManager;
import org.dataconservancy.cos.rdf.support.AnnotatedElementPair;
import org.dataconservancy.cos.rdf.support.AnnotatedElementPairMap;
import org.dataconservancy.cos.rdf.support.OwlAnnotationProcessor;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.ManagedGraph;
import org.dataconservancy.cos.rdf.support.Rdf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.dataconservancy.cos.rdf.support.Util.asResource;
import static org.dataconservancy.cos.rdf.support.Util.relativeId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class RegistrationPackageTest extends AbstractMockServerTest {

    private String baseUri = getBaseUri().toString();

    private OntologyManager ontologyManager = new OntologyManager();

    private RdfTestUtil testUtil = new RdfTestUtil(ontologyManager);

    @Rule
    public TestName TEST_HAME = new TestName();

    @Test
    public void testCreateRegistrationPackage() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_HAME, RegistrationPackageTest.class));

        final Registration r = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(r);
        final String dateRegistered = r.getDate_registered();
        final String embargoEndDate = r.getEmbargo_end_date();
        final String withdrawJustification = r.getWithdrawal_justification();
        final boolean isPendingWithdrawl = (r.isPending_withdrawal() != null
                ? r.isPending_withdrawal().booleanValue() : false);
        final String projectRegisteredFrom = r.getRegistered_from();
        final boolean isDashboard = (r.isDashboard() != null ? r.isDashboard().booleanValue() : false);
//        String userRegisteredBy =
        final boolean isRegistrationWithdrawn = r.isWithdrawn();
        final boolean isPendingRegistrationApproval = (r.isPending_registration_approval() != null
                ? r.isPending_registration_approval().booleanValue() : false);
        final boolean isPendingEmbargoApproval = (r.isPending_embargo_approval() != null
                ? r.isPending_embargo_approval().booleanValue() : false);
        final String registrationSupplement = r.getRegistration_supplement();
        assertFalse(ontologyManager.getOntModel().listSubModels().hasNext());
        assertFalse(ontologyManager.getOntModel().listIndividuals().hasNext());


        final Individual registration = ontologyManager.individual(relativeId(r.getId()),
                OwlClasses.OSF_REGISTRATION.ns(), OwlClasses.OSF_REGISTRATION.localname());

        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_HAS_DATEREGISTERED.fqname()), dateRegistered);
        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_IS_REGISTRATION.fqname()), true);
        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_IS_WITHDRAWN.fqname()), isRegistrationWithdrawn);
        registration.addProperty(ontologyManager.objectProperty(
                OwlProperties.OSF_REGISTERED_BY.fqname()), asResource(relativeId("a3q2g")));
        registration.addProperty(ontologyManager.objectProperty(
                OwlProperties.OSF_REGISTERED_FROM.fqname()), asResource(projectRegisteredFrom));

        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL.fqname()), isPendingEmbargoApproval);
        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL.fqname()), isPendingRegistrationApproval);
        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_IS_DASHBOARD.fqname()), isDashboard);
        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_IS_PENDINGWITHDRAWL.fqname()), isPendingWithdrawl);
        registration.addLiteral(ontologyManager.datatypeProperty(
                OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT.fqname()), registrationSupplement);

        assertFalse(ontologyManager.getOntModel().listSubModels().hasNext());
        assertTrue(ontologyManager.getOntModel().isInBaseModel(registration));
        assertTrue(ontologyManager.getOntModel().listIndividuals().hasNext());

        final Model allIndividuals = ModelFactory.createDefaultModel();
        allIndividuals.setNsPrefixes(Rdf.Ns.PREFIXES);

        ontologyManager.getOntModel().listIndividuals().forEachRemaining(i -> allIndividuals.add(i.listProperties()));
//        writeModel(allIndividuals);

    }

    @Test
    public void testCreateRegistrationPackageAnnotation() throws Exception {
        final ManagedGraph managedGraph = new ManagedGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(TEST_HAME, RegistrationPackageTest.class));
        final OsfService osfService = factory.getOsfService(OsfService.class);
        final String registrationId = "eq7a4";
        final String childRegistrationId = "vae86";
        final String registeredByUserId = "qmdz6";
        final String registeredFromNodeId = "3e7rd";
        final String contributorUserId = "bwgcm";
        final String registrationStorageProviderId = registrationId + "_osfstorage";
        final String childRegistrationStorageProviderId = childRegistrationId + "_osfstorage";

        // Step one: retrieve all of the objects that we want to serialize into the package:
        //   - Registration
        //   - Users of Contributors
        //
        //   Due to the design of the OSF Java model, most fields of the Registration class are retrieved by
        //   value.  However, the Contributor class references the User by a String id, not by value.  So that is
        //   why we need to retrieve the Users of Contributors.

        //   Retrieve the registration being packaged.  Perform sanity checks on the Java fields of the registration,
        //   insuring that the JSON presented by the OSF v2 API was deserialized properly into Java objects.
        final Registration registration = osfService.registration(registrationId).execute().body();
        assertNotNull(registration);
        assertNotNull(registration.getLicense());
        assertNotNull(registration.getContributors());
        assertFalse(registration.getContributors().isEmpty());

        //  Verify the existence of the single child registration.
        assertNotNull(registration.getChildren());
        final Registration childRegistration = registration.getChildren().stream()
                .filter(r -> r.getId().equals(childRegistrationId))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Missing expected child registration " + childRegistrationId));
        assertTrue(childRegistration.getParent().endsWith(registrationId + "/"));
        assertTrue(childRegistration.getRoot().endsWith(registrationId + "/"));

        //   Collect users from contributors.  Contributors only refer to their users by reference, so we
        //   have to contact the OSF API to retrieve the referenced users.
        final List<Contributor> contributors = registration.getContributors();
        final List<User> users = contributors.stream()
                .map(c -> {
                    try {
                        return osfService.user(c.getId()).execute().body();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());
        assertNotNull(users);
        assertFalse(users.isEmpty());
        //  TODO: handle contributors that do not have a user
        assertEquals(contributors.size(), users.size());

        // Verify presence of File providers from all registration nodes
        final List<File> providers = registration.getFiles();
        assertNotNull(providers);
        assertTrue(providers.size() > 0);

        // Step 2: process the OWL-related annotations on each object, which will
        // produce RDF that is captured in the PackageGraph

        final AnnotationsProcessor ap = new AnnotationsProcessor(managedGraph);

        // Process the OWL annotations on the Registration, its super classes, and its fields
        final Map<String, Individual> createdIndividuals = ap.process(registration);

        // Process the OWL annotations on the User, its super classes, and its fields
        users.forEach(ap::process);

        // Write the individuals to stderr for debugging
//        writeModel(onlyIndividuals(ontologyManager.getOntModel()));


        // Step 3: Verify that the expected OWL individuals are present in the model

        //   the Individual for the Registration
        final Individual registrationIndividual = ontologyManager.getOntModel().getIndividual(registrationId);
        assertNotNull(registrationIndividual);
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_CATEGORY, Category.PROJECT.name());
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_CHILD, asResource(childRegistrationId));
        // TODO double-check timezone and conversion from JodaTime to Calendar
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_DATECREATED, "2016-06-03T17:53:52.434Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_DATEMODIFIED, "2016-06-07T17:52:19.617Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_DATEREGISTERED, "2016-06-07T17:53:10.603Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_DESCRIPTION, "Test project Two.");
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT, "Open-Ended Registration");
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_ROOT, asResource(registrationId));
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_TITLE, "Project Two");
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_COLLECTION, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_FORK, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_PUBLIC, "true", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_REGISTRATION, "true", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(registrationIndividual,
                OwlProperties.OSF_IS_WITHDRAWN, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_REGISTERED_BY, asResource(registeredByUserId));
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_REGISTERED_FROM, asResource(registeredFromNodeId));
        testUtil.assertHasPropertyWithValue(registrationIndividual,
                OwlProperties.OSF_HAS_HASPROVIDER, asResource(registrationStorageProviderId));
        final Set<RDFNode> tags = registrationIndividual.listPropertyValues(
                testUtil.asProperty(OwlProperties.OSF_HAS_TAG)).toSet();
        assertEquals(1, tags.size());
        assertTrue(tags.contains(ResourceFactory.createPlainLiteral("newtag")));

        //   the Individual for the Child Registration
        final Individual childRegistrationIndividual = ontologyManager.getOntModel().getIndividual(childRegistrationId);
        assertNotNull(childRegistrationIndividual);
        testUtil.assertHasPropertyWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_CATEGORY, Category.DATA.name());
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_DATECREATED, "2016-06-07T14:46:14.778Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_DATEMODIFIED, "2016-06-07T17:47:39.006Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_DATEREGISTERED, "2016-06-07T17:53:10.766Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasPropertyWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_REGISTRATIONSUPPLEMENT, "Open-Ended Registration");
        // TODO: /root/ relationship is missing from the parent registration's /children/ relationship
        // testUtil.assertHasPropertyWithValue(
        // childRegistrationIndividual, OwlProperties.OSF_HAS_ROOT, asResource(registrationId));
        testUtil.assertHasPropertyWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_TITLE, "Raw Experimental Data");
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_COLLECTION, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_FORK, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_PENDINGEMBARGOAPPROVAL, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_PENDINGREGISTRATIONAPPROVAL, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_PUBLIC, "true", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_REGISTRATION, "true", XSDDatatype.XSDboolean);
        testUtil.assertHasTypedLiteralWithValue(childRegistrationIndividual,
                OwlProperties.OSF_IS_WITHDRAWN, "false", XSDDatatype.XSDboolean);
        testUtil.assertHasPropertyWithValue(childRegistrationIndividual,
                OwlProperties.OSF_HAS_HASPROVIDER, asResource(childRegistrationStorageProviderId));

        // TODO: confirm that registered_by and registered_from are not attributes on child registrations
//        testUtil.assertHasPropertyWithValue(childRegistrationIndividual, OwlProperties.OSF_REGISTERED_BY,
// asResource(registeredByUserId));
//        testUtil.assertHasPropertyWithValue(childRegistrationIndividual, OwlProperties.OSF_REGISTERED_FROM,
// asResource(registeredFromNodeId));

        //   the Individuals for the Users; each Java User instance should have an OWL Individual
        final List<Individual> userIndividuals = users.stream().map(User::getId).map(
                ontologyManager::individual).collect(Collectors.toList());
        assertEquals(users.size(), userIndividuals.size());

        final Individual registeredByUser = userIndividuals.stream()
                .filter(candidate -> candidate.getURI().equals(registeredByUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing expected user " + registeredByUserId));

        final Individual contributorUser = userIndividuals.stream()
                .filter(candidate -> candidate.getURI().equals(contributorUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Missing expected user " + contributorUserId));

        // TODO map github in java model
        // TODO: not sure about these empty strings...
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_BAIDUID, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_TWITTER, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_IMPACTSTORY, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_MIDDLENAMES, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_PERSONALWEBSITE, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_RESEARCHGATE, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_RESEARCHERID, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_SUFFIX, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_LINKEDIN, "");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_SCHOLAR, "");
        testUtil.assertHasTypedLiteralWithValue(registeredByUser,
                OwlProperties.OSF_HAS_DATEUSERREGISTERED, "2016-06-03T17:52:35.4Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_FULLNAME, "Elliot Metsger");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_GIVENNAME, "Elliot");
        testUtil.assertHasPropertyWithValue(registeredByUser, OwlProperties.OSF_HAS_LOCALE, "en_US");
        testUtil.assertHasTypedLiteralWithValue(registeredByUser,
                OwlProperties.OSF_IS_ACTIVE, "true", XSDDatatype.XSDboolean);

        testUtil.assertHasTypedLiteralWithValue(contributorUser,
                OwlProperties.OSF_HAS_DATEUSERREGISTERED, "2016-06-03T18:00:16.559Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_BAIDUID, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_TWITTER, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_IMPACTSTORY, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_MIDDLENAMES, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_PERSONALWEBSITE, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_RESEARCHGATE, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_RESEARCHERID, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_SUFFIX, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_LINKEDIN, "");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_SCHOLAR, "");
        testUtil.assertHasTypedLiteralWithValue(contributorUser,
                OwlProperties.OSF_HAS_DATEUSERREGISTERED, "2016-06-03T18:00:16.559Z", XSDDatatype.XSDdateTime);
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_FULLNAME, "JHU Emetsger");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_GIVENNAME, "JHU");
        testUtil.assertHasPropertyWithValue(contributorUser, OwlProperties.OSF_HAS_LOCALE, "en_US");
        testUtil.assertHasTypedLiteralWithValue(contributorUser,
                OwlProperties.OSF_IS_ACTIVE, "true", XSDDatatype.XSDboolean);

        //   the anonymous Individuals for the Contributors.  Jena doesn't let you retrieve instances of Individual for
        //   anonymous individuals.
        final Set<RDFNode> contributorNodes = registrationIndividual.listPropertyValues(
                testUtil.asProperty(OwlProperties.OSF_HAS_CONTRIBUTOR)).toSet();
        assertNotNull(contributorNodes);
        assertEquals(2, contributorNodes.size());
        contributorNodes.stream().forEach(contributorIndividual -> {
            assertFalse(contributorIndividual.isAnon());
            assertTrue(contributorIndividual.asResource().getURI().contains("#contributor"));
        });
        final RDFNode registeredByContributorNode = contributorNodes.stream()
                .filter(candidate -> candidate.asResource().hasProperty(testUtil.asProperty(OwlProperties.OSF_HAS_USER),
                        asResource(registeredByUserId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing expected contributor for user " + registeredByUserId));
        final RDFNode contributorNode = contributorNodes.stream()
                .filter(candidate -> candidate.asResource().hasProperty(testUtil.asProperty(OwlProperties.OSF_HAS_USER),
                        asResource(contributorUserId)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Missing expected contributor for user " + contributorUserId));

        assertTrue(contributorNode.asResource().hasLiteral(
                testUtil.asProperty(OwlProperties.OSF_IS_BIBLIOGRAPHIC), true));
        assertTrue(contributorNode.asResource().hasLiteral(
                testUtil.asProperty(OwlProperties.OSF_HAS_PERMISSION), "ADMIN"));
        assertTrue(registeredByContributorNode.asResource().hasLiteral(
                testUtil.asProperty(OwlProperties.OSF_IS_BIBLIOGRAPHIC), false));
        assertTrue(registeredByContributorNode.asResource().hasLiteral(
                testUtil.asProperty(OwlProperties.OSF_HAS_PERMISSION), "ADMIN"));

        //   the anonymous Individuals for License information
        final Set<RDFNode> licenseNodes = registrationIndividual.listPropertyValues(
                testUtil.asProperty(OwlProperties.OSF_HAS_LICENSE)).toSet();
        assertEquals(1, licenseNodes.size());
        final RDFNode license = licenseNodes.iterator().next();
        assertFalse(license.isAnon());
        assertTrue(license.asResource().getURI().contains("#license"));
        assertTrue(license.asResource().hasProperty(testUtil.asProperty(OwlProperties.OSF_HAS_LICENSE_NAME)));
        assertTrue(license.asResource().hasProperty(testUtil.asProperty(OwlProperties.OSF_HAS_LICENSE_TEXT)));
        assertTrue(license.asResource().hasLiteral(
                testUtil.asProperty(OwlProperties.OSF_HAS_LICENSE_NAME), "CC-By Attribution 4.0 International"));
        assertTrue(license.asResource().getProperty(
                testUtil.asProperty(OwlProperties.OSF_HAS_LICENSE_TEXT)).getLiteral().getString()
                    .startsWith("Creative Commons Attribution 4.0 International Public License"));

        //   the storage providers
        final Individual registrationStorageProvider = ontologyManager.individual(registrationStorageProviderId);
        final Individual childStorageProvider = ontologyManager.individual(childRegistrationStorageProviderId);
        final Set<Individual> providersByClass = ontologyManager.getOntModel().listIndividuals(
                ontologyManager.owlClass(OwlClasses.OSF_PROVIDER.ns(), OwlClasses.OSF_PROVIDER.localname())).toSet();
        assertNotNull(registrationStorageProvider);
        assertNotNull(childStorageProvider);
        assertNotNull(providersByClass);
        assertEquals(2, providersByClass.size());
        assertTrue(providersByClass.stream().anyMatch(candidateProvider ->
                candidateProvider.getURI().equals(registrationStorageProviderId)));
        assertTrue(providersByClass.stream().anyMatch(candidateProvider ->
                candidateProvider.getURI().equals(childRegistrationStorageProviderId)));

        assertTrue(ontologyManager.individual(registrationId).hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertFalse(ontologyManager.individual(registrationId).hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertFalse(ontologyManager.individual(registrationId).hasOntClass(OwlClasses.OSF_CONTRIBUTOR.fqname()));

        assertTrue(ontologyManager.individual(childRegistrationId).hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));
        assertFalse(ontologyManager.individual(childRegistrationId).hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertFalse(ontologyManager.individual(childRegistrationId).hasOntClass(OwlClasses.OSF_CONTRIBUTOR.fqname()));

        assertTrue(ontologyManager.individual(registeredByUserId).hasOntClass(OwlClasses.OSF_USER.fqname()));
        assertFalse(ontologyManager.individual(registeredByUserId).hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));

        assertTrue(ontologyManager.individual(registeredFromNodeId).hasOntClass(OwlClasses.OSF_NODE.fqname()));
        assertFalse(ontologyManager.individual(registeredFromNodeId).hasOntClass(OwlClasses.OSF_REGISTRATION.fqname()));

    }

    @Test
    public void testGetAnnotations() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_HAME, RegistrationPackageTest.class));
        final Registration r = factory.getOsfService(OsfService.class).registration("y6cx7").execute().body();
        assertNotNull(r);

        final AnnotatedElementPairMap<AnnotatedElementPair, AnnotationAttributes> result =
                new AnnotatedElementPairMap<>();
        OwlAnnotationProcessor.getAnnotationsForInstance(r, result);
        // Move of RegistrationMeta to a Map<String, RegistrationMeta> "hides" the RegistrationMeta class from
        // annotation processing.
        // TODO: https://github.com/DataConservancy/dcs-packaging-osf/issues/4
        assertEquals(89, result.size());

        final AnnotatedElementPair aep1 = new AnnotatedElementPair(r.getClass(), OwlIndividual.class);
        final AnnotatedElementPair aep2 = new AnnotatedElementPair(r.getClass(), OwlIndividual.class);
        assertEquals(aep1, aep2);

        final AnnotationAttributes attribs = result.get(AnnotatedElementPair.forPair(
                r.getClass(), OwlIndividual.class));
        assertNotNull(attribs);
        assertEquals(OwlClasses.OSF_REGISTRATION, attribs.getEnum("value"));

    }

    /**
     * A streamlined test which creates a RDF graph consisting of a single registration with a single wiki page.
     *
     * @throws Exception
     */
    @Test
    public void testGenerateWikiAnnotations() throws Exception {
        final Node n = new Node();
        n.setId("u9dc7");

        final User u = new User();
        u.setId("9m8ky");
        u.setFamily_name("Metsger");
        u.setGiven_name("Elliot");
        u.setFull_name("Elliot Metsger");

        final Wiki w = new Wiki();
        w.setId("hgkfe");
        w.setUser(u);
        w.setDate_modified("2016-09-15T14:19:14.417000");
        w.setContent_type("text/markdown");
        w.setNode(n.getId());
        w.setKind("file");
        w.setPath("/hgkfe");
        w.setMaterialized_path("/hgkfe");
        w.setSize(224);
        w.setName("home");
        final HashMap<String, String> links = new HashMap<>();
        links.put("download", "https://test-api.osf.io/v2/wikis/hgkfe/content/");
        links.put("info", "https://test-api.osf.io/v2/wikis/hgkfe/");
        links.put("self", "https://test-api.osf.io/v2/wikis/hgkfe/");
        w.setLinks(links);
        final HashMap<String, String> extra = new HashMap<>();
        extra.put("version", "3");
        w.setExtra(extra);

        final Registration r = new Registration();
        r.setId("registration");
        r.setWikis(Collections.singletonList(w));

        final ManagedGraph managedGraph = new ManagedGraph(ontologyManager);
        final AnnotationsProcessor ap = new AnnotationsProcessor(managedGraph);

        // Process the OWL annotations on the Registration, its super classes, and its fields
        final Map<String, Individual> createdIndividuals = ap.process(r);

        // Write the individuals to stderr for debugging
//        writeModel(onlyIndividuals(ontologyManager.getOntModel()));
    }

    private Map<Field, AnnotationAttributes> getFieldAnnotationAttribute(final Registration registration,
                                                                         final List<Field> annotatedFields,
                                                                         final Class<? extends Annotation> annotation) {
        final Map<Field, AnnotationAttributes> fieldAnnotationAttrs = new HashMap<>();
        doWithFields(registration.getClass(),
                f -> {
                    f.setAccessible(true);
                    annotatedFields.add(f);
                    final AnnotationAttributes annotationAttributes =
                            AnnotationUtils.getAnnotationAttributes(f, f.getAnnotation(annotation));
                    fieldAnnotationAttrs.put(f, annotationAttributes);
                },
                f -> f.getDeclaredAnnotation(annotation) != null);
        return fieldAnnotationAttrs;
    }

    private boolean isCollection(final Class<?> candidate) {
        return Collection.class.isAssignableFrom(candidate);
    }

    private boolean isArray(final Class<?> candidate) {
        return candidate.isArray();
    }

    private void writeModel(final Model m) {
        System.err.println();
        System.err.println("---");
        RDFDataMgr.write(System.err, m, RDFFormat.TURTLE_PRETTY);
        System.err.println("---");
        System.err.println();
    }

    private Model onlyIndividuals(final OntModel m) {
        final Model individuals = ModelFactory.createDefaultModel();
        individuals.setNsPrefixes(Rdf.Ns.PREFIXES);

        m.listIndividuals().forEachRemaining(i -> individuals.add(i.listProperties()));
        return individuals;
    }

}
