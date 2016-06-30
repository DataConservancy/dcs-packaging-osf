package org.dataconservancy.cos.rdf.support.test.model.AnnotatedElementPairTest;

import java.util.function.BiFunction;

/**
 * A simple transformer.
 */
public class MooTransformer implements BiFunction<Object, Foo, String> {

    @Override
    public String apply(Object outerObject, Foo individual) {
        return "Moo!";
    }

}
