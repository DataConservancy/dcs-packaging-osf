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

import org.apache.jena.ontology.Individual;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.Comment;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.Wiki;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.rdf.support.OntologyManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.jena.datatypes.xsd.XSDDatatype.XSDanyURI;
import static org.apache.jena.datatypes.xsd.XSDDatatype.XSDint;
import static org.apache.jena.rdf.model.ResourceFactory.createResource;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_COMMENT;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_COMMENT_TARGET;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_FILE;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_NODE;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_NODEBASE;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_REGISTRATION;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_USER;
import static org.dataconservancy.cos.rdf.support.OwlClasses.OSF_WIKI;
import static org.dataconservancy.cos.rdf.support.OwlProperties.DCTERMS_IDENTIFIER;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_AUTHORED_BY;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_BINARYURI;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_COMMENT;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_CONTENT;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_CONTENTTYPE;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_FULLNAME;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_GIVENNAME;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_HASFAMILYNAME;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_HASKIND;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_ID;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_MATERIALIZEDPATH;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_NAME;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_NODE;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_PATH;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_SIZE;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_HAS_WIKI;
import static org.dataconservancy.cos.rdf.support.OwlProperties.OSF_IN_REPLY_TO;
import static org.junit.Assert.assertNotNull;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class OsfPackageGraphTest extends AbstractMockServerTest {

    private String baseUri = getBaseUri().toString();

    private OntologyManager ontologyManager = new OntologyManager();

    @Rule
    public TestName TEST_NAME = new TestName();

    @Test
    public void testCreatePackageGraph() throws Exception {
        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, OsfPackageGraphTest.class));
        final OsfService osfService = factory.getOsfService(OsfService.class);
        final String registrationId = "eq7a4";

        final Registration registration = osfService.registration(registrationId).execute().body();
        final List<User> users = registration.getContributors().stream()
                .map(c -> {
                    try {
                        return osfService.user(c.getId()).execute().body();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());

        packageGraph.add(registration);
        users.forEach(packageGraph::add);

//        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

    }

    /**
     * A streamlined test (e.g. doesn't use {@code TestingOsfServiceFactory}) which creates a RDF graph consisting of a
     * single registration with a single wiki page.
     *
     * @throws Exception
     */
    @Test
    public void testGenerateWikiAnnotations() throws Exception {

        final String wikiId = "hgkfe";
        final String wikiUser = "9m8ky";
        final String content_type = "text/markdown";
        final String nodeGuid = "u9dc7";
        final String node = "https://test-api.osf.io/v2/nodes/" + nodeGuid + "/";
        final String kind = "file";
        final String filePath = "/hgkfe";
        final int size = 224;
        final String name = "home";
        final String binaryUri = "https://test-api.osf.io/v2/wikis/hgkfe/content/";

        final Node n = new Node();
        n.setId(nodeGuid);

        final User u = new User();
        u.setId("9m8ky");
        u.setFamily_name("Metsger");
        u.setGiven_name("Elliot");
        u.setFull_name("Elliot Metsger");

        final Wiki w = new Wiki();
        w.setId(wikiId);
        w.setUser(u);
        w.setDate_modified("2016-09-15T14:19:14.417000");
        w.setContent_type(content_type);
        w.setNode(n.getId());
        w.setKind(kind);
        w.setPath(filePath);
        w.setMaterialized_path(filePath);
        w.setSize(size);
        w.setName(name);
        final HashMap<String, String> links = new HashMap<>();
        links.put("download", binaryUri);
        links.put("info", "https://test-api.osf.io/v2/wikis/hgkfe/");
        links.put("self", "https://test-api.osf.io/v2/wikis/hgkfe/");
        w.setLinks(links);
        final HashMap<String, String> extra = new HashMap<>();
        extra.put("version", "3");
        w.setExtra(extra);

        final Comment c = new Comment();
        c.setId("comment");
        c.setContent("This is my comment on the wiki!");
        c.setNode(n);
        c.setPage("wiki");
        c.setTarget(links.get("self")); // target is the "self" url of the wiki
        w.setComments(Collections.singletonList(c));

        final Registration r = new Registration();
        r.setId("registration");
        r.setWikis(Collections.singletonList(w));

        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        packageGraph.add(r);
        packageGraph.add(u);

//        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

        final Individual wikiIndividual = ontologyManager.individual(w.getId());
        final Individual userIndividual = ontologyManager.individual(u.getId());
        final Individual registrationIndividual = ontologyManager.individual(r.getId());
        final Individual nodeIndividual = ontologyManager.individual(n.getId());
        final Individual commentIndividual = ontologyManager.individual(c.getId());

        assertNotNull(wikiIndividual);
        assertNotNull(userIndividual);
        assertNotNull(registrationIndividual);
        assertNotNull(nodeIndividual);
        assertNotNull(commentIndividual);

        final RdfTestUtil testUtil = new RdfTestUtil(ontologyManager);

        testUtil.assertIsType(wikiIndividual, OSF_WIKI.fqname(), OSF_FILE.fqname(), OSF_COMMENT_TARGET.fqname());
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_CONTENTTYPE, content_type);
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_HASKIND, kind);
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_PATH, filePath);
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_MATERIALIZEDPATH, filePath);
        testUtil.assertHasTypedLiteralWithValue(wikiIndividual, OSF_HAS_SIZE, String.valueOf(size), XSDint);
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_NAME, name);
        testUtil.assertHasTypedLiteralWithValue(wikiIndividual, OSF_HAS_BINARYURI, binaryUri, XSDanyURI);
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_AUTHORED_BY, createResource(u.getId()));
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_NODE, createResource(nodeGuid));
        testUtil.assertHasPropertyWithValue(wikiIndividual, OSF_HAS_COMMENT, createResource(c.getId()));

        testUtil.assertIsType(userIndividual, OSF_USER.fqname());
        testUtil.assertHasPropertyWithValue(userIndividual, OSF_HAS_HASFAMILYNAME, "Metsger");
        testUtil.assertHasPropertyWithValue(userIndividual, OSF_HAS_GIVENNAME, "Elliot");
        testUtil.assertHasPropertyWithValue(userIndividual, OSF_HAS_FULLNAME, "Elliot Metsger");

        testUtil.assertIsType(registrationIndividual, OSF_REGISTRATION.fqname(), OSF_NODEBASE.fqname());
        testUtil.assertHasPropertyWithValue(registrationIndividual, OSF_HAS_ID, "registration");
        testUtil.assertHasPropertyWithValue(registrationIndividual, OSF_HAS_WIKI, createResource(wikiId));
        testUtil.assertHasPropertyWithValue(registrationIndividual, DCTERMS_IDENTIFIER, r.getId());

        testUtil.assertIsType(nodeIndividual, OSF_NODE.fqname());

        testUtil.assertIsType(commentIndividual, OSF_COMMENT.fqname());
        testUtil.assertHasTypedLiteralWithValue(commentIndividual, OSF_IN_REPLY_TO, c.getTarget(), XSDanyURI);
        testUtil.assertHasPropertyWithValue(commentIndividual, OSF_HAS_NODE, createResource(n.getId()));
        testUtil.assertHasPropertyWithValue(commentIndividual, OSF_HAS_CONTENT, c.getContent());
    }
}