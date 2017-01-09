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
import org.dataconservancy.cos.osf.client.model.FileVersion;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class FileVersionObjectMappingTest extends AbstractMockServerTest {
    private final String baseResourcePath = "/model-mapping/2.2/objects/FileVersion/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of FileVersion JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testFileVersion() throws Exception {
        addResponseInterceptor(baseResourcePath + "file-version-github.json");

        final FileVersion version = factory.getOsfService(OsfService.class).fileversion("anyurl").execute().body();

        assertEquals("b07e10171449cd03df6362c280410ce68b393e34", version.getId());
        assertEquals(null, version.getContent_type());
        assertEquals(10, (int)version.getSize());
        assertEquals("http://localhost:8000/v2/files/56eac5d6b83f6901bbe90db3/versions/" +
                "b07e10171449cd03df6362c280410ce68b393e34/", version.getLinks().get("self"));
        assertEquals("https://osf.io/xug4a/files/github/README.md?ref=b07e10171449cd03df6362c280410ce68b393e34",
                version.getLinks().get("html"));

    }
}
