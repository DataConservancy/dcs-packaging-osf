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

import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by esm on 6/5/16.
 */
public class OwlAnnotationProcessor {

    /**
     * Obtain the annotations on the provided class, its fields, super classes, and super class fields.
     *
     * @param clazz the class to obtain annotations for
     * @param result the results, populated by this method
     */
    public static void getAnnotationsForClass(Class clazz, Map<AnnotatedElementPair, AnnotationAttributes> result) {
        // Get class-level annotations
        getAnnotations(clazz, result);

        // Recurse through declared fields of the class, getting the annotations of each field.
        Stream.of(clazz.getDeclaredFields()).forEach(f -> getAnnotations(f, result));

        // Recurse the class hierarchy, stopping at java.lang.Object
        // TODO annotations on interfaces?
        Class superclass = clazz.getSuperclass();
        if (superclass == Object.class) {
            return;
        }

        getAnnotationsForClass(superclass, result);
    }

    /**
     * Obtain the declared annotations on the provided annotated element.
     *
     * @param annotatedElement the annotated element
     * @param result the resutls, populated by this method
     */
    public static void getAnnotations(AnnotatedElement annotatedElement, Map<AnnotatedElementPair, AnnotationAttributes> result) {
        Annotation[] annotations = annotatedElement.getDeclaredAnnotations();
        Stream.of(annotations).forEach(annotation -> {
                    AnnotatedElementPair aep = new AnnotatedElementPair(annotatedElement, annotation.annotationType());
                    result.put(aep, AnnotationUtils.getAnnotationAttributes(annotatedElement, annotation));
                }
        );
    }

    /**
     * Obtain the OWL class that the supplied Java object maps to.
     *
     * @param javaObject the java object that maps to an OWL class
     * @param attributesMap the annotation attributes found on the class
     * @return the OWL class that the supplied Java object maps to
     * @throws IllegalArgumentException if the class of the Java object is not annotated by {@code OwlIndividual}, or if
     * the value of the {@code OwlIndividual} annotation is {@code null}
     */
    public static OwlClasses getOwlClass(Object javaObject, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        return getOwlClass(javaObject.getClass(), attributesMap);
    }

    /**
     * Obtain the OWL class that the supplied Java class maps to.
     *
     * @param javaClass the java class that maps to an OWL class
     * @param attributesMap the annotation attributes found on the class
     * @return the OWL class that the supplied Java class maps to
     * @throws IllegalArgumentException if the Java class is not annotated by {@code OwlIndividual}, or if
     * the value of the {@code OwlIndividual} annotation is {@code null}
     */
    public static OwlClasses getOwlClass(Class<?> javaClass, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        Class<OwlIndividual> annotationClass = OwlIndividual.class;
        String valueAttr = "value";

        AnnotatedElementPair aep = AnnotatedElementPair.forPair(javaClass, annotationClass);
        if (!attributesMap.containsKey(aep)) {
            throw new IllegalArgumentException(String.format("Could not find annotation %s on %s", annotationClass.getSimpleName(), javaClass.getSimpleName()));
        }

        AnnotationAttributes attrs = attributesMap.get(aep);
        OwlClasses owlClass = attrs.getEnum(valueAttr);
        if (owlClass == null) {
            throw new IllegalArgumentException(String.format("Annotation %s missing required attribute %s on class %s", annotationClass.getSimpleName(), valueAttr, javaClass.getSimpleName()));
        }

        return owlClass;
    }

    /**
     * Obtains the value of the field annotated with {@code IndividualUri} on the supplied object.  Performs any
     * transformation on the value before returning the transformed value.
     *
     * @param o
     * @param attributesMap
     * @param <T> the type of the field annotated with {@code IndividualUri}
     * @param <R> the type of the field annotated with {@code IndividualUri} after transformation
     * @return the transformed value of the {@code IndividualUri} field
     */
    public static <T, R> R getIndividualId(Object o, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        Class<OwlIndividual> owlIndividualClass = OwlIndividual.class;
        Class<IndividualUri> individualUriClass = IndividualUri.class;
        String transformAttr = "transform";
        List<Field> annotatedFields = new ArrayList<>();

        ReflectionUtils.doWithFields(o.getClass(),
                annotatedFields::add,
                field -> field.getDeclaredAnnotation(individualUriClass) != null);

        if (annotatedFields.size() == 0) {
            // The enclosing class may not be an OwlIndividual, therefore it wouldn't have an IndividualUri

            if (!attributesMap.containsKey(AnnotatedElementPair.forPair(o.getClass(), owlIndividualClass))) {
                throw new IllegalArgumentException(String.format("Annotation %s not found on %s.  Is %s an OwlIndividual?", individualUriClass.getSimpleName(), o.getClass().getSimpleName(), o.getClass().getSimpleName()));
            }

            throw new IllegalArgumentException(String.format("Missing required annotation %s on %s, an OwlIndividual.", individualUriClass.getSimpleName(), o.getClass().getSimpleName()));
        }

        if (annotatedFields.size() > 1) {
            throw new IllegalArgumentException(String.format("Found %s fields (%s) on %s annotated with %s.  Only one field may be annotated with %s", annotatedFields.size(), annotatedFields.stream().map(Field::getName).collect(Collectors.joining(", ")), o.getClass().getSimpleName(), individualUriClass.getSimpleName(), individualUriClass.getSimpleName()));
        }

        Field annotatedField = annotatedFields.get(0);
        AnnotatedElementPair aep = AnnotatedElementPair.forPair(annotatedField, individualUriClass);
        Class<Function<T, R>> transformClass = (Class<Function<T, R>>) attributesMap.get(aep).getClass(transformAttr);

        Function<T, R> transformFn = null;
        try {
            transformFn = transformClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        T individualUriValue = null;
        try {
            annotatedField.setAccessible(true);
            individualUriValue = (T) annotatedField.get(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        if (individualUriValue == null) {
            throw new IllegalArgumentException(String.format("IndividualUri field %s on class %s is null", annotatedFields.get(0).getName(), o.getClass().getSimpleName()));
        }

        return transformFn.apply(individualUriValue);
    }
}
