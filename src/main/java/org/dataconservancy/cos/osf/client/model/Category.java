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

package org.dataconservancy.cos.osf.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/** 
 * List of categories that can apply to an OSF node
 * @author khanson
 */
public enum Category {
	PROJECT("project"),
	HYPOTHESIS("hypothesis"),
	METHODS_AND_MEASURES("methods and measures"),
	PROCEDURE("procedure"),
	INSTRUMENTATION("instrumentation"),
	DATA("data"),
	ANALYSIS("analysis"),
	COMMUNICATION("communication"),
	SOFTWARE("software"),
	OTHER("other");    

    private final String value;
    private Category(String value) { this.value = value; }
    
    @JsonValue 
    public String value() { return value; }

    @JsonCreator
	public static Category forValue(String value) {
        if (value != null) {
            for (Category category : Category.values()) {
                if (value.equals(category.value())) {
                    return category;
                }
            }
        }
        return null;
    }
    
    
}
