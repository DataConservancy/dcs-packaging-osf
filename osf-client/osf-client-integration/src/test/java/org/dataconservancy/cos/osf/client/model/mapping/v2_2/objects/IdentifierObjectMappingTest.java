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
import org.dataconservancy.cos.osf.client.model.Identifier;
import org.dataconservancy.cos.osf.client.retrofit.OsfService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class IdentifierObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/Identifier/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of Identifier JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalIdentifier() throws Exception {
        addResponseInterceptor(baseResourcePath + "identifiers-minimal.json");

        final Identifier identifier = factory.getOsfService(OsfService.class).identifier("anyurl").execute().body();

        assertEquals("http://localhost:8000/v2/identifiers/564366998c5e4a74a2e66146/",
                identifier.getLinks().get("self"));
        assertEquals("doi", identifier.getCategory());
        assertEquals("10.17605/OSF.IO/NMBWQ", identifier.getValue());
        assertEquals("564366998c5e4a74a2e66146", identifier.getId());

    }

}
