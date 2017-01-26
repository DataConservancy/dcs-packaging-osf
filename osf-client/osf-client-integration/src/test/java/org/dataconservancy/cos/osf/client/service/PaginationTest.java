/*
 *
 *  * Copyright 2017 Johns Hopkins University
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.dataconservancy.cos.osf.client.service;

import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Log;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginationTest extends AbstractMockServerTest {

    private OsfService osfService;

    @Before
    public void setUp() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor("/model-mapping/2.2/graphs/shared/", this.getClass()));
        osfService = factory.getOsfService(OsfService.class);
    }

    /**
     * Demonstrates how paginated results can be navigated when the relationship is returned as a URL (in this test, the
     * 'logs' relationship for a Registration is returned as a URL), not as an object.
     *
     * @throws IOException if the {@code Registration} cannot be retrieved by the test {@code osfService}
     */
    @Test
    public void testPaginationOfRel() throws Exception {
        final Registration r = osfService.registrationByUrl("http://localhost:8000/v2/registrations/tgzhk/")
                .execute().body();

        final String logsRel = r.getLogs();
        final List<Log> resultsPage = osfService.getLogs(logsRel).execute().body();

        // A stream will automatically retrieve pages of results transparently in the background
        assertEquals(20, resultsPage.stream().count());

        assertEquals(20, resultsPage.size());

        // TODO these assertions fail even though the meta section is present in the second page of results.
//        assertEquals(20, resultsPage.size());
//        assertEquals(10, resultsPage.perPage());
//        assertEquals(20, resultsPage.total());
    }

    /**
     * Retrieves a {@code Node}, traverses the {@code files} relationship, insuring that all the files are retrieved in
     * order, despite the fact that the {@code files} relationship is paginated.
     * <p>
     * Because {@code Node} defines a {@link Node#files} as a
     * {@link com.github.jasminb.jsonapi.annotations.Relationship} with a strategy of
     * {@link com.github.jasminb.jsonapi.ResolutionStrategy#OBJECT}, the JSONAPI converter will retrieve all the files
     * associated with the node.
     * </p>
     * @throws IOException if the {@code Node} cannot be retrieved by the test {@code osfService}
     */
    @Test
    public void testPaginationOfResolvedRelationship() throws IOException {
        final Node n = osfService.nodeByUrl("http://localhost:8000/v2/nodes/xmnkz/").execute().body();

        // xmnkz node has a single file provider
        assertEquals(1, n.getFiles().size());
        final File provider = n.getFiles().get(0);
        assertEquals("xmnkz:osfstorage", provider.getId());

        // the xmnkz:osfstorage provider has 11 files across two pages.  The API does not expose the notion of pages
        // when relationships are traversed by the JSONAPI converter (the relationships are traversed by the JSONAPI
        // converter implementation, and are not able to be influenced by the caller), so we cannot demonstrate that two
        // pages were consulted when producing the response.  However, the raw JSON is available on the classpath at
        // localhost/8000/v2/nodes/xmnkz/files/osfstorage/
        final List<File> files = provider.getFiles();
        assertEquals(11, files.size());

        // Also serves to demonstrate that the order is preserved as encounter order in the JSON.
        assertEquals("587fdf4bad3c4800474d7eb3", files.get(0).getId());
        assertEquals("587fdf666c613b0043f2c804", files.get(10).getId());
    }

    @Test
    public void testPaginationWhenResultsFitWithinSinglePage() throws Exception {
        // e.g. a List containing one element
        // will the OSF API present pagination information if pagination is not required?
        // it appears so:
        // https://api.osf.io/v2/users/me/institutions/?version=2.2
        // also note that the meta object is a top-level object in v2.2 of the API, e.g. compare
        // https://api.osf.io/v2/users/gb6f3/nodes/?version=2.2 to
        // https://api.osf.io/v2/users/gb6f3/nodes/

        // in the absence of a 'meta' section with size() information, should
        // the size of the wrapped resourcelist be returned?
    }
}
