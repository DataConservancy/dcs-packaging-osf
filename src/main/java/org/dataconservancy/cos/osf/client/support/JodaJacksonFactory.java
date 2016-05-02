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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;

/**
 * Created by esm on 4/25/16.
 */
public class JodaJacksonFactory {

    @JsonCreator
    public static DateTime createDateCreated(@JsonProperty("date_created") String dateCreated) {
        return DateTime.parse(dateCreated);
    }

    @JsonCreator
    public static DateTime createDateModified(@JsonProperty("date_modified") String dateCreated) {
        return DateTime.parse(dateCreated);
    }

}
