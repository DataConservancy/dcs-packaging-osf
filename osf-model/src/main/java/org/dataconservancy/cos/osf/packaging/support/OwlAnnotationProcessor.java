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
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.IdentityTransform;
import org.dataconservancy.cos.rdf.support.OwlClasses;
import org.dataconservancy.cos.rdf.support.ToStringTransform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides utility methods for processing OWL annotations.
 */
public class OwlAnnotationProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(OwlAnnotationProcessor.class);

    private static final ConcurrentHashMap<Class<? extends Function>, Function> FIELD_TRANSFORMERS =
            new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<? extends Function>, Function> CLASS_TRANSFORMERS =
            new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Class<? extends Function>, Function> URI_FIELD_TRANSFORMERS =
            new ConcurrentHashMap<>();


    private static final ConcurrentHashMap<Class<? extends Function>, Function> URI_CLASS_TRANSFORMERS =
            new ConcurrentHashMap<>();

    static {
        FIELD_TRANSFORMERS.put(OwlProperty.DEFAULT_FIELD_TRANSFORM_FUNCTION,
                newFunction(OwlProperty.DEFAULT_FIELD_TRANSFORM_FUNCTION));
        CLASS_TRANSFORMERS.put(OwlProperty.DEFAULT_CLASS_TRANSFORM_FUNCTION,
                newFunction(OwlProperty.DEFAULT_CLASS_TRANSFORM_FUNCTION));
        URI_FIELD_TRANSFORMERS.put(IndividualUri.DEFAULT_FIELD_TRANSFORM_FUNCTION,
                newFunction(IndividualUri.DEFAULT_FIELD_TRANSFORM_FUNCTION));
        URI_CLASS_TRANSFORMERS.put(IndividualUri.DEFAULT_CLASS_TRANSFORM_FUNCTION,
                newFunction(IndividualUri.DEFAULT_CLASS_TRANSFORM_FUNCTION));
    }

    private static Function newFunction(Class<? extends Function> functionClass) {
        try {
            return functionClass.newInstance();
        } catch (InstantiationException|IllegalAccessException e) {
            throw new RuntimeException(String.format(
                    "Unable to instantiate Function class %s: %s", functionClass.getName(), e.getMessage()), e);
        }
    }

    /**
     * Creates single instances of the transformers found on each annotated element, and caches them in static member
     * fields.
     *
     * @param annotations
     */
    private static void populateTransformers(Map<AnnotatedElementPair, AnnotationAttributes> annotations) {
        annotations.keySet().stream()
                .filter(pair -> pair.getAnnotationClass() == OwlProperty.class)
                .forEach(pair -> {
                    AnnotationAttributes attributes = annotations.get(pair);
                    Class<? extends Function> fieldTransform = attributes.getClass(OwlProperty.FIELD_TRANSFORM_ATTRIBUTE);
                    Class<? extends Function> classTransform = attributes.getClass(OwlProperty.CLASS_TRANSFORM_ATTRIBUTE);


                    if (fieldTransform != null) {
                        FIELD_TRANSFORMERS.putIfAbsent(fieldTransform, newFunction(fieldTransform));
                    }

                    if (classTransform != null) {
                        CLASS_TRANSFORMERS.putIfAbsent(classTransform, newFunction(classTransform));
                    }
                });

        annotations.keySet().stream()
                .filter(pair -> pair.getAnnotationClass() == IndividualUri.class)
                .forEach(pair -> {
                    AnnotationAttributes attributes = annotations.get(pair);
                    Class<? extends Function> uriFieldTransform = attributes.getClass(IndividualUri.FIELD_TRANSFORM_ATTRIBUTE);
                    Class<? extends Function> uriClassTransform = attributes.getClass(IndividualUri.CLASS_TRANSFORM_ATTRIBUTE);

                    if (uriFieldTransform != null) {
                        URI_FIELD_TRANSFORMERS.putIfAbsent(uriFieldTransform, newFunction(uriFieldTransform));
                    }

                    if (uriClassTransform != null) {
                        URI_CLASS_TRANSFORMERS.putIfAbsent(uriClassTransform, newFunction(uriClassTransform));
                    }
                });
    }

    /**
     * Processes the transformation of fields annotated with {@code OwlProperty}.
     * Selects a transformer for the supplied {@code field} and invokes it on the supplied {@code fieldValue}.  This
     * method selects a field {@code transformer} or a {@code classTransformer} using the following rubric:
     * <ul>
     *     <li>prefer a non-default, non-identity field transformer</li>
     *     <li>prefer a non-default, non-identity class transformer</li>
     *     <li>prefer a field transform over a default class transform</li>
     *     <li>prefer a class transform</li>
     * </ul>
     *
     * @param enclosingObject the object that declares the member {@code field}
     * @param field the field being transformed
     * @param fieldValue the value of the field
     * @param annotations the annotations found on the object graph, including those for the {@code field} and
     * {@code enclosingObject}
     * @return the transformed value for the field
     */
    public static Object transform(Object enclosingObject, Field field, Object fieldValue, Map<AnnotatedElementPair, AnnotationAttributes> annotations) {
        OwlProperty property = getOwlProperty(field, annotations);
        Function fieldTransformer = FIELD_TRANSFORMERS.get(property.transform());
        Function classTransformer = CLASS_TRANSFORMERS.get(property.classTransform());

        Object transformedValue;

        String logMsg = "    Transforming %s %s with value %s using %s to %s";

        if (property.transform() != IdentityTransform.class && property.transform() != OwlProperty.DEFAULT_FIELD_TRANSFORM_FUNCTION) {
            // prefer a non-default, non-identity field transformer
            transformedValue = fieldTransformer.apply(fieldValue);
            LOG.trace(String.format(logMsg, "field", field.getType(), fieldValue, fieldTransformer.getClass().getName(), transformedValue));

        } else {

            if (property.classTransform() != IdentityTransform.class && property.transform() != OwlProperty.DEFAULT_CLASS_TRANSFORM_FUNCTION) {
                // prefer a non-default, non-identity class transformer
                transformedValue = classTransformer.apply(enclosingObject);
                LOG.trace(String.format(logMsg, "class", field.getType(), fieldValue, classTransformer.getClass().getName(), transformedValue));

            } else if (property.classTransform() == OwlProperty.DEFAULT_CLASS_TRANSFORM_FUNCTION) {
                // prefer a field transform over a default class transform
                transformedValue = fieldTransformer.apply(fieldValue);

            } else {
                // otherwise, use the class transform
                transformedValue = classTransformer.apply(enclosingObject);
            }
        }

        return transformedValue;
    }

    /**
     * Processes the transformation of fields annotated with {@code IndividualUri}.
     * Selects a transformer for the supplied {@code field} and invokes it on the supplied {@code fieldValue}.  This
     * method selects a field {@code transformer} or a {@code classTransformer} using the following rubric:
     * <ul>
     *     <li>prefer a non-default, non-tostring field transformer</li>
     *     <li>prefer a non-default, non-tostring class transformer</li>
     *     <li>prefer a field transform over a default class transform</li>
     *     <li>prefer a class transform</li>
     * </ul>
     *
     * @param enclosingObject the object that declares the member {@code field}
     * @param annotatedField the field being transformed
     * @param annotations the annotations found on the object graph, including those for the {@code annotatedField} and
     * {@code enclosingObject}
     * @return the transformed value for the field
     * @throws IllegalArgumentException if the value of {@code annotatedField} is {@code null}
     */
    public static Object transformIndividualUri(Object enclosingObject, Field annotatedField, Map<AnnotatedElementPair, AnnotationAttributes> annotations) {
        IndividualUri annotation = getIndividualUri(annotatedField, annotations);
        Function fieldTransformer = URI_FIELD_TRANSFORMERS.get(annotation.transform());
        Function classTransformer = URI_CLASS_TRANSFORMERS.get(annotation.classTransform());

        ReflectionUtils.makeAccessible(annotatedField);
        Object fieldValue = ReflectionUtils.getField(annotatedField, enclosingObject);
        if (fieldValue == null) {
            throw new IllegalArgumentException(String.format("IndividualUri field %s on class %s is null", annotatedField.getName(), enclosingObject.getClass().getSimpleName()));
        }

        Object transformedValue;
        String logMsg = "    Transforming %s %s with value %s using %s to %s";

        // prefer a non-default, non-tostring field transformer
        if (annotation.transform() != ToStringTransform.class && annotation.transform() != IndividualUri.DEFAULT_FIELD_TRANSFORM_FUNCTION) {
            transformedValue = fieldTransformer.apply(fieldValue);
            LOG.trace(String.format(logMsg, "field", annotatedField.getType(), fieldValue, fieldTransformer.getClass().getName(), transformedValue));

        } else {

            if (annotation.classTransform() != ToStringTransform.class && annotation.classTransform() != IndividualUri.DEFAULT_CLASS_TRANSFORM_FUNCTION) {
                // prefer a non-default, non-tostring class transformer
                transformedValue = classTransformer.apply(enclosingObject);
                LOG.trace(String.format(logMsg, "class", annotatedField.getType(), fieldValue, classTransformer.getClass().getName(), transformedValue));

            } else if (annotation.classTransform() == IndividualUri.DEFAULT_CLASS_TRANSFORM_FUNCTION) {
                // prefer a field transform over a default class transform
                transformedValue = fieldTransformer.apply(fieldValue);

            } else {
                // otherwise, use the class transform
                transformedValue = classTransformer.apply(enclosingObject);
            }
        }

        return transformedValue;
    }

    /**
     * Obtain the annotations on the provided class, its fields, super classes, and super class fields.
     *
     * @param object the object to obtain annotations for
     * @param result the results, populated by this method
     */
    public static void getAnnotationsForInstance(Object object, Map<AnnotatedElementPair, AnnotationAttributes> result) {
        // If the class is in java.*, javax.*, sun.*, we ignore.  No need to process JDK classes.
        if (ignored(object.getClass())) {
            LOG.trace("Ignoring annotations on {}", object.getClass());
            return;
        }

        // Get class-level annotations
        LOG.trace("Processing class level annotations for {}", object.getClass());
        getAnnotations(object.getClass(), result);

        // Recurse through declared fields of the class and super-class, getting the annotations of each field.
        LOG.trace("Processing field level annotations for {}", object.getClass());
        ReflectionUtils.doWithFields(object.getClass(), f -> getAnnotations(f, result));

        // Recurse through declared fields, filtering for collections or arrays, and then getting annotations on the
        // type contained by the collection or array.  We do not process the fields of enums, because it produces an
        // endless loop.
        ReflectionUtils.doWithFields(object.getClass(), f -> {
            LOG.trace("Processing class {} field {} ({}) for annotations.", object.getClass(), f.getName(), f.getType());
            ReflectionUtils.makeAccessible(f);
            Object value = f.get(object);

            if (value != null) {
                Stream<?> unwrappedValues = unwrap(f, value);
                Optional<?> element = unwrappedValues.findFirst();
                if (element.isPresent()) {
                    getAnnotationsForInstance(element.get(), result);
                }
            }
        },
        f -> (isCollection(f.getType()) || isArray(f.getType())) && !object.getClass().isEnum());

        populateTransformers(result);
    }

    /**
     * Obtain the declared annotations on the provided annotated element.
     *
     * @param annotatedElement the annotated element
     * @param result the results, populated by this method
     */
    public static void getAnnotations(AnnotatedElement annotatedElement, Map<AnnotatedElementPair, AnnotationAttributes> result) {
        LOG.trace("  Processing {} for annotations", annotatedElement);
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
     * Obtains the {@code OwlProperty} from the supplied field, or throws an {@code IllegalArgumentException}.
     *
     * @param javaField the java field that must be annotated by {@code OwlProperty}
     * @param attributesMap a map of annotated elements to their annotation attributes
     * @return the {@code OwlProperty} instance
     * @throws IllegalArgumentException if the supplied field is not annotated by {@code OwlProperty}
     */
    public static OwlProperty getOwlProperty(Field javaField, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        AnnotatedElementPair aep = AnnotatedElementPair.forPair(javaField, OwlProperty.class);

        if (!attributesMap.containsKey(aep)) {
            throw new IllegalArgumentException(String.format("Annotation %s not found on field named %s, type %s.",
                    OwlProperty.class.getSimpleName(), javaField.getName(), javaField.getType()));
        }

        return AnnotationUtils.getAnnotation(javaField, OwlProperty.class);
    }

    /**
     * Obtains the {@code IndividualUri} from the supplied field, or throws an {@code IllegalArgumentException}.
     *
     * @param javaField the java field that must be annotated by {@code IndividualUri}
     * @param attributesMap a map of annotated elements to their annotation attributes
     * @return the {@code IndividualUri} instance
     * @throws IllegalArgumentException if the supplied field is not annotated by {@code IndividualUri}
     */
    public static IndividualUri getIndividualUri(Field javaField, Map<AnnotatedElementPair, AnnotationAttributes> attributesMap) {
        AnnotatedElementPair aep = AnnotatedElementPair.forPair(javaField, IndividualUri.class);

        if (!attributesMap.containsKey(aep)) {
            throw new IllegalArgumentException(String.format("Annotation %s not found on field named %s, type %s.",
                    IndividualUri.class.getSimpleName(), javaField.getName(), javaField.getType()));
        }

        return AnnotationUtils.getAnnotation(javaField, IndividualUri.class);
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
        return (R)transformIndividualUri(o, annotatedField, attributesMap);
    }

    /**
     * Determines if the supplied class represents a Collection.
     *
     * @param candidate the candidate class, which may be a Collection
     * @return true if the candidate class is a Collection, false otherwise
     */
    public static boolean isCollection(Class<?> candidate) {
        return Collection.class.isAssignableFrom(candidate);
    }

    /**
     * Determines if the supplied class represents an array type.
     *
     * @param candidate the candidate class, which may be an array type
     * @return true if the candidate class is an array type, false otherwise
     */
    public static boolean isArray(Class<?> candidate) {
        return candidate.isArray();
    }

    /**
     * Determines if the supplied {@code Field} represents a {@code Collection} or {@code Array}, and returns
     * a {@code Stream} of values contained therein.  If the supplied {@code Field} does not represent a
     * {@code Collection} or {@code Array}, a {@code Stream} containing the {@code fieldValue} is returned.
     *
     * @param field the field, which may represent a Collection or Array type
     * @param fieldValue the value for the field
     * @return a stream of objects contained in the Collection or Array, or a stream containing the supplied value
     */
    public static Stream<?> unwrap(Field field, Object fieldValue) {
        final Stream<?> objectsToProcess;
        if (OwlAnnotationProcessor.isCollection(field.getType())) {
            objectsToProcess = ((Collection) fieldValue).stream();
        } else if (OwlAnnotationProcessor.isArray(field.getType())) {
            objectsToProcess = Stream.of(((Object[]) fieldValue));
        } else {
            objectsToProcess = Stream.of(fieldValue);
        }
        return objectsToProcess;
    }

    /**
     * Determines if a class should be ignored for the purposes of all annotation processing.  If this method returns
     * true for a class, then no annotations should be processed on its members.  If the class is a member of the
     * packages {@code java.*}, {@code javax.*}, or {@code sun.*}, it is ignored.
     *
     * @param candidateToIgnore a class that may be ignored
     * @return true if the class is to be ignored for the purposes of annotation processing
     */
    static boolean ignored(Class<?> candidateToIgnore) {
        Stream<String> packagePrefixToIgnore = Stream.of("java", "javax", "sun");
        return packagePrefixToIgnore.anyMatch(prefix -> candidateToIgnore.getPackage().getName().startsWith(prefix));
    }
}
