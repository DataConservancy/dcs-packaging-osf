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
import org.dataconservancy.cos.osf.client.model.Comment;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class CommentObjectMappingTest extends AbstractMockServerTest {
    private final String baseResourcePath = "/model-mapping/2.2/objects/Comment/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of Comment JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalComment() throws Exception {
        addResponseInterceptor(baseResourcePath + "comment-minimal.json");

        final Comment comment = factory.getOsfService(OsfService.class).comment("anyurl").execute().body();

        assertEquals("j68y7", comment.getId());
        assertEquals(false, comment.isCan_edit());
        assertEquals("2016-04-01T04:45:44.038Z", comment.getDate_modified());
        assertEquals(false, comment.isDeleted());
        assertEquals(false, comment.is_ham());
        assertEquals(false, comment.isHas_children());
        assertEquals(true, comment.isModified());
        assertTrue(comment.getContent().startsWith("API Keys are not implemented"));
        assertEquals(false, comment.isHas_report());
        assertEquals(false, comment.isIs_abuse());
        assertEquals("2015-08-06T13:14:03.793Z", comment.getDate_created());
        assertEquals("node", comment.getPage());
        assertEquals("http://localhost:8000/v2/comments/j68y7/", comment.getLinks().get("self"));
    }
}
