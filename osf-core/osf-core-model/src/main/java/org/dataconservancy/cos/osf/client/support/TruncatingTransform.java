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
package org.dataconservancy.cos.osf.client.support;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Truncates strings to the specified limit, splitting on horizontal whitespace characters
 *
 * @see java.util.regex.Pattern
 */
public class TruncatingTransform implements Function<String, String> {

    /**
     * The default number of "words" returned by this transformer
     */
    public static final int DEFAULT_LIMIT = 10;

    /**
     * The specified number of "words" returned by this transformer, defaults to {@link #DEFAULT_LIMIT}
     */
    private int limit = DEFAULT_LIMIT;

    /**
     * Truncates strings to the default limit.
     */
    public TruncatingTransform() {

    }

    /**
     * Truncates strings to the specified limit.  A limit less than one will result in the string <em>not</em> being
     * transformed at all.
     *
     * @param limit specified number of "words" returned by this transformer
     */
    public TruncatingTransform(int limit) {
        this.limit = limit;
    }

    /**
     * Splits the string into parts using horizontal whitespace characters, and returns the number of specified parts
     * joined by spaces, and ending with a trailing ellipse.  If the limit is less than 1, or the number of parts is
     * less than the limit, the string is returned as is.  If the number of parts is greater than the limit, then
     * <em>limit</em> parts are joined using the space character and an ellipse is appended before the transformed
     * string is returned.
     *
     * @param s the string to transform
     * @return the transformed string
     * @see java.util.regex.Pattern#split(CharSequence)
     */
    @Override
    public String apply(String s) {
        if (limit < 1) {
            return s;
        }

        if (s != null && s.trim().length() > 0) {
            String[] parts = s.split("\\h");

            if (parts.length < limit) {
                return s;
            }

            AtomicInteger count = new AtomicInteger(0);

            String toReturn = Stream.of(parts)
                    .filter((part) -> count.incrementAndGet() < limit)
                    .collect(Collectors.joining(" "));

            return toReturn + " ... ";
        }

        return s;
    }

}
