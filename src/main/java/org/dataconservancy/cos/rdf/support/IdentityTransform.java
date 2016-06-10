package org.dataconservancy.cos.rdf.support;

import java.util.function.Function;

/**
 * Function that returns the supplied argument with no additional processing.  This class exists because
 * {@link Function#identity()} cannot be the target of an annotation attribute.
 *
 * @param <T> the type of the supplied and returned argument
 */
public final class IdentityTransform<T> implements Function<T, T> {

    /**
     * {@inheritDoc}
     * <h3>Implementation note:</h3>
     * <p>
     * Forwards to {@link Function#identity()}, and returns.
     * </p>
     *
     * @param t {@inheritDoc}
     * @return the supplied object, {@code t}
     */
    @Override
    public T apply(T t) {
        return Function.<T>identity().apply(t);
    }

}
