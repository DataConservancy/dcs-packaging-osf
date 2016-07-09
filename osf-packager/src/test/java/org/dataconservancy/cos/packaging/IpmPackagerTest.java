/*
 * Copyright 2016 Johns Hopkins University
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
package org.dataconservancy.cos.packaging;

import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
import org.dataconservancy.cos.osf.packaging.support.OntologyManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple test exercising the IpmPackager
 */
public class IpmPackagerTest  extends AbstractMockServerTest {
    private String baseUri = getBaseUri().toString();

    private OntologyManager ontologyManager = new OntologyManager();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreatePackageSimple() throws Exception {
        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(testName, IpmPackagerTest.class, getBaseUri()));
        final OsfService osfService = factory.getOsfService(OsfService.class);
        final String registrationId = "eq7a4";

        final Registration registration = osfService.registration(registrationId).execute().body();
        final List<User> users = registration.getContributors().stream()
                .map(c -> {
                    try {
                        return osfService.user(c.getId()).execute().body();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                })
                .collect(Collectors.toList());

        packageGraph.add(registration);
        users.forEach(packageGraph::add);

        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

        IpmPackager.buildPackage(packageGraph);

    }
}