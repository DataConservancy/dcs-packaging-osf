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

import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.Node;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Ignore("Fails due to https://github.com/CenterForOpenScience/osf.io/issues/6062")
public class NodeGraphMappingTest extends BaseGraphMappingTest {

    @Test
    public void testNodeRelationships() throws Exception {
        final Node node = osfService.nodeByUrl("http://localhost:8000/v2/nodes/xug4a/").execute().body();

        assertEquals("xug4a", node.getId());

        // draft_registrations
        assertEquals("http://localhost:8000/v2/nodes/xug4a/draft_registrations/", node.getDraft_registrations());

        // registrations
        assertEquals("http://localhost:8000/v2/nodes/xug4a/registrations/", node.getRegistrations());

        // linked_nodes
        assertEquals("http://localhost:8000/v2/nodes/xug4a/linked_nodes/", node.getLinked_nodes());

        // children
        assertEquals(3, node.getChildren().size());
        assertTrue(node.getChildren().stream().anyMatch(n -> n.getId().equals("veyxj")));
        assertTrue(node.getChildren().stream().anyMatch(n -> n.getId().equals("37a5n")));
        assertTrue(node.getChildren().stream().anyMatch(n -> n.getId().equals("xmnkz")));
    }

    @Test
    public void testNodeRelationshipsOfNodeBase() throws Exception {

        final Node node = osfService.nodeByUrl("http://localhost:8000/v2/nodes/xug4a/").execute().body();

        // contributors
        assertEquals(3, node.getContributors().size());
        final List<Contributor> contributors = node.getContributors();
        assertTrue(contributors.stream().anyMatch(c -> c.getId().equals("xug4a-gb6f3")));
        assertTrue(contributors.stream().anyMatch(c -> c.getId().equals("xug4a-9vw8x")));
        assertTrue(contributors.stream().anyMatch(c -> c.getId().equals("xug4a-r29rh")));

        // files
        assertEquals(1, node.getFiles().size());
        assertEquals("xug4a:osfstorage", node.getFiles().get(0).getId());

        // root
        assertEquals("http://localhost:8000/v2/nodes/xug4a/", node.getRoot());

        // parent
        assertEquals(null, node.getParent());

        // forked_from
        assertEquals(null, node.getForked_from());

        // logs
        assertEquals("http://localhost:8000/v2/nodes/xug4a/logs/", node.getLogs());

        // license
        assertEquals("563c1cf88c5e4a3877f9e965", node.getLicense().getId());

        // wikis
        assertEquals(2, node.getWikis().size());
        assertTrue(node.getWikis().stream().anyMatch(w -> w.getId().equals("z7rxw")));
        assertTrue(node.getWikis().stream().anyMatch(w -> w.getId().equals("xb89d")));

        // institutions
        assertEquals(emptyList(), node.getAffiliated_institutions());

        // forks
        assertEquals("http://localhost:8000/v2/nodes/xug4a/forks/", node.getForks());

        // citation (currently support retrieving the relationship, but citation support is not present in v2.2?)
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/citation/", node.getCitation());

        // identifiers
        assertEquals("http://localhost:8000/v2/nodes/xug4a/identifiers/", node.getIdentifiers());

        // comments
        assertEquals("http://localhost:8000/v2/nodes/xug4a/comments/P2ZpbHRlciU1QnRhcmdldCU1RD14dWc0YQ==",
                node.getComments());

        // preprints
        assertEquals("http://localhost:8000/v2/nodes/xug4a/preprints/", node.getPreprints());

    }
}
