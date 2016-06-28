package org.dataconservancy.cos.rdf.support.test.model.AnnotatedElementPairTest;

import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.TransformMode;

/**
 * Test class sharing the name annotation and field name with the Bar test class.
 */
public class Foo {

    @IndividualUri(transform = MooTransformer.class)
    String id = "foo";

}
