/*
 *
 *  * Copyright 2016 Johns Hopkins University
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

package org.dataconservancy.cos.osf.client.model;

import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class ContributorTest extends AbstractMockServerTest {

    @Rule
    public TestName TEST_NAME = new TestName();

    private OsfService osfService;

    @Before
    public void setUp() throws Exception {
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, ContributorTest.class));
        osfService = factory.getOsfService(OsfService.class);
    }

    @Test
    public void testResolveUser() throws Exception {
        final String contributorEndpoint = "https://api.osf.io/v2/registrations/0zqbo/contributors/";
        final List<Contributor> contributors = osfService.contributors(contributorEndpoint).execute().body();

        assertNotNull(contributors);
        assertEquals(2, contributors.size());

        final Contributor cVni4p = contributors.stream()
                .filter(c -> c.getId().equals("0zqbo-vni4p"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected contributor 0zqbo-vni4p"));

        final Contributor cJym4z = contributors.stream()
                .filter(c -> c.getId().equals("0zqbo-jym4z"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Missing expected contributor 0zqbo-jym4z"));


        assertTrue(cVni4p.getUserRel().endsWith("vni4p/"));

        assertTrue(cJym4z.getUserRel().endsWith("jym4z/"));

    }
}