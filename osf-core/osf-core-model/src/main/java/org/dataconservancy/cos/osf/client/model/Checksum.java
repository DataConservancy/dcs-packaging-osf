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
 * Created by esm on 5/2/16.
 */
public class Checksum {

    enum Algorithm {
        SHA_256,
        MD5
    }

    private Algorithm algorithm;

    private String value;

    public Checksum(Algorithm algorithm, String value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public String getValue() {
        return value;
    }
}
