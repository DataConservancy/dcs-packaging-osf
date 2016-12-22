/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.rdf.annotations;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Insures the various constants of {@link OwlProperty} work as expected.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class OwlPropertyTest {

    private OwlProperty owlProperty;

    @Before
    public void setUp() throws Exception {
        owlProperty = TestModelClass.class.getDeclaredField("id").getAnnotation(OwlProperty.class);
        assertNotNull(owlProperty);
    }

    /**
     * Asserts the default mode is TransformMode.FIELD.
     * @throws Exception
     */
    @Test
    public void testDefaultTransformMode() throws Exception {
        assertEquals(TransformMode.FIELD, owlProperty.mode());
    }

    /**
     * Asserts the default transform function field aligns with the actual default transform function.
     * @throws Exception
     */
    @Test
    public void testDefaultTransform() throws Exception {
        assertEquals(OwlProperty.DEFAULT_TRANSFORM_FUNCTION, owlProperty.transform());
    }

    /**
     * Asserts that the {@code transform()} can be retrieved via reflection using the
     * {@link OwlProperty#TRANSFORM} constant.
     * @throws Exception
     */
    @Test
    public void testTransformConstant() throws Exception {
        assertNotNull(OwlProperty.class.getMethod(OwlProperty.TRANSFORM));
    }

    /**
     * Asserts that the {@code mode()} can be retrieved via reflection using the
     * {@link OwlProperty#TRANSFORM_MODE} constant.
     * @throws Exception
     */
    @Test
    public void testTransformModeConstant() throws Exception {
        assertNotNull(OwlProperty.class.getMethod(OwlProperty.TRANSFORM_MODE));
    }

}
