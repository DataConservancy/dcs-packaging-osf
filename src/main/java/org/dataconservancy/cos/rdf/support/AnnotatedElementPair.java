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
package org.dataconservancy.cos.rdf.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Links an annotated element with an annotation on that element.  Suitable for use as a key in a {@code Map}.
 * <h4>Examples</h4>
 * This example contains a single {@code AnnotatedElementPair} composed of {@code Class MyClass} and the annotation
 * {@code Class OwlIndividual}:
 * <pre>
 * &#x40;OwlIndividual(OwlClasses.MY_CLASS)
 * public class MyClass {
 *     // ...
 * }
 * </pre>
 *
 * After adding a field annotated with {@code IndividualId}, another {@code AnnotatedElementPair} would exist,
 * composed of {@code Field id} and the annotation {@code Class IndividualId}:
 * <pre>
 * &#x40;OwlIndividual(OwlClasses.MY_CLASS)
 * public class MyClass {
 *     &#x40;IndividualId
 *     private String id;
 *     // ...
 * }
 * </pre>
 *
 * Adding a second field annotated with {@code OwlProperties} and {@code AnonIndividual} would create two
 * more {@code AnnotatedElementPair}s composed by: {@code Field foo} and the annotation {@code Class OwlProperties},
 * and {@code Field foo} and the annotation {@code Class AnonIndividual}:
 * <pre>
 * &#x40;OwlIndividual(OwlClasses.MY_CLASS)
 * public class MyClass {
 *     &#x40;IndividualId
 *     private String id;
 *
 *     &#x40;OwlProperties(OwlProperties.MY_PROPERTY)
 *     &#x40;AnonIndividual
 *     private AnotherClass foo;
 *     // ...
 * }
 * </pre>
 */
public class AnnotatedElementPair {

    private AnnotatedElement annotatedElement;

    private Class<? extends Annotation> annotationClass;

    public AnnotatedElementPair(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationClass) {
        this.annotatedElement = annotatedElement;
        this.annotationClass = annotationClass;
    }

    /**
     * Obtain the {@code AnnotatedElement} referenced by this pair.
     *
     * @return
     */
    public AnnotatedElement getAnnotatedElement() {
        return annotatedElement;
    }

    /**
     * Obtain the {@code AnnotationClass} referenced by this pair.
     * @return
     */
    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public static AnnotatedElementPair forPair(AnnotatedElement e, Class<? extends Annotation> annotationClass) {
        return new AnnotatedElementPair(e, annotationClass);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AnnotatedElementPair that = (AnnotatedElementPair) o;

        if (!annotatedElement.equals(that.annotatedElement)) return false;
        return annotationClass.equals(that.annotationClass);

    }

    @Override
    public int hashCode() {
        int result = annotatedElement.hashCode();
        result = 31 * result + annotationClass.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AnnotatedElementPair{" +
                "annotatedElement=" + annotatedElement +
                ", annotationClass=" + annotationClass +
                '}';
    }
}
