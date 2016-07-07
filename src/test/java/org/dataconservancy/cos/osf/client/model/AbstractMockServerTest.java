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
package org.dataconservancy.cos.osf.client.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jasminb.jsonapi.ResolutionStrategy;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.config.BaseConfigurationService;
import org.dataconservancy.cos.osf.client.config.DefaultOsfJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.DefaultWbJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.JacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.config.WbClientConfiguration;
import org.dataconservancy.cos.osf.client.service.OsfService;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.rules.TestName;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.mockserver.model.HttpCallback.callback;
import static org.mockserver.model.HttpRequest.request;

/**
 * Test fixture providing a {@link MockServerClient} used to configure HTTP expectations.
 */
public abstract class AbstractMockServerTest extends AbstractOsfClientTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractMockServerTest.class);
    /**
     * Custom HTTP header sent by a test request, used to tell the {@code MockServer} what JSON document
     * to return.  The values are interpreted as classpath resources.
     */
    public static final String X_RESPONSE_RESOURCE = "X-Response-Resource";
    /**
     * Base path of the file system hierarchy containing JSON response documents
     */
    public static final String JSON_ROOT = "/json/";

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient mockServer;

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient wbMockServer;

    /**
     * Starts mock HTTP servers on the port specified by the OSF client configuration and the Waterbutler client configuration
     */
    @BeforeClass
    public static void startMockServer() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        JacksonConfigurer<OsfClientConfiguration> osfConfigurer = new DefaultOsfJacksonConfigurer<>();
        JacksonConfigurer<WbClientConfiguration> wbConfigurer = new DefaultWbJacksonConfigurer<>();

        mockServer = ClientAndServer.startClientAndServer(
                osfConfigurer.configure(
                        mapper.readTree(IOUtils.toString(BaseConfigurationService.getConfigurationResource("org/dataconservancy/cos/osf/client/config/osf-client-local.json"), "UTF-8")),
                        mapper,
                        OsfClientConfiguration.class
                ).getPort()
        );

        wbMockServer = ClientAndServer.startClientAndServer(
                wbConfigurer.configure(
                        mapper.readTree(IOUtils.toString(BaseConfigurationService.getConfigurationResource("org/dataconservancy/cos/osf/client/config/osf-client-local.json"), "UTF-8")),
                        mapper,
                        WbClientConfiguration.class
                ).getPort()
        );
    }

    /**
     * Stops mock HTTP servers
     */
    @AfterClass
    public static void stopMockServer() throws Exception {
        mockServer.stop();
        wbMockServer.stop();
    }

    public static URI relativize(URI baseUri, URI requestUri) {
        URI result = baseUri.relativize(requestUri);
        LOG.trace("Relativizing {} against {}: {}", baseUri, requestUri, result);
        return result;
    }

    public static String resourceBase(TestName testName) {
        assertNotNull(testName);
        StringBuilder base = new StringBuilder(JSON_ROOT);
        base.append(NodeTest.class.getSimpleName()).append("/");
        base.append(testName.getMethodName()).append("/");

        LOG.trace("Test resource base path: {}", base);
        return base.toString();
    }

    public static String resourceBase(TestName testName, Class testClass) {
        assertNotNull(testName);
        StringBuilder base = new StringBuilder(JSON_ROOT);
        base.append(testClass.getSimpleName()).append("/");
        base.append(testName.getMethodName()).append("/");

        LOG.trace("Test resource base path: {}", base);
        return base.toString();
    }

    /**
     * Responsible for resolving the JSON response resource.
     *
     * @param test contains metadata about the current test method.
     * @param baseUri the baseUri of the OSF V2 API
     * @param requestUri the full request URI
     * @return a Path that identifies a classpath resource containing the JSON response document
     */
    public static Path resolveResponseResource(TestName test, Class testClass, URI baseUri, URI requestUri) {
        // http://localhost:8000/v2/nodes/v8x57/files/osfstorage/ -> nodes/v8x57/files/osfstorage/
        URI relativizedRequestUri = baseUri.relativize(requestUri);
        Path requestPath = Paths.get(relativizedRequestUri.getPath());

        // /json/NodeTest/testGetNodeObjectResolution/
        Path fsBase = Paths.get(resourceBase(test, testClass));

        // /json/NodeTest/testGetNodeObjectResolution/nodes/v8x57/files/osfstorage/index.json
        Path resolvedPath = Paths.get(fsBase.toString(), requestPath.toString(), "index.json");

        return resolvedPath;
    }

    /**
     * Sets up the expectations of the mock http server.
     */
    @Before
    public void setUpExpectations() {

        /*
         * Invokes the NodeResponseCallback when the HTTP header "X-Response-Resource" is present.
         * The header value is a classpath resource to the JSON document to be serialized for the
         * response.
         */
        mockServer.when(
                request()
                        .withHeader(X_RESPONSE_RESOURCE)
        )
                .callback(
                        callback()
                                .withCallbackClass(NodeResponseCallback.class.getName())
                );
        wbMockServer.when(
                request()
                        .withHeader(X_RESPONSE_RESOURCE)
        )
                .callback(
                        callback()
                                .withCallbackClass(NodeResponseCallback.class.getName())
                );
    }

    /**
     * Responsible for resolving the JSON response resource for OSF v2 API calls that are recursive.
     * <p>
     * For example, the {@link ResolutionStrategy} for {@code Node} {@link Node#contributors contributors} says
     * that when a {@code Node} is retrieved using the {@link OsfService}, the "contributors" relationship should be
     * recursively retrieved in the same API call, and deserialized into a {@code List} of {@code Contributor}
     * objects.  Once the {@code Node} is retrieved, the caller can iterate over the {@code Contributor} objects
     * without issuing subsequent calls to {@link OsfService}.  This behavior is governed by the
     * {@code ResolutionStrategy} annotations on the model classes.
     * </p>
     * <p>
     * This interceptor will map a response based on attributes of the request (and test name).  This insures that
     * {@link OsfService} API calls which are recursive will result in proper responses.
     * </p>
     */
    public static class RecursiveInterceptor implements Interceptor {

        private static final Logger LOG = LoggerFactory.getLogger(RecursiveInterceptor.class);

        private final URI baseUri;

        private final TestName testName;

        private final Class testClass;

        private ResponseResolver resolver;

        public RecursiveInterceptor(TestName testName, Class testClass, URI baseUri) {
            this.baseUri = baseUri;
            this.testName = testName;
            this.testClass = testClass;

            resolver = (name, base, req) -> {
                // http://localhost:8000/v2/nodes/v8x57/files/osfstorage/ -> nodes/v8x57/files/osfstorage/
                URI relativizedRequestUri = baseUri.relativize(req);
                Path requestPath = Paths.get(relativizedRequestUri.getPath());

                // /json/NodeTest/testGetNodeObjectResolution/
                Path fsBase = Paths.get(resourceBase(testName, testClass));

                // If there's a "page" query parameter, use it to return 'index-0?.json'
                if (req.getQuery() != null && req.getQuery().contains("page=")) {
                    int startIndex = req.getQuery().indexOf("page=") + "page=".length();
                    // HACK unlikely to have double-digit pages
                    int page = Integer.parseInt(req.getQuery().substring(startIndex, startIndex + 1));
                    String jsonFile = String.format("index-0%s.json", page);
                    LOG.trace("Request carried 'page' parameter, using JSON resource {}", jsonFile);
                    return Paths.get(fsBase.toString(), requestPath.toString(), jsonFile);
                } else {
                    LOG.trace("  Request did not carry 'page' parameter.");
                }

                // If there is no "page" query parameter, and the request ends in a "/", and there is
                // no 'index.json' file, then see if there is an 'index-01.json' file, and return that.

                Path jsonPath = Paths.get(fsBase.toString(), requestPath.toString(), "index.json");
                if (req.getPath().endsWith("/")) {
                    // /json/NodeTest/testGetNodeObjectResolution/nodes/v8x57/files/osfstorage/index.json
                    if (this.getClass().getResource(jsonPath.toString()) != null) {
                        return jsonPath;
                    } else {
                        LOG.trace("  JSON resource {} does not exist.", jsonPath.toFile());
                    }

                    // If there's no 'index.json' file, then perhaps the request is for a paginated response
                    jsonPath = Paths.get(fsBase.toString(), requestPath.toString(), "index-01.json");
                    if (this.getClass().getResource(jsonPath.toString()) != null) {
                        return jsonPath;
                    } else {
                        LOG.trace("  JSON resource {} does not exist.", jsonPath.toFile());
                    }
                }

                throw new IllegalArgumentException(
                        String.format("Unable to resolve request %s to a classpath resource under %s", req, fsBase));

            };
        }

        public RecursiveInterceptor(TestName testName, Class testClass, URI baseUri, ResponseResolver resolver) {
            this.testName = testName;
            this.testClass = testClass;
            this.baseUri = baseUri;
            this.resolver = resolver;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request req = chain.request();
            LOG.debug("HTTP request: {}", req.urlString());

            // Resolve the request URI to a path on the filesystem.
            Path resourcePath = resolver.resolve(testName, baseUri, req.uri());
            LOG.debug("Response resource: {}", resourcePath.toString());

            req = req.newBuilder().addHeader(X_RESPONSE_RESOURCE, resourcePath.toString()).build();

            return chain.proceed(req);
        }
    }

    /**
     * Responsible for resolving the JSON response for OSF v2 API calls.
     */
    @FunctionalInterface
    public static interface ResponseResolver {

        /**
         * Resolves a JSON response document based on properties of the OSF API, the HTTP request, and the test being
         * executed.  This method should return a classpath resource containing the JSON for the HTTP response body.
         *
         * @param testName contains metadata about the current test method.
         * @param baseUri the baseUri of the OSF V2 API
         * @param requestUri the full request URI
         * @return a Path that identifies a classpath resource containing the JSON response document
         */
        Path resolve(TestName testName, URI baseUri, URI requestUri);

    }
}
