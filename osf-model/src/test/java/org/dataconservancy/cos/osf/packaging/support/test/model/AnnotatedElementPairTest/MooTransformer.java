package org.dataconservancy.cos.osf.packaging.support.test.model.AnnotatedElementPairTest;

import java.util.function.BiFunction;

/**
 * A simple transformer.
 */
public class MooTransformer implements BiFunction<Foo, String, String> {

    @Override
    public String apply(Foo enclosingObject, String idInstance) {
        return "Moo!";
    }

}
