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
import org.dataconservancy.cos.osf.client.model.Institution;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class InstitutionObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/Institution/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of Institution JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalInstitution() throws Exception {
        addResponseInterceptor(baseResourcePath + "institution-minimal.json");

        final Institution institution = factory.getOsfService(OsfService.class).institution("anyurl").execute().body();

        assertEquals("https://accounts.osf.io/Shibboleth.sso/Login?entityID=urn%3Amace%3Aincommon%3Ajohnshopkins.edu",
                institution.getAuth_url());
        assertEquals("/static/img/institutions/shields/jhu-shield.png", institution.getLogo_path());
        assertEquals("Johns Hopkins University", institution.getName());
        assertEquals("A research data service provided by the <a href=\u005C\u0022https://www.library.jhu.edu/" +
                "\u005C\u0022>Sheridan Libraries</a>.", institution.getDescription());
        assertEquals("jhu", institution.getId());
        assertEquals("http://localhost:8000/v2/institutions/jhu/", institution.getLinks().get("self"));
    }
}
