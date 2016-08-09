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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests covering the JodaSupport utility class.
 */
public class JodaSupportTest {

    private static final String TO_BE_PARSED_NO_TZ = "2016-06-03T17:53:52.434000";

    private static final String PARSED_EXPECTED = "2016-06-03T17:53:52.434Z";

    /**
     * Insures that a timezone-less string matching pattern {@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS} is parsed using UTC.
     */
    @Test
    public void testDateTimeFormatterUtc() {
        assertEquals(PARSED_EXPECTED, JodaSupport.DATE_TIME_FORMATTER.parseDateTime(TO_BE_PARSED_NO_TZ).toString());
    }

}