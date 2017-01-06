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

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class Checksum {

    enum Algorithm {
        SHA256,
        MD5
    }

    private final Algorithm algorithm;

    private final String value;

    /**
     * Constructs a new checksum composed of a checksum algorithm and a checksum value.
     *
     * @param algorithm the algorithm used to calculate the {@code value}
     * @param value the checksum value
     */
    public Checksum(final Algorithm algorithm, final String value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    /**
     * The algorithm used to calculate the {@link #getValue() value}.
     *
     * @return the algorithm
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * The checksum value.
     *
     * @return the checksum value
     */
    public String getValue() {
        return value;
    }
}
