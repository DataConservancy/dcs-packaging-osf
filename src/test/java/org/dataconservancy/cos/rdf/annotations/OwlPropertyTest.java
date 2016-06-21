package org.dataconservancy.cos.rdf.annotations;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Insures the various constants of {@link OwlProperty} work as expected.
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
     * Asserts that the {@code transform()} can be retrieved via reflection using the {@link OwlProperty#TRANSFORM} constant.
     * @throws Exception
     */
    @Test
    public void testTransformConstant() throws Exception {
        assertNotNull(OwlProperty.class.getMethod(OwlProperty.TRANSFORM));
    }

    /**
     * Asserts that the {@code mode()} can be retrieved via reflection using the {@link OwlProperty#TRANSFORM_MODE} constant.
     * @throws Exception
     */
    @Test
    public void testTransformModeConstant() throws Exception {
        assertNotNull(OwlProperty.class.getMethod(OwlProperty.TRANSFORM_MODE));
    }

}
