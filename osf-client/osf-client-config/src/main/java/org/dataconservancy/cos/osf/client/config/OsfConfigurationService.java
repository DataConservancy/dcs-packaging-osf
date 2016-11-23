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
 * Factory responsible for returning populated instances of {@link OsfClientConfiguration}.
 */
public interface OsfConfigurationService {

    /**
     * Return an instance of the OSF client configuration.  Should never be {@code null}.
     *
     * @return the OSF client configuration
     */
    OsfClientConfiguration getConfiguration();

}
