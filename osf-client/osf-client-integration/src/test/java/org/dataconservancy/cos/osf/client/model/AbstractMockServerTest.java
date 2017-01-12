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
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.io.IOUtils;
import org.dataconservancy.cos.osf.client.config.DefaultOsfJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.DefaultWbJacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.JacksonConfigurer;
import org.dataconservancy.cos.osf.client.config.OsfClientConfiguration;
import org.dataconservancy.cos.osf.client.config.WbClientConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestName;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockserver.model.HttpCallback.callback;
import static org.mockserver.model.HttpRequest.request;

/**
 * Test fixture providing a {@link MockServerClient} used to configure HTTP expectations.
 * <p>
 * Starts two HTTP servers that typically listen on port 8000 and 7777, respectively the OSF API and Waterbutler
 * endpoints.  The HTTP server does not use the HTTP request URI to respond to requests.  Instead, it expects a
 * {@link #X_RESPONSE_RESOURCE HTTP header} to carry a resource path that is used for the response.
 * </p>
 * <p>
 * An OkHttp {@code Interceptor} is used to add the header to the request, and a MockServer callback is used to resolve
 * the resource path in the header to a HTTP response.
 * </p>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public abstract class AbstractMockServerTest extends AbstractOsfClientTest {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractMockServerTest.class);

    /**
     * Custom HTTP header sent by a test HTTP request, used to tell the {@code MockServer} what JSON document
     * to return.  Header values are interpreted as classpath resources.
     *
     * @see RecursiveInterceptor
     * @see NodeResponseCallback
     */
    public static final String X_RESPONSE_RESOURCE = "X-Response-Resource";

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient mockServer;

    /**
     * Set by {@link #startMockServer()}.
     */
    static MockServerClient wbMockServer;

    /**
     * Starts mock HTTP servers on the port specified by the OSF client configuration and the Waterbutler client
     * configuration
     */
    @Before
    public void startMockServer() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();

        final JacksonConfigurer<OsfClientConfiguration> osfConfigurer = new DefaultOsfJacksonConfigurer<>();
        final JacksonConfigurer<WbClientConfiguration> wbConfigurer = new DefaultWbJacksonConfigurer<>();

        final ResourceLoader loader = new DefaultResourceLoader();

        final Resource configuration = loader.getResource(getOsfServiceConfigurationResource());
        assertTrue("Unable to resolve configuration resource: '" + getOsfServiceConfigurationResource() + "'",
                configuration.exists());

        mockServer = ClientAndServer.startClientAndServer(
                osfConfigurer.configure(
                        mapper.readTree(IOUtils.toString(configuration.getURL(), "UTF-8")),
                        mapper,
                        OsfClientConfiguration.class
                ).getPort()
        );

        wbMockServer = ClientAndServer.startClientAndServer(
                wbConfigurer.configure(
                        mapper.readTree(IOUtils.toString(configuration.getURL(), "UTF-8")),
                        mapper,
                        WbClientConfiguration.class
                ).getPort()
        );

        /* Sets up the expectations of the mock http server.
         *
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
     * Stops mock HTTP servers
     */
    @After
    public void stopMockServer() throws Exception {
        mockServer.stop();
        wbMockServer.stop();
    }

    /**
     * Constructs a resource path by appending the supplied {@code prefix} to the name of the test class and the name
     * of the test method.  For example, given a prefix of {@code /prefix/}, a test named {@code FooTest}, and a test
     * method named {@code myTestMethod}, the resource path {@code /prefix/FooTest/myTestMethod/} would be returned.
     * Note the returned resource path will always end with a forward slash.  If the supplied prefix is absolute (e.g.
     * begins with a forward slash), the returned resource path with also be absolute.
     *
     * @param testName used to obtain the name of the test method
     * @param testClass used to obtain the name of the test class
     * @param prefix prefixes the resource path
     * @return the resource path ending with a forward slash
     */
    public static String resourcePathFrom(final TestName testName, final Class testClass, final String prefix) {
        assertNotNull(testName);
        final StringBuilder base = new StringBuilder(prefix);
        base.append(testClass.getSimpleName()).append("/");
        base.append(testName.getMethodName()).append("/");

        LOG.trace("Test resource base path: {}", base);
        return base.toString();
    }

    /**
     * Constructs a resource path from a HTTP uri.  Given a uri {@code http://localhost:8000/v2/nodes/abc123/}, this
     * method returns a resource path {@code localhost/8000/v2/nodes/abc123}.  Note the absence of forward slashes at
     * the beginning and end of the resource path.
     *
     * @param uri the URI used to form a resource path
     * @return the resource path, with no preceeding or trailing slashes
     */
    public static String resourcePathFrom(final URI uri) {
        if (!uri.getScheme().startsWith("http")) {
            throw new IllegalArgumentException("Only operates on http or https schemes." +
                    "  Scheme was: " + uri.getScheme());
        }

        if (uri.getPort() < 0) {
            return new java.io.File(uri.getHost(), uri.getPath()).getPath();
        }

        final java.io.File path = new java.io.File(uri.getHost(), String.valueOf(uri.getPort()));

        return new java.io.File(path, uri.getPath()).getPath();
    }

    /**
     * Adds an interceptor to the OSF service factory which adds a {@code X_RESPONSE_RESOURCE} header to the HTTP
     * request.  Test classes can use this instead of the {@link RecursiveInterceptor}/{@link ResponseResolver}
     * combination if the tests do not need to resolve relationships.
     *
     * @param resourcePath the resource to resolve and return in the response.
     */
    protected void addResponseInterceptor(final String resourcePath) {
        factory.interceptors().add(chain ->
                chain.proceed(chain.request()
                        .newBuilder()
                        .addHeader(X_RESPONSE_RESOURCE, resourcePath)
                        .build()));
    }

    /**
     * Responsible for resolving the JSON response resource for OSF v2 API calls that are recursive.
     * <p>
     * For example, the {@link com.github.jasminb.jsonapi.ResolutionStrategy} for {@code Node}
     * {@link Node#contributors contributors} says that when a {@code Node} is retrieved using the
     * {@link org.dataconservancy.cos.osf.client.service.OsfService}, the "contributors" relationship should be
     * recursively retrieved in the same API call, and deserialized into a {@code List} of {@code Contributor} objects.
     * Once the {@code Node} is retrieved, the caller can iterate over the {@code Contributor} objects without issuing
     * subsequent calls to {@link org.dataconservancy.cos.osf.client.service.OsfService}.  This behavior is governed by
     * the {@code ResolutionStrategy} annotations on the model classes.
     * </p>
     * <p>
     * This interceptor will map a response based on attributes of the request (and test name).  This insures that
     * {@link org.dataconservancy.cos.osf.client.service.OsfService} API calls which are recursive will result in proper
     * responses.
     * </p>
     */
    public static class RecursiveInterceptor implements Interceptor {

        private static final Logger LOG = LoggerFactory.getLogger(RecursiveInterceptor.class);

        private ResponseResolver resolver;

        /**
         * Constructs an interceptor that will look for test resources under the resource path formed by the class
         * name and the test method name.  This constructor hard-codes a prefix of {@code /json/} to the resource path
         * formed by the test method and test class; effectively this constructor is the same as calling
         * {@link RecursiveInterceptor#RecursiveInterceptor(TestName, Class, String)} with the string {@code /json/}.
         * <p>
         * Classpath resources will be resolved using the {@code testClass} class loader.
         * </p>
         *
         * @param testName used to obtain the test method being executed
         * @param testClass the test being executed
         * @see {@link #RecursiveInterceptor(TestName, Class, String)}
         */
        @Deprecated
        public RecursiveInterceptor(final TestName testName, final Class testClass) {
            this(testName, testClass, "/json/");
        }

        /**
         * Constructs an interceptor that will look for test resources under the resource path formed by the class
         * name and the test method name.  The string {@code jsonRoot} will be prefixed to the resource path formed by
         * the test method and test class.
         * <p>
         * Classpath resources will be resolved using the {@code testClass} class loader.
         * </p>
         *
         * @param testName used to obtain the test method being executed
         * @param testClass the test being executed
         * @param jsonRoot string prefixed to the resource path
         */
        public RecursiveInterceptor(final TestName testName, final Class testClass, final String jsonRoot) {
            this(resourcePathFrom(testName, testClass, jsonRoot), testClass);
        }

        /**
         * Constructs an interceptor that will look for test resources under the resource path specified by
         * {@code resourceBase}.
         * <p>
         * Classpath resources will be resolved using the {@code testClass} class loader.
         * </p>
         *
         * @param resourceBase the base classpath resource used to resolve responses
         * @param testClass the test being executed
         */
        public RecursiveInterceptor(final String resourceBase, final Class<?> testClass) {
            resolver = (req) -> {
                // http://localhost:8000/v2/nodes/v8x57/files/osfstorage/ ->
                // localhost/8000/v2/nodes/v8x57/files/osfstorage/
                final String requestPath = resourcePathFrom(req) + "/";

                // If there's a "page" query parameter, use it to return 'index-0?.json'
                if (req.getQuery() != null && req.getQuery().contains("page=")) {
                    final int startIndex = req.getQuery().indexOf("page=") + "page=".length();
                    // HACK unlikely to have double-digit pages
                    final int page = Integer.parseInt(req.getQuery().substring(startIndex, startIndex + 1));
                    final String jsonFile = String.format("index-0%s.json", page);
                    LOG.trace("Request carried 'page' parameter, using JSON resource {}", jsonFile);
                    return resourceBase + requestPath + jsonFile;
                } else {
                    LOG.trace("  Request did not carry 'page' parameter.");
                }

                // If there is no "page" query parameter, and the request ends in a "/", and there is
                // no 'index.json' file, then see if there is an 'index-01.json' file, and return that.

                String jsonPath = resourceBase + requestPath + "index.json";
                if (req.getPath().endsWith("/")) {
                    // /json/NodeTest/testGetNodeObjectResolution/localhost/8000/nodes/v8x57/files/osfstorage/index.json
                    if (testClass.getResource(jsonPath) != null) {
                        return jsonPath;
                    } else {
                        LOG.debug("  JSON resource '{}' does not exist.", jsonPath);
                    }

                    // If there's no 'index.json' file, then perhaps the request is for a paginated response
                    jsonPath = resourceBase + requestPath + "index-01.json";
                    if (testClass.getResource(jsonPath) != null) {
                        return jsonPath;
                    } else {
                        LOG.trace("  JSON resource '{}' does not exist.", jsonPath);
                        throw new IllegalArgumentException(
                                String.format("Unable to resolve request '%s' to a classpath resource '%s'",
                                        req, jsonPath));
                    }
                } else {
                    // This is binary content, e.g.: /v1/resources/vae86/providers/osfstorage/57570a07c7950c0045ac8051
                    jsonPath = resourceBase + requestPath;
                    if (testClass.getResource(jsonPath) != null) {
                        return jsonPath;
                    } else {
                        LOG.debug("  JSON resource '{}' does not exist.", jsonPath);
                        throw new IllegalArgumentException(
                                String.format("Unable to resolve request '%s' to a classpath resource '%s'",
                                        req, jsonPath));
                    }
                }
            };
        }

        /**
         * Constructs an interceptor with all resolution logic contained in the supplied {@code resolver}.
         *
         * @param resolver responsible for resolving test resources
         */
        public RecursiveInterceptor(final ResponseResolver resolver) {
            this.resolver = resolver;
        }

        /**
         * {@inheritDoc}
         * <p>
         * Implementation notes: adds a HTTP header {@code X-Response-Resource} to the request with a value obtained by
         * invoking {@link ResponseResolver#resolve(URI)}.  The mock HTTP server will treat the value of the header as
         * a classpath resource, resolving the resource and returning it in the response.
         * </p>
         * @param chain {@inheritDoc}
         * @return {@inheritDoc}
         * @throws IOException {@inheritDoc}
         */
        @Override
        public Response intercept(final Chain chain) throws IOException {
            Request req = chain.request();
            LOG.debug("HTTP request: {}", req.urlString());

            // Resolve the request URI to a path on the filesystem.
            final String resourcePath = resolver.resolve(req.uri());
            LOG.debug("Response resource: {}", resourcePath);

            req = req.newBuilder().addHeader(X_RESPONSE_RESOURCE, resourcePath).build();

            return chain.proceed(req);
        }
    }

    /**
     * Responsible for resolving the JSON response for OSF v2 API calls.
     */
    @FunctionalInterface
    public static interface ResponseResolver {

        /**
         * Resolves a JSON response document based on the HTTP request.  This method returns a classpath resource
         * containing the JSON for the HTTP response body.
         *
         * @param requestUri the full request URI
         * @return a classpath resource containing the JSON response document
         */
        String resolve(URI requestUri);

    }
}
