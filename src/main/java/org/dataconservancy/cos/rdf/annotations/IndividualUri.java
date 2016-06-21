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
 * Annotates a field to be used as a resource identifier for an OWL individual.  Each Java class annotated as a
 * {@link OwlIndividual} is expected to have exactly one field annotated with {@code @IndividualUri}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface IndividualUri {

    /**
     * Class constant identifying the default transformation function.
     */
    static final Class<? extends Function> DEFAULT_TRANSFORM_FUNCTION = ToStringTransform.class;

    /**
     * String constant identifying the annotation attribute {@link #transform()}.  Useful when using this
     * annotation with the Java Reflection API.
     */
    static final String TRANSFORM = "transform";

    /**
     * String constant identifying the annotation attribute {@link #mode()}. Useful when using this
     * annotation with the Java Reflection API.
     */
    static final String TRANSFORM_MODE = "mode";

    /**
     * A function that transforms the value of the annotated field, provided the value of the field.
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
     * @return the {@code Class} of a {@code Function} responsible for transforming the value of the field, provided the
     * value of the field, to a {@code String}
     */
    Class<? extends Function<Object, String>> transform() default ToStringTransform.class;

    /**
     * The transformation mode.
     * <p>
     * A mode of {@link TransformMode#FIELD} means that the value of the annotated field will be supplied to the
     * transform function.  A mode of {@link TransformMode#CLASS} means that the instance of the class enclosing the
     * annotated field will be supplied to the transform function.
     * </p>
     *
     * @return the mode of the transformation function; whether it is accepting an instance of the annotated field or
     * an instance of the class enclosing the annotated field
     */
    TransformMode mode() default TransformMode.FIELD;

}
