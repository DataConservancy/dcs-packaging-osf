package org.dataconservancy.cos.rdf.annotations;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Insures the various constants of {@link IndividualUri} work as expected.
 */
public class IndividualUriTest {

    private IndividualUri individualUri;

    @Before
    public void setUp() throws Exception {
        individualUri = TestModelClass.class.getDeclaredField("id").getAnnotation(IndividualUri.class);
        assertNotNull(individualUri);
    }

    /**
     * Asserts the default mode is TransformMode.FIELD.
     * @throws Exception
     */
    @Test
    public void testDefaultTransformMode() throws Exception {
        assertEquals(TransformMode.FIELD, individualUri.mode());
    }

    /**
     * Asserts the default transform function field aligns with the actual default transform function.
     * @throws Exception
     */
    @Test
    public void testDefaultTransform() throws Exception {
        assertEquals(IndividualUri.DEFAULT_TRANSFORM_FUNCTION, individualUri.transform());
    }

    /**
     * Asserts that the {@code transform()} can be retrieved via reflection using the {@link IndividualUri#TRANSFORM} constant.
     * @throws Exception
     */
    @Test
    public void testTransformConstant() throws Exception {
        assertNotNull(IndividualUri.class.getMethod(IndividualUri.TRANSFORM));
    }

    /**
     * Asserts that the {@code mode()} can be retrieved via reflection using the {@link IndividualUri#TRANSFORM_MODE} constant.
     * @throws Exception
     */
    @Test
    public void testTransformModeConstant() throws Exception {
        assertNotNull(IndividualUri.class.getMethod(IndividualUri.TRANSFORM_MODE));
    }
}
