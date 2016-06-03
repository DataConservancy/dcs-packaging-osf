package org.dataconservancy.cos.rdf.support;

import java.util.function.Function;

/**
 * Created by esm on 6/3/16.
 */
public class IdentityTransform<T> implements Function<T, T> {

    @Override
    public T apply(T t) {
        return t;
    }

}
