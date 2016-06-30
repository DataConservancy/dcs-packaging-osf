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

import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * Discovers the field annotated with {@code IndividualUri} on an OWL individual, and returns the value of that field.
 *
 * @param <T> the type of the object enclosing the individual instance
 * @param <U> the type of the individual instance, which contains the {@code IndividualUri} annotated field
 */
public final class IndividualUriTransform<T, U> implements BiFunction<T, U, String> {

    /**
     * {@inheritDoc}
     * <h3>Implementation note:</h3>
     * <p>
     * Discovers the field annotated with {@code IndividualUri} on the supplied {@code individual}, and returns the
     * string value of the field by invoking {@code toString()}.  The {@code outerObject} is ignored,
     * and may be {@code null}.
     * </p>
     * @param outerObject instance of the object that has the {@code individual} as a member
     * @param individual the instance of the class declaring the {@code IndividualUri} annotated field
     * @return the string form of {@code idInstance}, may be {@code null}
     * @throws IllegalArgumentException if {@code individual} is {@code null} or no field annotated with
     * {@code IndividualUri} is found on the {@code individual} class.
     */
    @Override
    @SuppressWarnings("unchecked")
    public String apply(T outerObject, U individual) {
        if (individual == null) {
            throw new IllegalArgumentException("Supplied individual must not be null.");
        }

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicReference atomicRef = new AtomicReference();
        ReflectionUtils.doWithFields(individual.getClass(), field -> {
            ReflectionUtils.makeAccessible(field);
            atomicRef.set(field.get(individual));
            found.set(true);
        }, field -> atomicRef.get() == null && AnnotationUtils.getAnnotation(field, IndividualUri.class) != null);

        if (atomicRef.get() == null && !found.get()) {
            throw new IllegalArgumentException(
                    String.format("Class '%s' is missing required annotation '%s'",
                            individual.getClass().getName(), IndividualUri.class.getName()));
        }

        if (atomicRef.get() == null) {
            return null;
        }

        return atomicRef.get().toString();
    }
}
