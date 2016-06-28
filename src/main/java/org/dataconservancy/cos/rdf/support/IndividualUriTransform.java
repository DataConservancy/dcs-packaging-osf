/*
 * Copyright 2016 Johns Hopkins University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dataconservancy.cos.rdf.support;

import java.util.function.BiFunction;

/**
 * Function that transforms its argument to a {@code String}
 *
 * @param <U> the type of the object enclosing the {@code IndividualUri} annotated field
 * @param <T> the type of the {@code IndividualUri} annotated field, typically a {@code String}
 */
public final class IndividualUriTransform<U, T> implements BiFunction<U, T, String> {

    /**
     * {@inheritDoc}
     * <h3>Implementation note:</h3>
     * <p>
     * Applies the {@code toString()} method on the supplied object.
     * </p>
     * @param enclosingObject instance of the object that has the {@code IndividualUri} annotated field as a member
     * @param idInstance the instance of the {@code IndividualUri} annotated field
     * @return the string form of {@code idInstance}
     */
    @Override
    public String apply(U enclosingObject, T idInstance) {
        return idInstance.toString();
    }

}
