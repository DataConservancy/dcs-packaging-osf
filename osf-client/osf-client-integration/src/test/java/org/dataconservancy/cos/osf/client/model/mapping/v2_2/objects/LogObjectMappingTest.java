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

package org.dataconservancy.cos.osf.client.model.mapping.v2_2.objects;

import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.Log;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import java.util.Map;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class LogObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/Log/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of a Log event for a comment_added action.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalLogCommentAdded() throws Exception {
        addResponseInterceptor(baseResourcePath + "log-minimal-comment_added.json");

        final Log log = factory.getOsfService(OsfService.class).log("anyurl").execute().body();

        assertEquals("2017-01-04T20:46:34.610Z", log.getDate());
        assertEquals("comment_added", log.getAction());
        assertEquals("586d5f2ab83f6901f9174e5d", log.getId());
        assertEquals(null, log.getParams().get("preprint_provider"));
        assertEquals("y9jdt", ((Map<String, ?>) log.getParams().get("params_node")).get("id"));
        assertEquals("OSF Public API Feedback", ((Map<String, ?>) log.getParams().get("params_node")).get("title"));
        assertEquals(null, log.getParams().get("params_project"));
        assertEquals(emptyList(), log.getParams().get("contributors"));
    }

    @Test
    public void testMinimalOsfStorageFileAdded() throws Exception {
        addResponseInterceptor(baseResourcePath + "log-minimal-osf_storage_file_added.json");

        final Log log = factory.getOsfService(OsfService.class).log("anyurl").execute().body();

        assertEquals("2017-01-06T21:39:54.374Z", log.getDate());
        assertEquals("osf_storage_file_added", log.getAction());
        assertEquals("58700eaab83f6901ff065e43", log.getId());
        assertEquals(null, log.getParams().get("preprint_provider"));
        assertEquals("y9jdt", ((Map<String, ?>) log.getParams().get("params_node")).get("id"));
        assertEquals("OSF Public API Feedback", ((Map<String, ?>) log.getParams().get("params_node")).get("title"));
        assertEquals(null, log.getParams().get("params_project"));
        assertEquals(emptyList(), log.getParams().get("contributors"));
        assertEquals("/typical osf workflow wiki examples.py", log.getParams().get("path"));
        assertEquals("/project/y9jdt/files/osfstorage/58700ea26c613b01fdac5e0a/?action=download",
                ((Map<String, String>) log.getParams().get("urls")).get("download"));
        assertEquals("/project/y9jdt/files/osfstorage/58700ea26c613b01fdac5e0a/",
                ((Map<String, String>) log.getParams().get("urls")).get("view"));

    }

    @Test
    public void testMinimalOsfStorageFileUpdated() throws Exception {
        addResponseInterceptor(baseResourcePath + "log-minimal-osf_storage_file_updated.json");

        final Log log = factory.getOsfService(OsfService.class).log("anyurl").execute().body();

        assertEquals("2017-01-06T21:42:01.805Z", log.getDate());
        assertEquals("osf_storage_file_updated", log.getAction());
        assertEquals("58700f29b83f6901ff065e47", log.getId());
        assertEquals(null, log.getParams().get("preprint_provider"));
        assertEquals("y9jdt", ((Map<String, ?>) log.getParams().get("params_node")).get("id"));
        assertEquals("OSF Public API Feedback", ((Map<String, ?>) log.getParams().get("params_node")).get("title"));
        assertEquals(null, log.getParams().get("params_project"));
        assertEquals(emptyList(), log.getParams().get("contributors"));
        assertEquals("/typical osf workflow wiki examples.py", log.getParams().get("path"));
        assertEquals("/project/y9jdt/files/osfstorage/58700ea26c613b01fdac5e0a/?action=download",
                ((Map<String, String>) log.getParams().get("urls")).get("download"));
        assertEquals("/project/y9jdt/files/osfstorage/58700ea26c613b01fdac5e0a/",
                ((Map<String, String>) log.getParams().get("urls")).get("view"));

    }

    @Test
    public void testMinimalWikiUpdated() throws Exception {
        addResponseInterceptor(baseResourcePath + "log-minimal-wiki_updated.json");

        final Log log = factory.getOsfService(OsfService.class).log("anyurl").execute().body();

        assertEquals("2017-01-06T21:18:36.829Z", log.getDate());
        assertEquals("wiki_updated", log.getAction());
        assertEquals("587009ac9ad5a102008b06e5", log.getId());
        assertEquals(null, log.getParams().get("preprint_provider"));
        assertEquals("y9jdt", ((Map<String, ?>) log.getParams().get("params_node")).get("id"));
        assertEquals("OSF Public API Feedback", ((Map<String, ?>) log.getParams().get("params_node")).get("title"));
        assertEquals(null, log.getParams().get("params_project"));
        assertEquals(emptyList(), log.getParams().get("contributors"));
        assertEquals("6", log.getParams().get("version"));
        assertEquals("934e3", log.getParams().get("page_id"));
        assertEquals("Typical Workflow", log.getParams().get("page"));
    }
}
