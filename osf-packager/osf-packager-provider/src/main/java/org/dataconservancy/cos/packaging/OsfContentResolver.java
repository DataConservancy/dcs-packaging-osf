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

import java.io.InputStream;

/**
 * Accepts a URI with location semantics, resolves the URI, and returns the bytes of the resource's default
 * representation.  Typically used to resolve binary resources that have a single representation.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@FunctionalInterface
public interface OsfContentResolver {

    /**
     * Resolves the supplied {@code contentUri} to a byte stream.
     *
     * @param contentUri a URI with location semantics that is expected to resolve to a byte stream
     * @return the bytestream
     * @throws RuntimeException if there is an error resolving the {@code contentUri}
     */
    public InputStream resolve(String contentUri) throws RuntimeException;

}
