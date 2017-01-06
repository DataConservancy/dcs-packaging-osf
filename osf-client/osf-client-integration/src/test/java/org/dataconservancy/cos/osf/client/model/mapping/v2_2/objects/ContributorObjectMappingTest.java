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
import org.dataconservancy.cos.osf.client.model.Contributor;
import org.dataconservancy.cos.osf.client.model.Permission;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class ContributorObjectMappingTest extends AbstractMockServerTest {
    private final String baseResourcePath = "/model-mapping/2.2/objects/Contributor/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of License JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalLicense() throws Exception {
        addResponseInterceptor(baseResourcePath + "contributor-minimal.json");

        final Contributor c = factory.getOsfService(OsfService.class).contributor("anyurl").execute().body();

        assertEquals("xug4a-9vw8x", c.getId());
        assertEquals(1, c.getIndex());
        assertEquals(null, c.getUnregistered_contributor());
        assertEquals(true, c.isBibliographic());
        assertEquals(Permission.WRITE, c.getPermission());
    }
}
