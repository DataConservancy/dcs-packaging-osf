package org.dataconservancy.cos.osf.packaging.support.test.model.AnnotatedElementPairTest;

import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.TransformMode;

/**
 * Test class sharing the name annotation and field name with the Bar test class.
 */
public class Foo {

    @IndividualUri(mode = TransformMode.CLASS, transform = MooTransformer.class)
    String id = "foo";

}
