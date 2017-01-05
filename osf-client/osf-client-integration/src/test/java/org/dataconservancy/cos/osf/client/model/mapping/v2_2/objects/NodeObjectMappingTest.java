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
import org.dataconservancy.cos.osf.client.model.Category;
import org.dataconservancy.cos.osf.client.model.Node;
import org.dataconservancy.cos.osf.client.model.NodeLicense;
import org.dataconservancy.cos.osf.client.model.Permission;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class NodeObjectMappingTest extends AbstractMockServerTest {

    private final String baseResourcePath = "/model-mapping/2.2/objects/Node/";

    /**
     * Tests a minimal mapping (attributes only, no relationships) of Node JSON to Java.
     *
     * @throws Exception
     */
    @Test
    public void testMinimalNode() throws Exception {
        addResponseInterceptor(baseResourcePath + "node-minimal.json");

        final Node node = factory.getOsfService(OsfService.class).nodeByUrl("anyurl").execute().body();

        assertEquals("xug4a", node.getId());
        assertEquals(Category.PROJECT, node.getCategory());
        assertEquals(false, node.isFork());
        assertEquals(false, node.isPreprint());
        assertEquals("foo project description.", node.getDescription());
        assertEquals(Collections.singletonList(Permission.READ), node.getCurrent_user_permissions());
        // Note: not equal to '2017-01-04T22:41:12.000000Z' because we normalize date times with Joda
        assertEquals("2017-01-04T22:41:12.000Z", node.getDate_modified());
        assertEquals("2016-03-16T17:58:47.353Z", node.getDate_created());
        assertEquals("Test Project", node.getTitle());
        assertEquals(false, node.isCollection());
        assertEquals(false, node.isRegistration());
        assertEquals(true, node.isPublic());
        assertEquals(Collections.emptyList(), node.getTags());
        final NodeLicense expectedLicense = new NodeLicense();
        expectedLicense.setYear("2016");
        expectedLicense.setCopyright_holders(Arrays.asList("Elliot Metsger", "all rights reserved."));
        assertEquals(expectedLicense, node.getNode_license());
        assertEquals("http://localhost:8000/v2/nodes/xug4a/", node.getLinks().get("self"));
        assertEquals("https://osf.io/xug4a/", node.getLinks().get("html"));
    }
}
