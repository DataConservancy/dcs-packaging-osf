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
package org.dataconservancy.cos.osf.packaging.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * Created by esm on 6/5/16.
 */
public class AnnotatedElementPair {

    private AnnotatedElement annotatedElement;

    private Class<? extends Annotation> annotationClass;

    public AnnotatedElementPair(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationClass) {
        this.annotatedElement = annotatedElement;
        this.annotationClass = annotationClass;
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
