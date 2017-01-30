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

import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.retrofit.OsfService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Various tests against model classes:
 * <ul>
 *     <ol>Insuring that the JSON response documents from the OSF v2 API are properly deserialized into
 *         Java classes.</ol>
 *     <ol>Insuring that relationships represented in JSON response documents are properly navigated</ol>
 * </ul>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class NodeTest extends AbstractMockServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(NodeTest.class);

    private String baseUri = getBaseUri().toString();

    @Rule
    public TestName TEST_NAME = new TestName();

    /**
     * This is a basic sanity test, insuring a JSON response document is properly mapped into a Node.
     * <p>
     * Retrieves a JSON document containing a node that has no relationships with
     * {@link com.github.jasminb.jsonapi.ResolutionStrategy#OBJECT} (which makes it easier in terms of setting HTTP
     * expectations; there are no subsequent requests to retrieve additional objects).
     * </p>
     *
     * @throws Exception
     */
    @Test
    public void testGetNodeWithNoObjectResolution() throws Exception {
        factory.interceptors().add((chain) -> chain.proceed(
                chain.request().newBuilder().addHeader(X_RESPONSE_RESOURCE, "project-node-only-ref-rels.json").build()
        ));

        final Node n = factory.getOsfService(OsfService.class).nodeById("v8x57").execute().body();
        assertNotNull(n);

        // These fields are null because they are not in the json response document
        assertNull(n.getContributors());
        assertNull(n.getChildren());
        assertNull(n.getFiles());

        // The rest of the fields should be present
        assertEquals(baseUri + "nodes/r5s4u/", n.getRoot());
        assertEquals(baseUri + "nodes/r5s4u/", n.getParent());
        assertEquals(baseUri + "nodes/v8x57/registrations/", n.getRegistrations());
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
        // Note: not equal to '2016-05-10T13:53:07.886000Z' because we normalize date times with Joda
        assertEquals("2016-05-10T13:53:07.886Z", n.getDate_modified());
        assertEquals("2016-04-19T13:08:24.039Z", n.getDate_created());
    }

    /**
     * Insures that relationships present in the JSON response documents are properly navigated, specifically those
     * using {@code ResolutionStrategy#OBJECT}.
     *
     * @throws Exception
     */
    @Test
    public void testGetNodeObjectResolution() throws Exception {

        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, NodeTest.class));

        final Node n = factory.getOsfService(OsfService.class).nodeById("v8x57").execute().body();
        assertNotNull(n);

        // Only test the relationships that are object references; string refs and fields were tested earlier

        // Children
        assertTrue(n.getChildren().isEmpty());

        // Contributors
        assertEquals(1, n.getContributors().size());
        final Contributor c = n.getContributors().get(0);
        assertEquals("a3q2g", c.getId());
        assertTrue(c.isBibliographic());
        assertNotNull(c.getPermission());
        assertEquals(Permission.ADMIN, c.getPermission());

        // Files
        assertEquals(1, n.getFiles().size());
        // The fields in a File are covered in another test; we just verify the hierarchy is what we expect
        final File storageProvider = n.getFiles().get(0);
        assertEquals("v8x57:osfstorage", storageProvider.getId());
        assertEquals(2, storageProvider.getFiles().size());

        assertFilesContainsName("moo", storageProvider.getFiles());
        assertFilesContainsName("porsche.jpg", storageProvider.getFiles());

        // TODO store files in a Map keyed by name?

        final File f = getFile("porsche.jpg", storageProvider.getFiles());
        assertNotNull(f.getLinks());
        assertEquals("http://localhost:7777/v1/resources/v8x57/providers/osfstorage/5716311dcfa27c0045ec7cab",
                f.getLinks().get("download"));



        // TODO remainder of file hierarchy.
    }

    /**
     * Tests a structure with a top-level project containing a sub-project, which contains a file.
     * Retrieving the top-level project will recursively retrieve the sub-project's file.
     *
     * @throws Exception
     */
    @Test
    public void testGetSubProjectFileFromParent() throws Exception {
        final String topLevel = "jp4tk";
        final String sub = "pd24n";
        final String fileName = "porsche.jpg";

        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, NodeTest.class));

        final Node topNode = factory.getOsfService(OsfService.class).nodeById(topLevel).execute().body();
        assertNotNull(topNode);

        // the top node has only one file, the osfstorage provider.
        assertEquals(1, topNode.getFiles().size());
        assertEquals("osfstorage", topNode.getFiles().get(0).getName());
        assertEquals("osfstorage", topNode.getFiles().get(0).getProvider());
        assertEquals("/", topNode.getFiles().get(0).getPath());

        // the osfstorage provider contains no files or directories
        assertEquals(0, topNode.getFiles().get(0).getFiles().size());

        // There's one child
        assertEquals(1, topNode.getChildren().size());
        final Node subNode = topNode.getChildren().get(0);
        assertNotNull(subNode);
        assertEquals(sub, subNode.getId());

        // It has one storage provider
        assertEquals(1, subNode.getFiles().size());
        assertEquals("osfstorage", subNode.getFiles().get(0).getName());
        assertEquals("osfstorage", subNode.getFiles().get(0).getProvider());
        assertEquals("/", subNode.getFiles().get(0).getPath());

        // the osfstorage provider contains one file
        assertEquals(1, subNode.getFiles().get(0).getFiles().size());
        assertEquals(fileName, subNode.getFiles().get(0).getFiles().get(0).getName());
    }

    @Test
    public void testDownloadFile() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, NodeTest.class));

        final OsfService osfService = factory.getOsfService(OsfService.class);
        final Node nodeWithFile = osfService.nodeById("pd24n").execute().body();
        assertNotNull(nodeWithFile);
        final File osfProvider = nodeWithFile.getFiles().get(0);
        final File binary = osfProvider.getFiles().get(0);
        assertNotNull(binary);
        assertEquals("porsche.jpg", binary.getName());

        final String downloadUrl = (String)binary.getLinks().get("download");
        assertNotNull(downloadUrl);

        final InputStream response = osfService.stream(downloadUrl).execute().body().byteStream();
        final byte[] content = IOUtils.toByteArray(response);
        assertNotNull(content);
        assertEquals(Long.valueOf(binary.getSize()), Long.valueOf(content.length));
    }

    /**
     * Asserts that the supplied {@code files} collection contains at least one {@code File} with the supplied
     * {@code name}.
     *
     * @param name the name of the file to check for
     * @param files a collection of File objects
     */
    private static void assertFilesContainsName(final String name, final Collection<File> files) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return;
            }
        }

        fail("Expected file named '" + name + "' was not found.");
    }

    private File getFile(final String name, final Collection<File> files) {
        for (File f : files) {
            if (f.getName().equals(name)) {
                return f;
            }
        }

        fail("Expected file named '" + name + "' was not found.");
        return null;
    }

}
