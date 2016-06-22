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
package org.dataconservancy.cos.osf.packaging.support.test.model.OwlAnnotationProcessorTest.testEnum;

import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.OwlProperties;

/**
 * Created by esm on 6/9/16.
 */
public enum AnEnum {

    FOO ("foo"),

    @OwlProperty(OwlProperties.DCTERMS_DESCRIPTION)
    BAR ("bar"),
    BAZ ("baz");

    private final String value;

    private AnEnum(String value) {
        this.value = value;
    }

}
