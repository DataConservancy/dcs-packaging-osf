/*
 * Copyright 2017 Johns Hopkins University
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
package org.dataconservancy.cos.osf.client.model.mapping.v2_2.objects;

import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.Wiki;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests mapping a version 2.2 instance of a Wiki object in JSON to Java.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class WikiObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/Wiki/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of Wiki JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalWiki() throws Exception {
        addResponseInterceptor(baseResourcePath + "wiki-minimal.json");

        final List<Wiki> wikis = factory.getOsfService(OsfService.class).wikis("anyurl").execute().body();

        assertNotNull(wikis);
        assertEquals(1, wikis.size());

        final Wiki wiki = wikis.get(0);

        assertEquals("4b26r", wiki.getId());
        assertEquals("file", wiki.getKind());
        assertEquals("Examples", wiki.getName());
        // Note: not equal to '2016-12-05T21:29:01.463000Z' because we normalize date times with Joda
        assertEquals("2016-12-05T21:29:01.463Z", wiki.getDate_modified());
        assertEquals("text/markdown", wiki.getContent_type());
        assertEquals("/4b26r", wiki.getPath());
        assertEquals("/4b26r", wiki.getMaterialized_path());
        assertEquals(7168, wiki.getSize());
        assertEquals("http://localhost:8000/v2/wikis/4b26r/", wiki.getLinks().get("info"));
        assertEquals("http://localhost:8000/v2/wikis/4b26r/content/", wiki.getLinks().get("download"));
        assertEquals("http://localhost:8000/v2/wikis/4b26r/", wiki.getLinks().get("self"));
        assertEquals(5, wiki.getVersion());
    }

}
