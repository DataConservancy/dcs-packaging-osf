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
import org.dataconservancy.cos.osf.client.model.Category;
import org.dataconservancy.cos.osf.client.model.Permission;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.retrofit.OsfService;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class RegistrationObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/Registration/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of Registration JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalRegistration() throws Exception {
        addResponseInterceptor(baseResourcePath + "registration-minimal.json");

        final Registration reg = factory.getOsfService(OsfService.class).registration("anyurl").execute().body();

        assertEquals("Open-Ended Registration", reg.getRegistration_supplement());
        assertEquals(false, reg.isPending_embargo_approval());
        assertEquals(true, reg.isRegistration());
        assertEquals(null, reg.getEmbargo_end_date());
        assertEquals(false, reg.isWithdrawn());
        assertEquals(null, reg.getWithdrawal_justification());
        assertEquals(Category.PROJECT, reg.getCategory());
        assertEquals(false, reg.isPreprint());
        assertEquals(singletonList(Permission.READ), reg.getCurrent_user_permissions());
        assertEquals("Test Project", reg.getTitle());
        assertEquals("test", reg.getRegistrationMetadataSummary().getValue());
        assertEquals(emptyList(), reg.getRegistrationMetadataSummary().getComments());
        assertEquals(emptyList(), reg.getRegistrationMetadataSummary().getExtra());
        assertEquals(true, reg.isPublic());
        assertEquals(false, reg.isFork());
        assertEquals("foo project description.", reg.getDescription());
        assertEquals(emptyList(), reg.getTags());
        assertEquals(false, reg.isCollection());
        assertEquals(false, reg.isPending_registration_approval());
        // Note: not equal to '2016-08-01T19:15:56.126000Z' because we normalize date times with Joda
        assertEquals("2016-08-01T19:15:56.126Z", reg.getDate_modified());
        assertEquals("2016-08-09T04:35:20.557Z", reg.getDate_registered());
        assertEquals("2016-03-16T17:58:47.353Z", reg.getDate_created());
        assertEquals(false, reg.isPending_withdrawal());
        assertEquals("5nua2", reg.getId());
        assertEquals("2016", reg.getNode_license().getYear());
        assertEquals(Arrays.asList("Elliot Metsger", "all rights reserved."),
                reg.getNode_license().getCopyright_holders());

    }

}
