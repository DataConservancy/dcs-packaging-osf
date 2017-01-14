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
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class FilesGraphMappingTest extends BaseGraphMappingTest {

    @Test
    public void testFilesRelationships() throws Exception {
        // storage provider
        final File provider = osfService.files("http://localhost:8000/v2/registrations/tgzhk/files/")
                .execute().body().get(0);

        // files
        assertEquals(3, provider.getFiles().size());
        assertTrue(provider.getFiles().stream().anyMatch(f -> f.getId().equals("56eaca949ad5a101b13fe6ed")));
        assertTrue(provider.getFiles().stream().anyMatch(f -> f.getId().equals("56eacb926c613b01a5650b8d")));
        assertTrue(provider.getFiles().stream().anyMatch(f -> f.getId().equals("56eacb02b83f6901b9e90fbd")));

        // node relationship of provider
        assertEquals(null, provider.getNodeRel());

        // node relationship of files
        assertEquals("http://localhost:8000/v2/registrations/tgzhk/", provider.getFiles().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected File!"))
                .getNodeRel());

        // versions relationship of provider
        assertEquals(null, provider.getVersions());
        // versions relationship of a file
        final File folder = provider.getFiles().stream()
                .filter(f -> f.getId().equals("56eacb02b83f6901b9e90fbd")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected folder '56eacb02b83f6901b9e90fbd'"));
        final File fileWithVersions = folder.getFiles().stream()
                .filter(f -> f.getId().equals("56eacb19b83f6901b9e90fbe")).findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected file '56eacb19b83f6901b9e90fbe'"));
        assertEquals(1, fileWithVersions.getVersions().size());

        // comments
        assertEquals(null, provider.getComments());
    }
}
