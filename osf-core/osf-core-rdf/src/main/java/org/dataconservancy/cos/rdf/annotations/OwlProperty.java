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

import org.dataconservancy.cos.rdf.support.OwlProperties;
import org.dataconservancy.cos.rdf.support.IdentityTransform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * Annotates a field which will be mapped to an OWL property.  The subject of the property will be the enclosing
 * {@code @OwlIndividual}, and the object of the property will be the value of the annotated field (subject to any
 * transformation, described below).
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface OwlProperty {

    /**
     * Class constant identifying the default transformation function
     */
    static final Class<? extends Function> DEFAULT_TRANSFORM_FUNCTION = IdentityTransform.class;

    /**
     * String constant identifying the annotation attribute {@link #transform()}.  Useful when using this
     * annotation with the Java Reflection API.
     */
    static final String TRANSFORM = "transform";

    /**
     * String constant identifying the annotation attribute {@link #mode()}.  Useful when using this
     * annotation with the Java Reflection API.
     */
    static final String TRANSFORM_MODE = "mode";

    /**
     * The URI of the OWL property associated with the annotated field.  This field will be serialized as the object of
     * the supplied property, and its subject will be that of the containing {@code @OwlIndividual}.
     *
     * @return the OWL property associated with this field
     */
    OwlProperties value();

    /**
     * A function that transforms the value of the annotated field, provided the value of the field.
     * <p>
     * This is useful when the value of annotated field needs to be manipulated prior to being converted to RDF.  For
     * example, assume that {@code parent} in the following class contains the string form of a URL:
     * </p>
     * <pre>
     * &#x40;OwlIndividual
     * public class MyDomainClass {
     *   &#x40;OwlProperty(OwlProperties.OSF_HAS_PARENT)
     *   String parent;
     * }
     * </pre>
     * <p>
     * The resulting RDF model would create a resource such as:
     * </p>
     * <pre>
     * &lt;fghij&gt; a owl:Thing ;
     *   :hasParent &lt;http://mydomain/object/abcde&gt; ;
     *   # ...
     *   # other predicates
     *   # ...
     *   .
     * </pre>
     * <p>
     * What if you wanted the RDF to relativize the URL like so:
     * </p>
     * <pre>
     * &lt;fghij&gt; a owl:Thing ;
     *   :hasParent &lt;abcde&gt; ;
     *   # ...
     *   # other predicates
     *   # ...
     *   .
     * </pre>
     * You would annotate the {@code @OwlProperty} with this {@code transform} attribute, specifying a {@code Function}
     * that accepts the value of the annotated field (in this example {@code http://mydomain/object/abcde}) and
     * transforms it to the desired value (e.g {@code abcde}).
     *
     * @return the {@code Class} of a {@code Function} responsible for transforming the value of the field, provided the
     * value of the field
     */
    Class<? extends Function> transform() default IdentityTransform.class;

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
