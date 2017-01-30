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
package org.dataconservancy.cos.osf.client.config;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
abstract class BaseConfiguration {

    String host;

    int port;

    String scheme;

    String basePath;

    int read_timeout_ms = 30 * 1000; // 30 seconds

    int write_timeout_ms = 10 * 1000; // 10 seconds

    int connect_timeout_ms = 10 * 1000; // 10 seconds

    /**
     * The remote host of the API; may be a DNS name or a dotted-quad IP.
     * <p>
     * e.g. "localhost", "127.0.0.1"
     * </p>
     *
     * @return the API host
     */
    public String getHost() {
        return host;
    }

    /**
     * The host of the API; may be a DNS name or a dotted-quad IP.
     * <p>
     * e.g. "localhost", "127.0.0.1"
     * </p>
     *
     * @param host the API host
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * The remote port of the API.
     * <p>
     * e.g. "80", "8080", "443"
     * </p>
     *
     * @return the API port
     */
    public int getPort() {
        return port;
    }

    /**
     * The report port of the API.
     * <p>
     * e.g. "80", "8080", "443"
     * </p>
     *
     * @param port the API port
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * The protocol scheme used to communicate with the API.
     * <p>
     * e.g. "http", "https"
     * </p>
     *
     * @return the protocol scheme
     */
    public String getScheme() {
        return scheme;
    }

    /**
     * The protocol scheme used to communicate with the API.
     * <p>
     * e.g. "http", "https"
     * </p>
     *
     * @param scheme the protocol scheme
     */
    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * The base URL path of the API.
     * <p>
     * e.g. {@code "/v1/"}, {@code "/v2/"}
     * </p>
     *
     * @return the base path of the API
     */
    public String getBasePath() {
        return basePath;
    }

    /**
     * The base URL path of the API.
     * <p>
     * e.g. {@code "/v1/"}, {@code "/v2/"}
     * </p>
     *
     * @param basePath the base path of the API
     */
    public void setBasePath(final String basePath) {
        this.basePath = basePath;
    }

    /**
     * Timeout when waiting for data to read from an open socket.
     *
     * @return timeout in milliseconds, must be greater than -1
     */
    public int getRead_timeout_ms() {
        return read_timeout_ms;
    }

    /**
     * Timeout when waiting for data to read from an open socket.
     *
     * @param read_timeout_ms timeout in milliseconds, must be greater than -1
     */
    public void setRead_timeout_ms(final int read_timeout_ms) {
        if (read_timeout_ms < 0) {
            throw new IllegalArgumentException("Read timeout must be a positive integer");
        }
        this.read_timeout_ms = read_timeout_ms;
    }

    /**
     * Timeout when waiting for data to written to an open socket.
     *
     * @return timeout in milliseconds, must be greater than -1
     */
    public int getWrite_timeout_ms() {
        return write_timeout_ms;
    }

    /**
     * Timeout when waiting for data to be written to an open socket.
     *
     * @param write_timeout_ms timeout in milliseconds, must be greater than -1
     */
    public void setWrite_timeout_ms(final int write_timeout_ms) {
        if (write_timeout_ms < 0) {
            throw new IllegalArgumentException("Write timeout must be a positive integer");
        }
        this.write_timeout_ms = write_timeout_ms;
    }

    /**
     * Timeout when waiting to connect to a socket.
     *
     * @return timeout in milliseconds, must be greater than -1
     */
    public int getConnect_timeout_ms() {
        return connect_timeout_ms;
    }

    /**
     * Timeout when waiting to connect to a socket.
     *
     * @param connect_timeout_ms timeout in milliseconds, must be greater than -1
     */
    public void setConnect_timeout_ms(final int connect_timeout_ms) {
        if (connect_timeout_ms < 0) {
            throw new IllegalArgumentException("Connect timeout must be a positive integer");
        }
        this.connect_timeout_ms = connect_timeout_ms;
    }
}
