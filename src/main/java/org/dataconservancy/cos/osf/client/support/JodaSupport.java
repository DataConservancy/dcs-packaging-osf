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

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by esm on 5/2/16.
 * @author esm
 * @author khanson
 * 
 */
public class JodaSupport {

    /**
     * A Joda DateTimeFormatter which parses timezone-less strings as UTC using the pattern: {@code yyyy-MM-dd'T'HH:mm:ss.SSSSSS}
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").withZoneUTC();

    //NOTE: dates on some of the API paths are formatted with the 'Z' at the end. Until they are consistent, there is this...
    public static final DateTimeFormatter DATE_TIME_FORMATTER_ALT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

}
