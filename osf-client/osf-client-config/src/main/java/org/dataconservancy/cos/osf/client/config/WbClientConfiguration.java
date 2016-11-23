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

/**
 * Encapsulates the configuration parameters needed by an HTTP-based Waterbutler client
 */
public class WbClientConfiguration {

    private String host;

    private int port;

    private String scheme;

    private String basePath;

    /**
     * The host of the Waterbutler API; may be a DNS name or a dotted-quad IP.
     *
     * @return the API host
     */
    public String getHost() {
        return host;
    }

    /**
     * The host of the Waterbutler API; may be a DNS name or a dotted-quad IP.
     *
     * @param host the API host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * The port of the Waterbutler API.
     *
     * @return the API port
     */
    public int getPort() {
        return port;
    }

    /**
     * The port of the Waterbutler API.
     *
     * @param port the API port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * The protocol scheme used to communicate with the API (e.g. "http", "https")
     *
     * @return the protocol scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * The protocol scheme used to communicate with the API (e.g. "http", "https")
     *
     * @param scheme the protocol scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * The base path of the API, e.g. {@code "/v1/"}.
     *
     * @return the base path of the API
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * The base path of the API, e.g. {@code "/v1/"}.
     *
     * @param basePath the base path of the API
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
}
