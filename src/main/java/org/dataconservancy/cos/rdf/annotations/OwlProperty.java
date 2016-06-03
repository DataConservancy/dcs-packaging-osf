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
import org.dataconservancy.cos.rdf.support.ToStringTransform;

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
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface OwlProperty {

    /**
     * The Owl property associated with the annotated field.  This field will be serialized as the object of the
     * supplied property, and its subject will be that of the containing {@code @OwlIndividal}.
     *
     * @return the OWL property associated with this field
     */
    OwlProperties value();

    /**
     * A function that transforms the value of the annotated field.
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
     * @return the {@code Class} of a {@code Function} responsible for transforming the value of the field to a
     * {@code String}
     */
    Class<? extends Function> transform() default IdentityTransform.class;
}
