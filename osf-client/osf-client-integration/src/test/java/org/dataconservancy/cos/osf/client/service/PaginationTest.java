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
import org.dataconservancy.cos.osf.client.model.Node;
import org.junit.Before;
import org.junit.Test;

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

//    @Test
//    public void testPaginationOfRel() throws Exception {
//        final Registration r = osfService.registrationByUrl("http://localhost:8000/v2/registrations/tgzhk/")
//                .execute().body();
//
//        final String logsRel = r.getLogs();
//        ResourceList<Log> resultsPage = osfService.getLogs(logsRel).execute().body();
//
//        assertEquals(10, resultsPage.size());
//        assertEquals(Integer.valueOf(20), resultsPage.getMeta().get("total"));
//        assertEquals(null, resultsPage.getFirst());
//        assertEquals(null, resultsPage.getPrevious());
//        assertEquals("http://localhost:8000/v2/registrations/tgzhk/logs/?page=2", resultsPage.getNext());
//        assertEquals("http://localhost:8000/v2/registrations/tgzhk/logs/?page=2", resultsPage.getLast());
//
//        resultsPage = osfService.getLogs(resultsPage.getNext()).execute().body();
//
//        assertEquals(10, resultsPage.size());
//        assertEquals("http://localhost:8000/v2/registrations/tgzhk/logs/", resultsPage.getFirst());
//        assertEquals("http://localhost:8000/v2/registrations/tgzhk/logs/", resultsPage.getPrevious());
//        assertEquals(null, resultsPage.getNext());
//        assertEquals(null, resultsPage.getLast());
//        assertEquals(Integer.valueOf(20), resultsPage.getMeta().get("total"));
//    }

    @Test
    public void testPaginationOfFiles() throws Exception {
        final Node n = osfService.nodeByUrl("http://localhost:8000/v2/nodes/xmnkz/").execute().body();

        final File provider = n.getFiles().get(0);
        assertEquals("xmnkz:osfstorage", provider.getId());

        final List<File> files = provider.getFiles();
        assertEquals(11, files.size());
    }
}
