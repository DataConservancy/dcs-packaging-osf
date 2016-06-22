package org.dataconservancy.cos.rdf.support.test.model.AnnotatedElementPairTest;

import java.util.function.Function;

/**
 * A simple transformer.
 */
public class MooTransformer implements Function<Object, String> {

    @Override
    public String apply(Object foo) {
        return "Moo!";
    }

}
