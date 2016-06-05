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
package org.dataconservancy.cos.rdf.annotations;

import org.dataconservancy.cos.rdf.support.OwlClasses;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a field which will be mapped to an anonymous OWL individual.   The subject of the individual will be the
 * enclosing {@code @OwlIndividual}, and the object of the property will be the value of the annotated field.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface AnonIndividual {

    /**
     * The Owl class associated with the annotated Java class.  Instances of the annotated Java class will be serialized
     * as an OWL individual that is an instance of the supplied {@code OwlClasses}.  Fields annotated with
     * {@code AnonIndividual} are also expected to be annotated with {@code OwlProperty}.
     *
     * @return the OWL class associated with this Java class
     */
    OwlClasses value();

}
