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
import org.dataconservancy.cos.osf.client.model.License;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class LicenseObjectMappingTest extends AbstractMockServerTest {


    private final String baseResourcePath = "/model-mapping/2.2/objects/License/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of License JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalLicense() throws Exception {
        addResponseInterceptor(baseResourcePath + "license-osf-api.json");

        final License license = factory.getOsfService(OsfService.class).license("anyurl").execute().body();

        assertEquals("Copyright {{year}} {{copyrightHolders}}", license.getText());
        assertEquals("No license", license.getName());
        assertEquals("563c1cf88c5e4a3877f9e965", license.getId());
        assertEquals(Arrays.asList("year", "copyrightHolders"), license.getRequired_fields());
        assertEquals("https://api.osf.io/v2/licenses/563c1cf88c5e4a3877f9e965/", license.getLinks().get("self"));
    }
}
