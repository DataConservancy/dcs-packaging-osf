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

import org.dataconservancy.cos.osf.packaging.support.test.model.OwlAnnotationProcessorTest.testClassHierarchy.Child;
import org.dataconservancy.cos.osf.packaging.support.test.model.OwlAnnotationProcessorTest.testClassHierarchy.Container;
import org.dataconservancy.cos.osf.packaging.support.test.model.OwlAnnotationProcessorTest.testEnum.AnEnum;
import org.dataconservancy.cos.osf.packaging.support.test.model.OwlAnnotationProcessorTest.testEnum.SomeClass;
import org.dataconservancy.cos.rdf.annotations.AnonIndividual;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Insures proper behaviors of the OwlAnnotationProcessor
 */
public class OwlAnnotationProcessorTest {


    /**
     * Insures that the {@code getAnnotationsForInstance} method properly walks the class hierarchy and obtains all
     * of the {@code AnnotatedElementPair} instances that it is meant to.
     */
    @Test
    public void testClassHierarchy() throws Exception {

        Child child = new Child();
        Container container = new Container();
        container.setChildren(Arrays.asList(child));

        Map<AnnotatedElementPair, AnnotationAttributes> annotations = new HashMap<>();

        OwlAnnotationProcessor.getAnnotationsForInstance(container, annotations);

        // expecting a pair for:
        //  Container.children has OwlProperty
        //  Container.foo has OwlProperty
        //  Container.foo has AnonIndividual
        //  Child class has OwlIndividual
        //  Child.id has IndividualUri

        assertEquals(5, annotations.size());
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("children"), OwlProperty.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("foo"), OwlProperty.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("foo"), AnonIndividual.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Child.class.getDeclaredField("id"), IndividualUri.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Child.class, OwlIndividual.class)));

    }

    /**
     * Tests {@code getAnnotationsForInstance} can properly get the annotations off of an {@code Enum} without
     * triggering a loop that will blow the stack.
     *
     * @throws Exception
     */
    @Test
    public void testEnum() throws Exception {
        Map<AnnotatedElementPair, AnnotationAttributes> annotations = new HashMap<>();
        OwlAnnotationProcessor.getAnnotationsForInstance(AnEnum.BAR, annotations);
        assertEquals(1, annotations.size());
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(AnEnum.class.getDeclaredField("BAR"), OwlProperty.class)));
    }

    /**
     * Insures array types are detected properly
     * @throws Exception
     */
    @Test
    public void testIsArray() throws Exception {
        assertFalse(OwlAnnotationProcessor.isArray(Object.class));
        Object[] arr = {new Object()};
        assertTrue(OwlAnnotationProcessor.isArray(arr.getClass()));
        assertFalse(OwlAnnotationProcessor.isArray(AnEnum.FOO.getClass()));
        assertFalse(OwlAnnotationProcessor.isArray(Arrays.asList(arr).getClass()));
    }

    /**
     * Insures collection types are detected properly
     * @throws Exception
     */
    @Test
    public void testIsCollection() throws Exception {
        assertFalse(OwlAnnotationProcessor.isCollection(Object.class));
        Object[] arr = {new Object()};
        assertFalse(OwlAnnotationProcessor.isCollection(arr.getClass()));
        assertFalse(OwlAnnotationProcessor.isCollection(AnEnum.FOO.getClass()));
        assertTrue(OwlAnnotationProcessor.isCollection(Arrays.asList(arr).getClass()));
    }

    /**
     * Insures that classes in the java, javax, and sun packages are excluded from annotation processing.
     * @throws Exception
     */
    @Test
    public void testIgnored() throws Exception {
        assertTrue(OwlAnnotationProcessor.ignored(Object.class));
        assertTrue(OwlAnnotationProcessor.ignored(sun.misc.Unsafe.class));
        assertFalse(OwlAnnotationProcessor.ignored(SomeClass.class));
    }
}
