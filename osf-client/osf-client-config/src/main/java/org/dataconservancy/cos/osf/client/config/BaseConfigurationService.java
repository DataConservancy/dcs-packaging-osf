/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.osf.client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Abstract base class for client configuration.  Provides a method to subclasses for resolving a classpath resource
 * containing the client configuration.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public abstract class BaseConfigurationService {

    static final Logger LOG = LoggerFactory.getLogger(BaseConfigurationService.class);

    /**
     * Error message when a classpath resource cannot be resolved.  Parameters are: name of the resource, error message.
     */
    static final String ERR_RESOLVE_RESOURCE = "Error resolving classpath resource %s: %s";

    /**
     * Error message when a classpath resource cannot be read.  Parameters are: name of the resource, error message.
     */
    static final String ERR_READING_RESOURCE = "Error reading classpath resource %s: %s";

    /**
     * The classpath resource that contains the client configuration.  Subclasses are responsible for setting this
     * member, typically by invoking {@link #getConfigurationResource(String)}.
     */
    String configurationResource;

    /**
     * Resolve the supplied classpath resource to a URL.  If the resource is not able to be resolved, a
     * {@code RuntimeException} is thrown.
     *
     * @param configurationResource the classpath resource containing the client configuration
     * @return the URL of the resolved resource
     * @throws RuntimeException if the classpath resource cannot be resolved
     */
    public static URL getConfigurationResource(final String configurationResource) {

        if (configurationResource == null || configurationResource.trim().length() == 0) {
            throw new IllegalArgumentException("Supplied resource path must not be null or empty.");
        }

        final Resource springResource;

        if (configurationResource.startsWith("file:") || configurationResource.startsWith("http")) {
            try {
                springResource = new UrlResource(configurationResource);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Unable to create a file: URL from the supplied configuration resource '" +
                        configurationResource + "': " + e.getMessage(), e);
            }
        } else if (configurationResource.startsWith("classpath:")) {
            springResource = new ClassPathResource(configurationResource.substring("classpath:".length()));
        } else if (configurationResource.startsWith("classpath*:")) {
            springResource = new ClassPathResource(configurationResource.substring("classpath*:".length()));
        } else {
            springResource = new ClassPathResource(configurationResource);
        }

        URL configUrl = null;
        try {
            configUrl = springResource.getURL();
        } catch (IOException e) {
            throw new RuntimeException("Unable to obtain a URL from the Spring configuration resource '" +
                    springResource + "': " + e.getMessage(), e);
        }

        if (configUrl == null) {
            throw new RuntimeException(
                    String.format(ERR_RESOLVE_RESOURCE, configurationResource, "not found!"));
        }

        return configUrl;
    }

}
