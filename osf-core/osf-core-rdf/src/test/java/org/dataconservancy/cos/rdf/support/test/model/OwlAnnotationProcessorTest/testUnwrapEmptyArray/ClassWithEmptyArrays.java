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
package org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testUnwrapEmptyArray;

/**
 * Test class with fields that are arrays.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@SuppressWarnings("checkstyle:hideutilityclassconstructor")
public class ClassWithEmptyArrays {

    /**
     * field of Integer[] set to a zero length array
     */
    public static final Integer[] ARRAY_OF_INTEGER = new Integer[]{};

    /**
     * field of ClassFoo[] set to a zero length array
     */
    public static final ClassFoo[] ARRAY_OF_FOO_EMPTY_ARRAY = new ClassFoo[]{};

    /**
     * field of ClassFoo[] set to null
     */
    public static final ClassFoo[] ARRAY_OF_FOO_NULL = null;

    /**
     * field of ClassFoo[] set to a array of length 1 (initialized with {@code null} values)
     */
    public static final ClassFoo[] ARRAY_OF_FOO_WITH_SIZE = new ClassFoo[1];

    public ClassWithEmptyArrays() {

    }

    public static class ClassFoo {

    }

}
