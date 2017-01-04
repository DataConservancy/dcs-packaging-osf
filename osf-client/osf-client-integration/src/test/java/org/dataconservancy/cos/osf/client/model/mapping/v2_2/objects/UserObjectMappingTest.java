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
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class UserObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/User/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of User JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalUser() throws Exception {
        addResponseInterceptor(baseResourcePath + "user-minimal.json");

        final User user = factory.getOsfService(OsfService.class).userByUrl("anyurl").execute().body();

        assertNotNull(user);
        assertEquals("typ46", user.getId());
        assertEquals("Geiger", user.getFamily_name());
        assertEquals("", user.getSuffix());
        assertEquals("en_us", user.getLocale());
        // Note: not equal to '2014-03-18T19:11:57.252000Z' because we normalize date times with Joda
        assertEquals("2014-03-18T19:11:57.252Z", user.getDate_registered());
        assertEquals("J.", user.getMiddle_names());
        assertEquals("Brian", user.getGiven_name());
        assertEquals("Brian J. Geiger", user.getFull_name());
        assertTrue(user.isActive());
        assertEquals("America/New_York", user.getTimezone());
        assertEquals("http://localhost:8000/v2/users/typ46/", user.getLinks().get("self"));
        assertEquals("https://osf.io/typ46/", user.getLinks().get("html"));
        assertEquals("https://secure.gravatar.com/avatar/3dd8757ba100b8406413706886243811?d=identicon",
                user.getLinks().get("profile_image"));
    }

}
