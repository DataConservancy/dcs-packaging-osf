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
package org.dataconservancy.cos.osf.client.support;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.dataconservancy.cos.osf.client.config.OsfConfigurationService;

import java.io.IOException;

/**
 * Pins the OSF HTTP API to the specified version by adding an {@code Accept} HTTP header specifying the desired version
 * for each request.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 * @see <a href="https://api.osf.io/v2/#versioning">OSF API Versioning</a>
 * @see <a href="https://osf.io/y9jdt/wiki/Changelog/?view">OSF API changelog</a>
 */
public class ApiVersionInterceptor implements Interceptor {

    /**
     * Accept header template to be sent on each request.
     */
    static final String ACCEPT_HEADER = "application/vnd.api+json;version=%s";

    /**
     * The version of the OSF API being used.  Parameterizes {@link #ACCEPT_HEADER}.
     */
    private final String apiVersion;

    /**
     * Constructs this interceptor with the supplied version (e.g. {@code 2.2}).
     *
     * @param apiVersion the OSF API version
     */
    public ApiVersionInterceptor(final String apiVersion) {
        if (apiVersion == null || apiVersion.trim().length() == 0) {
            throw new IllegalArgumentException("API version must not be empty or null.");
        }
        this.apiVersion = apiVersion;
    }

    /**
     * Obtains the {@link org.dataconservancy.cos.osf.client.config.OsfClientConfiguration#getApiVersion() API version}
     * from the OSF configuration service.
     *
     * @param configurationService the OSF configuration service
     */
    public ApiVersionInterceptor(final OsfConfigurationService configurationService) {
        this(configurationService.getConfiguration().getApiVersion());
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request req = chain.request().newBuilder()
                                .addHeader("Accept", acceptHeader())
                                .build();

        return chain.proceed(req);
    }

    /**
     * Returns the value of the 'Accept' header added to the HTTP request by this interceptor.
     *
     * @return the 'Accept' header value
     */
    String acceptHeader() {
        return String.format(ACCEPT_HEADER, apiVersion);
    }

}
