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

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Request;
import org.apache.jena.riot.RDFFormat;

import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
import org.dataconservancy.cos.rdf.support.OntologyManager;
import org.dataconservancy.packaging.shared.IpmPackager;
import org.dataconservancy.packaging.tool.model.GeneralParameterNames;
import org.dataconservancy.packaging.tool.model.PackageGenerationParameters;
import org.dataconservancy.packaging.tool.model.PropertiesConfigurationParametersBuilder;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Simple test exercising the IpmPackager
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class OsfContentProviderTest extends AbstractMockServerTest {
    private OntologyManager ontologyManager = new OntologyManager();

    @Rule
    public TestName TEST_NAME = new TestName();

    @Override
    protected String getOsfServiceConfigurationResource() {
        return "/org/dataconservancy/cos/packaging/config/osf-client.json";
    }

    @Test
    public void testCreatePackageSimple() throws Exception {
        // Prepare the OSF registration and users information
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, OsfContentProviderTest.class));
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

        // Prepare package graph and print it to StdErr.
        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        packageGraph.add(registration);
        users.forEach(packageGraph::add);
        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

        // Prepare content provider using package graph
        final OsfContentProvider contentProvider = new OsfContentProvider(packageGraph, (url) -> {
            final Call req = factory.getHttpClient().newCall(new Request.Builder().url(url).build());
            try {
                return req.execute().body().byteStream();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        // Read package generation parameters from a resource file.
        final InputStream paramStream =
                OsfContentProvider.class.getResourceAsStream("/PackageGenerationParams.properties");

        // Create the package in the default location with the default name.
        // No metatdata is supplied.
        final IpmPackager packager = new IpmPackager();
        packager.buildPackage(contentProvider, null, paramStream);
        contentProvider.close();
    }

    @Test
    public void testCreatePackageWithWiki() throws Exception {
        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(TEST_NAME, OsfContentProviderTest.class));
        final OsfService osfService = factory.getOsfService(OsfService.class);
        final String registrationId = "ng9em";

        final PackageGenerationParameters params =  new PropertiesConfigurationParametersBuilder()
                .buildParameters(OsfContentProviderTest.class
                        .getResourceAsStream("/PackageGenerationParams.properties"));

        params.addParam(GeneralParameterNames.PACKAGE_LOCATION,
                System.getProperty("java.io.tmpdir"));

        params.addParam(GeneralParameterNames.PACKAGE_NAME, "WikiPackage");
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


        //List<Wiki> wikis = osfService.wikis("http://localhost:8000/v2/registrations/ng9em/wikis/").execute().body();
        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

        final OsfContentProvider contentProvider = new OsfContentProvider(packageGraph, (url) -> {
            final Call req = factory.getHttpClient().newCall(new Request.Builder().url(url).build());
            try {
                return req.execute().body().byteStream();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        // Read package generation parameters from a resource file.
        final InputStream paramStream =
                OsfContentProvider.class.getResourceAsStream("/PackageGenerationParams.properties");

        final IpmPackager packager = new IpmPackager();
        packager.buildPackage(contentProvider, null, paramStream);
        contentProvider.close();
    }

}