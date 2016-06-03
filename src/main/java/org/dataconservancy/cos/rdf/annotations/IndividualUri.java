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

import org.dataconservancy.cos.rdf.support.ToStringTransform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Annotates a field to be used as a URI for an OWL individual.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface IndividualUri {

    /**
     * A function that transforms the value of the annotated field.
     * <p>
     * This is useful when the value of annotated field needs to be manipulated prior to being converted to RDF.  For
     * example, assume that {@code id} in the following class contains the string form of a URL:
     * </p>
     * <pre>
     * &#x40;OwlIndividual
     * public class MyDomainClass {
     *   &#x40;IndividualUri
     *   String id;
     * }
     * </pre>
     * <p>
     * The resulting RDF model would create a resource such as:
     * </p>
     * <pre>
     * &lt;http://mydomain/object/1234&gt; a owl:Thing ;
     *   # ...
     *   # other predicates
     *   # ...
     *   .
     * </pre>
     * <p>
     * What if you wanted the RDF to relativize the resource identifier like so:
     * </p>
     * <pre>
     * &lt;1234&gt; a owl:Thing ;
     *   # ...
     *   # other predicates
     *   # ...
     *   .
     * </pre>
     * You would annotate the {@code @IndividualUri} with this {@code transform} attribute, specifying a {@code Function}
     * that accepts the value of the annotated field (in this example {@code http://mydomain/object/1234}) and
     * transforms it to the desired value (e.g {@code 1234}).
     *
     * @return the {@code Class} of a {@code Function} responsible for transforming the value of the field to a
     * {@code String}
     */
    Class<? extends Function<Object, String>> transform() default ToStringTransform.class;
}
