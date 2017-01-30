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
package org.dataconservancy.cos.osf.client.config;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Encapsulates the configuration parameters needed by an HTTP-based OSF client.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class OsfClientConfiguration extends BaseConfiguration {

    private static final String DEFAULT_API_VERSION = "2.2";

    private String authHeader;

    private String apiVersion = DEFAULT_API_VERSION;

    /**
     * The Basic authentication header that should be sent on every HTTP request, e.g.
     * {@code Basic ZW1ldHNnZaodnr1haWwuY29to9b2b2JhcmJheg==}
     *
     * @return the authentication header
     */
    public String getAuthHeader() {
        return authHeader;
    }

    /**
     * The Basic authentication header that should be sent on every HTTP request, e.g.
     * {@code Basic ZW1ldHNnZaodnr1haWwuY29to9b2b2JhcmJheg==}
     *
     * @param authHeader the authentication header
     */
    public void setAuthHeader(final String authHeader) {
        this.authHeader = authHeader;
    }

    /**
     * The protocol scheme used to communicate with the v2 API (e.g. "http", "https")
     *
     * @return the protocol scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * The protocol scheme used to communicate with the v2 API (e.g. "http", "https")
     *
     * @param scheme the protocol scheme
     */
    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * Obtain the base URI used to communicate with the v2 API.
     *
     * @return the base URI of the OSF v2 API
     */
    public URI getBaseUri() {
        try {
            return new URI(scheme, null, host, port, basePath, null, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Specified version of the OSF API to use (e.g. {@code 2.2})
     *
     * @return the API version, defaults to "2.2"
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * Specified version of the OSF API to use (e.g. {@code 2.2})
     *
     * @param apiVersion the API version
     */
    public void setApiVersion(final String apiVersion) {
        this.apiVersion = apiVersion;
    }

}
