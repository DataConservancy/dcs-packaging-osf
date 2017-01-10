/*
 *
 *  * Copyright 2016 Johns Hopkins University
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

package org.dataconservancy.cos.osf.client.model;

import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ben Trumbore (wbt3@cornell.edu)
 */
public class WikiTest extends AbstractMockServerTest {

    @Rule
    public TestName TEST_NAME = new TestName();

    private OsfService osfService;

    @Before
    public void setUp() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, WikiTest.class));
        osfService = factory.getOsfService(OsfService.class);
    }

    @Test
    public void testWikiMapping() throws Exception {
        final String registrationId = "ng9em";
        final Registration registration = osfService.registration(registrationId).execute().body();
        assertNotNull(registration);

        final List<Wiki> wikis = registration.getWikis();
        assertNotNull(wikis);
        assertEquals(1, wikis.size());

        final Wiki pjnbm = wikis.stream()
                .filter(c -> c.getId().equals("pjnbm"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected wiki pjnbm"));

        assertTrue(pjnbm.getNode().endsWith("ng9em/"));
        assertTrue(pjnbm.getUser().getId().equals("3rty2"));

        final List<Comment> comments = pjnbm.getComments();
        assertNotNull(comments);
        assertEquals(2, comments.size());
        comments.stream().filter(comment -> comment.getId().equals("3ben8mz4ugwp"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected Comment with id '3ben8mz4ugwp'"));
        comments.stream().filter(comment -> comment.getId().equals("86c4gqutj9bz"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected Comment with id '86c4gqutj9bz'"));

        assertEquals("file", pjnbm.getKind());
        assertEquals("home", pjnbm.getName());
        assertEquals("2016-09-13T22:24:10.128Z", pjnbm.getDate_modified());
        assertEquals("text/markdown", pjnbm.getContent_type());
        assertEquals("/pjnbm", pjnbm.getPath());
        assertEquals("/pjnbm", pjnbm.getMaterialized_path());
        assertEquals(916, pjnbm.getSize());
        assertEquals("pjnbm", pjnbm.getId());
        assertEquals(1, pjnbm.getExtra().get("version"));

    }
}