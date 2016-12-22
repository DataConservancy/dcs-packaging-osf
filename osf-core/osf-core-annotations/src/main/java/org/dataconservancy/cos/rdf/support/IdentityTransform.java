/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.rdf.support;

import java.util.function.Function;

/**
 * Function that returns the supplied argument with no additional processing.  This class exists because
 * {@link Function#identity()} cannot be the target of an annotation attribute.
 *
 * @param <T> the type of the supplied and returned argument
 * @author Elliot Metsger (emetsger@jhu.edu)
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
    public T apply(final T t) {
        return Function.<T>identity().apply(t);
    }

}
