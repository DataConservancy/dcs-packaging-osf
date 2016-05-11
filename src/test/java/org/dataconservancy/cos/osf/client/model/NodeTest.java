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
package org.dataconservancy.cos.osf.client.model;

import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.squareup.okhttp.Request;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mockserver.client.server.MockServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockserver.model.HttpCallback.callback;
import static org.mockserver.model.HttpRequest.request;

/**
 * Various tests against model classes:
 * <ul>
 *     <ol>Insuring that the JSON response documents from the OSF v2 API are properly deserialized into Java classes.</ol>
 *     <ol>Insuring that relationships represented in JSON response documents are properly navigated</ol>
 * </ul>
 */
public class NodeTest extends AbstractMockServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(NodeTest.class);

    /**
     * Base path of the file system hierarchy containing JSON response documents
     */
    private static final String JSON_ROOT = "/json/";

    /**
     * Set by {@link AbstractMockServerTest#mockServerRule}.  Due to the way Mock Server 3.10.4 works, this
     * field can't be put in a superclass.
     */
    private MockServerClient mockServer;

    private String baseUri = getBaseUri().toString();

    @Rule
    public TestName testName = new TestName();

    /**
     * Sets up the expectations of the mock http server.
     */
    @Before
    public void setUpExpectations() {

        /*
         * Invokes the NodeResponseCallback when the HTTP header "X-Response-Resource" is present.
         * The header value is a classpath resource to the JSON document to be serialized for the
         * response.
         */
        mockServer.when(
                request()
                        .withHeader(X_RESPONSE_RESOURCE)
        )
                .callback(
                        callback()
                                .withCallbackClass(NodeResponseCallback.class.getName())
                );
    }

    /**
     * This is a basic sanity test, insuring a JSON response document is properly mapped into a Node.
     * <p>
     * Retrieves a JSON document containing a node that has no relationships with {@link ResolutionStrategy#OBJECT}
     * (which makes it easier in terms of setting HTTP expectations; there are no subsequent requests to retrieve
     * additional objects).
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testGetNodeWithNoObjectResolution() throws Exception {
        factory.interceptors().add((chain) -> chain.proceed(
                chain.request().newBuilder().addHeader(X_RESPONSE_RESOURCE, "project-node-only-ref-rels.json").build()
        ));

        Node n = factory.getOsfService(OsfService.class).node("v8x57").execute().body();
        assertNotNull(n);

        // These fields are null because they are not in the json response document
        assertNull(n.getContributors());
        assertNull(n.getChildren());
        assertNull(n.getFiles());

        // The rest of the fields should be present
        assertEquals(baseUri + "nodes/r5s4u/", n.getRoot());
        assertEquals(baseUri + "nodes/r5s4u/", n.getParent());
        assertEquals(baseUri + "nodes/v8x57/registrations/", n.getRegistrations());
        assertEquals(baseUri + "nodes/v8x57/node_links/", n.getNode_links());
        assertEquals(baseUri + "nodes/v8x57/logs/", n.getLogs());
        assertEquals(Category.DATA, n.getCategory());
        assertNull(n.getDescription());
        assertEquals("Raw Data", n.getTitle());
        assertNull(n.getForked_from());
        assertFalse(n.isFork());
        assertFalse(n.isCollection());
        assertFalse(n.isRegistration());
        assertFalse(n.isPublic());
        assertTrue(n.getTags().isEmpty());
        assertEquals("v8x57", n.getId());
        assertNotNull(n.getLinks());
        assertEquals(2, n.getLinks().size());
        assertEquals(baseUri + "nodes/v8x57/", n.getLinks().get("self"));
        assertEquals("http://localhost:5000/v8x57/", n.getLinks().get("html"));
        assertNull(n.getPageLinks());
        assertEquals("2016-05-10T13:53:07.886000", n.getDate_modified());
        assertEquals("2016-04-19T13:08:24.039000", n.getDate_created());
    }

    /**
     * Insures that relationships present in the JSON response documents are properly navigated, specifically those
     * using {@link ResolutionStrategy#OBJECT}.
     *
     * @throws Exception
     */
    @Test
    public void testGetNodeObjectResolution() throws Exception {

        factory.interceptors().add((chain) -> {
            Request req = chain.request();
            LOG.debug("HTTP request: {}", req.urlString());

            // Resolve the request URI to a path on the filesystem.
            Path resourcePath = resolveResponseResource(testName, getConfiguration().getBaseUri(), req.uri());
            LOG.debug("Response resource: {}", resourcePath.toString());

            req = req.newBuilder().addHeader(X_RESPONSE_RESOURCE, resourcePath.toString()).build();

            return chain.proceed(req);
        });

        Node n = factory.getOsfService(OsfService.class).node("v8x57").execute().body();
        assertNotNull(n);

        // Only test the relationships that are object references; string refs and fields were tested earlier

        // Children
        assertTrue(n.getChildren().isEmpty());

        // Contributors
        assertEquals(1, n.getContributors().size());
        Contributor c = n.getContributors().get(0);
        assertEquals("a3q2g", c.getId());
        assertTrue(c.isBibliographic());
        assertNotNull(c.getPermission());
        assertEquals(Permission.ADMIN, c.getPermission());

        // Files
        assertEquals(1, n.getFiles().size());
        // The fields in a File are covered in another test; we just verify the hierarchy is what we expect
        File storageProvider = n.getFiles().get(0);
        assertEquals("v8x57:osfstorage", storageProvider.getId());
        assertEquals(2, storageProvider.getFiles().size());

        assertFilesContainsName("moo", storageProvider.getFiles());
        assertFilesContainsName("porsche.jpg", storageProvider.getFiles());

        // TODO remainder of file hierarchy.
    }

    private static URI relativize(URI baseUri, URI requestUri) {
        URI result = baseUri.relativize(requestUri);
        LOG.trace("Relativizing {} against {}: {}", baseUri, requestUri, result);
        return result;
    }

    private static String resourceBase(TestName testName) {
        assertNotNull(testName);
        StringBuilder base = new StringBuilder(JSON_ROOT);
        base.append(NodeTest.class.getSimpleName()).append("/");
        base.append(testName.getMethodName()).append("/");

        LOG.trace("Test resource base path: {}", base);
        return base.toString();
    }

    /**
     * Responsible for resolving the JSON response resource.
     *
     * @param test contains metadata about the current test method.
     * @param baseUri the baseUri of the OSF V2 API
     * @param requestUri the full request URI
     * @return a Path that identifies a classpath resource containing the JSON response document
     */
    private static Path resolveResponseResource(TestName test, URI baseUri, URI requestUri) {
        // http://localhost:8000/v2/nodes/v8x57/files/osfstorage/ -> nodes/v8x57/files/osfstorage/
        URI relativizedRequestUri = baseUri.relativize(requestUri);
        Path requestPath = Paths.get(relativizedRequestUri.getPath());

        // /json/NodeTest/testGetNodeObjectResolution/
        Path fsBase = Paths.get(resourceBase(test));

        // /json/NodeTest/testGetNodeObjectResolution/nodes/v8x57/files/osfstorage/index.json
        Path resolvedPath = Paths.get(fsBase.toString(), requestPath.toString(), "index.json");

        return resolvedPath;
    }

    /**
     * Asserts that the supplied {@code files} collection contains at least one {@code File} with the supplied
     * {@code name}.
     *
     * @param name the name of the file to check for
     * @param files a collection of File objects
     */
    private static void assertFilesContainsName(String name, Collection<File> files) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return;
            }
        }

        fail("Expected file named '" + name + "' was not found.");
    }
}
