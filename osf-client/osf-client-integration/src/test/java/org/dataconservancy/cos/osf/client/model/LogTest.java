/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.osf.client.model;

import com.github.jasminb.jsonapi.ResourceList;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests mapping between the JSON of a 'logs' document and the Java model for the Log class
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class LogTest extends AbstractMockServerTest {

    @Rule
    public TestName TEST_NAME = new TestName();

    private OsfService osfService;

    @Before
    public void setUp() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, LogTest.class, getBaseUri()));
        osfService = factory.getOsfService(OsfService.class);
    }

    @Test
    public void testLogMapping() throws Exception {
        final String registrationId = "eq7a4";

        final Registration registration = osfService.registration(registrationId).execute().body();
        assertNotNull(registration);
        assertNotNull(registration.getLogs());

        final ResourceList<Log> logs = osfService.getLogs(registration.getLogs()).execute().body();

        final String eventId = "57570a06c7950c0045ac803e";

        final Log log = logs.stream()
                .filter(e -> e.getId().equals(eventId))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("Expected log stream to contain log id " + eventId));

        // additional assertions

        final String expectedAction = "node_removed";
        final String expectedDate = "2016-06-07T17:52:19.617000";
        final String expectedNode = "eq7a4/";
        final String expectedOrigNode = "5w8q7/";
        final String expectedUser = "qmdz6/";

        assertEquals(expectedAction, log.getAction());
        assertEquals(expectedDate, log.getDate());
        assertTrue(log.getNode().endsWith(expectedNode));
        assertTrue(log.getOriginal_node().endsWith(expectedOrigNode));
        assertTrue(log.getUser().endsWith(expectedUser));

        assertTrue(log.getParams().containsKey("params_node"));
        assertTrue(log.getParams().containsKey("params_project"));
        assertTrue(((Map) log.getParams().get("params_node")).get("id").equals("5w8q7"));
        assertTrue(((Map) log.getParams().get("params_node")).get("title").equals("Workflow Execution"));
    }
}
