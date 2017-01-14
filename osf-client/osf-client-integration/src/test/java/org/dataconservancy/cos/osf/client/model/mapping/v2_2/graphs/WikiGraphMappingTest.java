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
package org.dataconservancy.cos.osf.client.model.mapping.v2_2.graphs;

import org.dataconservancy.cos.osf.client.model.Comment;
import org.dataconservancy.cos.osf.client.model.Wiki;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class WikiGraphMappingTest extends BaseGraphMappingTest {

    @Test
    @Ignore("Fails due to https://github.com/CenterForOpenScience/osf.io/issues/6062")
    public void testWikiRelationships() throws Exception {
        final Wiki wiki = osfService.wiki("http://localhost:8000/v2/wikis/xb89d/").execute().body();

        assertEquals("http://localhost:8000/v2/nodes/xug4a/", wiki.getNode());
        assertEquals("gb6f3", wiki.getUser().getId());
        final List<Comment> comments = wiki.getComments();
        assertEquals(1, comments.size());
        comments.stream().filter(c -> c.getId().equals("yw2tjpf86bgx")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing comment 'yw2tjpf86bgx'"));
    }
}
