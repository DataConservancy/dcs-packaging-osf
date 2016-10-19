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
import org.apache.commons.io.FileUtils;
import org.apache.jena.riot.RDFFormat;
import org.dataconservancy.cos.osf.client.config.WbConfigurationService;
import org.dataconservancy.cos.osf.client.model.AbstractMockServerTest;
import org.dataconservancy.cos.osf.client.model.Registration;
import org.dataconservancy.cos.osf.client.model.User;
import org.dataconservancy.cos.osf.client.model.Wiki;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.dataconservancy.cos.osf.packaging.OsfPackageGraph;
import org.dataconservancy.cos.osf.packaging.support.OntologyManager;
import org.dataconservancy.packaging.tool.model.GeneralParameterNames;
import org.dataconservancy.packaging.tool.model.PackageGenerationParameters;
import org.dataconservancy.packaging.tool.model.PropertiesConfigurationParametersBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Simple test exercising the IpmPackager
 */
public class IpmPackagerTest extends AbstractMockServerTest {
    private String baseUri = getBaseUri().toString();

    private OntologyManager ontologyManager = new OntologyManager();

    @Rule
    public TestName testName = new TestName();

    @Override
    protected String getOsfServiceConfigurationResource() {
        return "/org/dataconservancy/cos/packaging/config/osf-client.json";
    }

    @Test
    public void testCreatePackageSimple() throws Exception {
        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(testName, IpmPackagerTest.class, getBaseUri()));
        final OsfService osfService = factory.getOsfService(OsfService.class);
        final String registrationId = "eq7a4";

        final PackageGenerationParameters params =  new PropertiesConfigurationParametersBuilder()
                        .buildParameters(IpmPackager.class
                                .getResourceAsStream("/PackageGenerationParams.properties"));

        params.addParam(GeneralParameterNames.PACKAGE_LOCATION,
                System.getProperty("java.io.tmpdir"));
        params.addParam(GeneralParameterNames.PACKAGE_NAME, "MyPackage");
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

//        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

        IpmPackager packager = new IpmPackager((String url) -> {
            Call req = factory.getHttpClient().newCall(new Request.Builder().url(url).build());
            try {
                return req.execute().body().bytes();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        packager.buildPackage(packageGraph, null);

    }

    @Test
    public void testCreatePackageWithWiki() throws Exception {
        final OsfPackageGraph packageGraph = new OsfPackageGraph(ontologyManager);
        factory.interceptors().add(new RecursiveInterceptor(testName, IpmPackagerTest.class, getBaseUri()));
        final OsfService osfService = factory.getOsfService(OsfService.class);
        final String registrationId = "ng9em";

        final PackageGenerationParameters params =  new PropertiesConfigurationParametersBuilder()
                .buildParameters(IpmPackager.class
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

        List<Wiki> wikis = osfService.wikis("http://localhost:8000/v2/registrations/ng9em/wikis/").execute().body();
//        packageGraph.serialize(System.err, RDFFormat.TURTLE_PRETTY, packageGraph.OSF_SELECTOR);

        IpmPackager packager = new IpmPackager((String url) -> {
            Call req = factory.getHttpClient().newCall(new Request.Builder().url(url).build());
            try {
                return req.execute().body().bytes();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        });

        packager.buildPackage(packageGraph, null);

    }

    /**
     * Insures that the system property, {@code osf.client.conf}, is honored by Spring when constructing the application context.
     *
     * @throws Exception
     */
    @Test
    public void testIpmManagerConfigurationResolutionWithSpring() throws Exception {
        System.setProperty("osf.client.conf", "classpath:/org/dataconservancy/cos/packaging/TestSpringClientConfiguration-classpath.json");
        WbConfigurationService configSvc = IpmPackager.cxt.getBean("wbConfigurationSvc", WbConfigurationService.class);
        assertEquals("test-host", configSvc.getConfiguration().getHost());
    }

    /**
     * An unsatisfying test attempting to insure that a temporary download directory can be created on different
     * platforms.
     *
     * @throws Exception
     */
    @Test
    public void testAllocateTempDirectory() throws Exception {
        final File allocatedDirectory = IpmPackager.allocateTempDir();
        assertNotNull(allocatedDirectory);
        assertTrue(allocatedDirectory.exists());
        assertTrue(allocatedDirectory.isDirectory());

        // clean up
        FileUtils.deleteQuietly(allocatedDirectory);
    }

    /**
     * Multiple calls to allocateTempDir result in a unique name each time.
     *
     * @throws Exception
     */
    @Test
    public void testAllocateTempDirectoryUniqueNames() throws Exception {
        File one = IpmPackager.allocateTempDir();
        File two = IpmPackager.allocateTempDir();

        assertNotSame(one, two);
        assertNotEquals(one, two);
        assertNotEquals(one.getName(), two.getName());

        // Clean up
        FileUtils.forceDelete(one);
        FileUtils.forceDelete(two);
    }

    /**
     * Another unsatisfying test attempting to insure that temporary directory names don't collide even when
     * called from the same parent thread.
     *
     * @throws Exception
     */
    @Test
    public void testAllocateTempDirectoryThreaded() throws Exception {
        final FileHolder fhOne = new FileHolder();
        final FileHolder fhTwo = new FileHolder();

        Thread one = new Thread(() -> {
            try {
                fhOne.setF(IpmPackager.allocateTempDir());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });

        Thread two = new Thread(() -> {
            try {
                fhTwo.setF(IpmPackager.allocateTempDir());
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });

        one.start();
        two.start();

        one.join(10000);
        two.join(10000);

        assertNotNull(fhOne.f);
        assertNotNull(fhTwo.f);
        assertNotEquals(fhOne.f, fhTwo.f);
        assertNotEquals(fhOne.f.getName(), fhTwo.f.getName());

        // Clean up
        FileUtils.deleteQuietly(fhOne.f);
        FileUtils.deleteQuietly(fhTwo.f);
    }

    private class FileHolder {
        File f;

        public void setF(File f) {
            this.f = f;
        }
    }
}