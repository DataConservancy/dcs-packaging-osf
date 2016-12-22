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

import org.dataconservancy.cos.rdf.support.IndividualUriTransform;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiFunction;

/**
 * Annotates a field to be used as a resource identifier for an OWL individual.  Each Java class annotated as a
 * {@link OwlIndividual} is expected to have exactly one field annotated with {@code IndividualUri}.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.FIELD)
public @interface IndividualUri {

    /**
     * Class constant identifying the default transformation function.
     */
    static final Class<? extends BiFunction> DEFAULT_TRANSFORM_FUNCTION = IndividualUriTransform.class;

    /**
     * String constant identifying the annotation attribute {@link #transform()}.  Useful when using this
     * annotation with the Java Reflection API.
     */
    static final String TRANSFORM = "transform";

    /**
     * Specifies a {@link BiFunction} that may be used to transform the value of the field annotated by
     * {@code IndividualUri}.  By default the string value of the annotated field is returned, as if {@code toString()}
     * was called on the value.
     * <h3>{@code BiFunction} arguments</h3>
     * <p>
     * The arguments supplied to the {@code BiFunction} are the instance of the object that has the OWL individual as
     * a member, and the instance of the OWL individual that has this field as an annotated member.
     * </p>
     * <p>
     * Given the following domain model (getters and setters elided for brevity):
     * </p>
     * <pre>
     * &#x40;OwlIndividual
     * public class Book {
     *     &#x40;IndividualUri
     *     String bookTitle = "Moby Dick";
     *
     *     List&lt;;Chapter&gt; chapters;
     * }
     *
     * &#x40;OwlIndividual
     * public class Chapter {
     *     &#x40;IndividualUri
     *     Integer chapterNo = 1;
     *
     *     List&lt;Page&gt; pages;
     * }
     *
     * &#x40;OwlIndividual
     * public class Page {
     *     &#x40;IndividualUri
     *     Integer pageId = 1;
     * }
     * </pre>
     * <p>
     * The transform for {@code bookTitle} will receive as arguments {@code null} and an instance of {@code Book}.  The
     * transform for {@code chapterNo} will receive as arguments an instance of {@code Book} and an instance of
     * {@code Chapter}.  Finally, the transform for {@code pageNo} will receive an instance of {@code Chapter} and an
     * instance of {@code Page}.  In each case, the default transformation will return the {@code String} value of the
     * annotated field: "Moby Dick", "1", and "1" for the {@code Book}, {@code Chapter}, and {@code Page} respectively.
     * </p>
     * <h3>Custom transformations</h3>
     * <p>
     * Supplying a non-default transform may be used when the value of the field must be manipulated prior to being
     * used as a RDF resource identifier.  Transforms accommodate a wide range of use cases.
     * </p>
     * <p>
     * For example, assume that {@code id} in the following class contains the string form of a
     * URL:
     * </p>
     * <pre>
     * &#x40;OwlIndividual
     * public class MyDomainClass {
     *   &#x40;IndividualUri
     *   String id = "http://mydomain/object/1234";
     * }
     * </pre>
     * <p>
     * The resulting RDF model would create a resource such as:
     * </p>
     * <pre>
     * &lt;http://mydomain/object/1234&gt; a owl:Thing ;
     *   # other predicates elided for brevity
     *   .
     * </pre>
     * <p>
     * A custom transformation can be implemented such that URL is decoded, and the id {@code 1234} is used as the
     * RDF resource identifier:
     * </p>
     * <pre>
     * &lt;1234&gt; a owl:Thing ;
     *   # other predicates elided for brevity
     *   .
     * </pre>
     * <p>
     * Looking back at our previous domain model for a {@code Page}, suppose you wanted RDF resource identifiers for a
     * {@code Page} to be hash URIs on {@code Chapter} instances:
     * </p>
     * <pre>
     * &lt;1&gt;       a ex:Chapter .  # A Chapter individual with RDF resource ID "1"
     * &lt;1#page1&gt; a ex:Page .     # A Page individual with RDF resource ID "1#page1"
     * &lt;1#page2&gt; a ex:Page .     # ... with resource ID "1#page2"
     * </pre>
     * A custom transform can implement this as well.
     *
     * @return the {@code Class} of a {@code BiFunction} responsible for transforming the values to a single value used
     * as the identifier for the OWL individual
     */
    Class<? extends BiFunction> transform() default IndividualUriTransform.class;

}
