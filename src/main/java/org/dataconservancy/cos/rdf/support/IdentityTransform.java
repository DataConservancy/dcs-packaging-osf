package org.dataconservancy.cos.rdf.support;

import java.util.function.Function;

/**
 * Function that returns the supplied argument with no additional processing.
 *
 * @param <T> the type of the supplied and returned argument
 */
public class IdentityTransform<T> implements Function<T, T> {

    /**
     * {@inheritDoc}
     * <h3>Implementation note:</h3>
     * <p>
     * Simply returns the supplied object.
     * </p>
     *
     * @param t {@inheritDoc}
     * @return the supplied object, {@code t}
     */
    @Override
    public T apply(T t) {
        return t;
    }

}
