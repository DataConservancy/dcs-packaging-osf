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

import com.github.jasminb.jsonapi.ResourceList;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class CommentTest extends AbstractMockServerTest {

    @Rule
    public TestName TEST_NAME = new TestName();

    private OsfService osfService;

    @Before
    public void setUp() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, CommentTest.class));
        osfService = factory.getOsfService(OsfService.class);
    }

    @Test
    public void testSimpleMapping() throws Exception {
        final ResourceList<Comment> comments = osfService.getComments("http://localhost:8000/v2/nodes/u9dc7/comments/")
                .execute()
                .body();

        assertNotNull(comments);
        assertEquals(2, comments.size());

        comments.stream().filter(comment -> comment.getId().equals("kam4y2f7xvu8"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected Comment with id 'kam4y2f7xvu8'"));
        comments.stream().filter(comment -> comment.getId().equals("72c698vdjure"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected Comment with id '72c698vdjure'"));


    }
}