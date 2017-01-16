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
import org.dataconservancy.cos.osf.client.model.File;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class FileObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/File/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of File JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalStorageProvider() throws Exception {
        addResponseInterceptor(baseResourcePath + "github-storage-provider-minimal.json");

        final File file = factory.getOsfService(OsfService.class).file("anyurl").execute().body();

        assertEquals("xug4a", file.getNode());
        assertEquals("/", file.getPath());
        assertEquals("folder", file.getKind());
        assertEquals("github", file.getName());
        assertEquals("github", file.getProvider());
        assertEquals("xug4a:github", file.getId());
        assertEquals(null, file.getExtra());
        assertEquals("http://localhost:8000/v2/addons/?filter%5Bcategories%5D=storage",
                file.getLinks().get("storage_addons"));
        assertEquals("https://files.osf.io/v1/resources/xug4a/providers/github/", file.getLinks().get("upload"));
        assertEquals("https://files.osf.io/v1/resources/xug4a/providers/github/?kind=folder",
                file.getLinks().get("new_folder"));
    }

    @Test
    public void testMinimalFile() throws Exception {
        addResponseInterceptor(baseResourcePath + "github-file-minimal.json");

        final File file = factory.getOsfService(OsfService.class).file("anyurl").execute().body();

        assertEquals(null, file.getNode());
        assertEquals("/README.md", file.getPath());
        assertEquals("/README.md", file.getMaterialized_path());
        assertEquals("file", file.getKind());
        assertEquals("README.md", file.getName());
        assertEquals("github", file.getProvider());
        assertEquals("56eac5d6b83f6901bbe90db3", file.getId());
        assertEquals("2017-01-06T17:36:56.073Z", file.getLast_touched());
        assertEquals("2016-03-17T14:33:11.000Z", file.getDate_created());
        assertEquals("2017-01-04T22:41:12.000Z", file.getDate_modified());
        assertEquals(7, file.getCurrent_version());
        assertEquals("8pdf3", file.getGuid());
        assertEquals(83, file.getSize().intValue());
        assertEquals(emptyList(), file.getTags());
        assertNotNull(file.getExtra());
        assertEquals("a", file.getHashes().get("sha256").getValue());
        assertEquals("b", file.getHashes().get("md5").getValue());
        assertEquals(3, file.getDownloads());
        assertEquals("http://localhost:8000/v2/files/56eac5d6b83f6901bbe90db3/",
                file.getLinks().get("info"));
        assertEquals("http://localhost:8000/v2/files/56eac5d6b83f6901bbe90db3/",
                file.getLinks().get("self"));
        assertEquals("https://files.osf.io/v1/resources/xug4a/providers/github/README.md",
                file.getLinks().get("move"));
        assertEquals("https://files.osf.io/v1/resources/xug4a/providers/github/README.md",
                file.getLinks().get("upload"));
        assertEquals("https://files.osf.io/v1/resources/xug4a/providers/github/README.md",
                file.getLinks().get("download"));
        assertEquals("https://files.osf.io/v1/resources/xug4a/providers/github/README.md",
                file.getLinks().get("delete"));
    }
}
