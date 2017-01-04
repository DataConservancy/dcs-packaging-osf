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

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esm on 5/2/16.
 * @author esm
 * @author khanson
 * 
 */
public class JodaSupport {

    /**
     * A Joda DateTimeFormatter which parses timezone-less strings as UTC using the pattern:
     * {@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS}
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").withZoneUTC();

    //NOTE: dates on some of the API paths are formatted with the 'Z' at the end.
    // Until they are consistent, there is this...
    public static final DateTimeFormatter DATE_TIME_FORMATTER_ALT =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final DateTimeFormatter DATE_TIME_FORMATTER_ALT_2 =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static final DateTimeFormatter DATE_TIME_FORMATTER_ALT_3 =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZoneUTC();

    /**
     * A Joda DateTimeFormatter which parses strings using the pattern:
     * {@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'}
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER_ALT_4 =
            DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");

    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS =
            new ArrayList<DateTimeFormatter>() {
                {
                    add(DATE_TIME_FORMATTER);
                    add(DATE_TIME_FORMATTER_ALT);
                    add(DATE_TIME_FORMATTER_ALT_2);
                    add(DATE_TIME_FORMATTER_ALT_3);
                    add(DATE_TIME_FORMATTER_ALT_4);
                }
            };

    private JodaSupport() {
        // prevent instantiation
    }


    /**
     * Parses a string timestamp into a Joda {@code DateTime} object.
     * <p>
     * This method is able to parse multiple representations of a timestamp:
     * </p>
     * <ul>
     *   <li>yyyy-MM-dd'T'HH:mm:ss.SSSSSS</li>
     *   <li>yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'</li>
     *   <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     *   <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * </ul>
     *
     * @param dateTime a string representing a timestamp
     * @return the Joda {@code DateTime} for the timestamp
     * @throws RuntimeException if the string representing the timestamp cannot be parsed
     */
    public static DateTime parseDateTime(final String dateTime) {
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return formatter.parseDateTime(dateTime);
            } catch (Exception e) {
                // nothing we can do, try the next formatter in line, or error out below.
            }
        }
        throw new RuntimeException(
                "Unable to parse '" + dateTime + "' to a Joda DateTime object: Missing a DateTimeFormatter.");
    }

}
