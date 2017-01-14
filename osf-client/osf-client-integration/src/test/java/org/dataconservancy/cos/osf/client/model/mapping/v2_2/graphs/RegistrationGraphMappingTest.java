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

import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.junit.Test;

import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class RegistrationGraphMappingTest extends BaseGraphMappingTest {

    @Test
    public void testRegistrationRelationships() throws Exception {
        final Registration reg = osfService.registrationByUrl("http://localhost:8000/v2/registrations/tgzhk/")
                .execute().body();

        assertNotNull(reg);
        assertEquals(null, reg.getLinked_registrations());
        assertEquals("http://localhost:8000/v2/nodes/xug4a/", reg.getRegistered_from());
        assertEquals("http://localhost:8000/v2/users/gb6f3/", reg.getRegistered_by());
        assertEquals("http://localhost:8000/v2/metaschemas/564d31db8c5e4a7c9694b2be/", reg.getRegistration_schema());
        assertEquals(3, reg.getChildren().size());
        reg.getChildren().stream().filter(r -> r.getId().equals("ur6jx")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing child ur6jx"));
        reg.getChildren().stream().filter(r -> r.getId().equals("tgcks")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing child tgcks"));
        reg.getChildren().stream().filter(r -> r.getId().equals("3q742")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing child 3q742"));
    }

    @Test
    public void testRegistrationRelationshipsOfNodeBase() throws Exception {
        final Registration reg = osfService.registrationByUrl("http://localhost:8000/v2/registrations/tgzhk/")
                .execute().body();

        // Files
        final File provider = reg.getFiles().stream().filter(f -> f.getId().equals("tgzhk:osfstorage")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing osfstorage provider 'tgzhk:osfstorage'"));
        assertEquals("Found " + provider.getFiles().size() + " file(s): " + provider.getFiles()
                .stream().map(File::getId).collect(Collectors.joining(", ")), 3, provider.getFiles().size());
        assertTrue(provider.getFiles().stream().anyMatch(f -> f.getId().equals("56eacb926c613b01a5650b8d")));
        assertTrue(provider.getFiles().stream().anyMatch(f -> f.getId().equals("56eacb02b83f6901b9e90fbd")));
        assertTrue(provider.getFiles().stream().anyMatch(f -> f.getId().equals("56eaca949ad5a101b13fe6ed")));

        // Contributors
        assertEquals("tgzhk-gb6f3", reg.getContributors().get(0).getId());

        // Root
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/", reg.getRoot());

        // Parent
        assertEquals(null, reg.getParent());

        // Forked from
        assertEquals(null, reg.getForked_from());

        // Logs
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/logs/", reg.getLogs());

        // License
        assertEquals("563c1cf88c5e4a3877f9e965", reg.getLicense().getId());

        // Wikis
        assertEquals(emptyList(), reg.getWikis());

        // Institutions
        assertEquals(emptyList(), reg.getAffiliated_institutions());

        // Forks
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/forks/", reg.getForks());

        // Citation
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/citation/", reg.getCitation());

        // Identifiers
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/identifiers/", reg.getIdentifiers());

        // Comments
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/comments/P2ZpbHRlciU1QnRhcmdldCU1RD10Z3poaw==",
                reg.getComments());
    }
}
