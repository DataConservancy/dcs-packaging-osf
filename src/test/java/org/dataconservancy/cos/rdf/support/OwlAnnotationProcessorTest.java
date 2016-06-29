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

import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testClassHierarchy.Child;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testClassHierarchy.Container;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testClassHierarchy.SomeOtherClass;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testEnum.AnEnum;

import org.dataconservancy.cos.rdf.annotations.AnonIndividual;
import org.dataconservancy.cos.rdf.annotations.IndividualUri;
import org.dataconservancy.cos.rdf.annotations.OwlIndividual;
import org.dataconservancy.cos.rdf.annotations.OwlProperty;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testEnum.SomeClass;

import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testNullFieldValueAndInteractionWithSeen.AContainer;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testNullFieldValueAndInteractionWithSeen.YetAnotherClass;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testRecursion.Recursive;
import org.dataconservancy.cos.rdf.support.test.model.OwlAnnotationProcessorTest.testRecursion.RecursiveContainer;
import org.junit.Test;
import org.springframework.core.annotation.AnnotationAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

        AnnotatedElementPairMap<AnnotatedElementPair, AnnotationAttributes> annotations = new AnnotatedElementPairMap<>();

        OwlAnnotationProcessor.getAnnotationsForInstance(container, annotations);

        // expecting a pair for:
        //  Container.id has IndividualUri
        //  Container.children has OwlProperty
        //  Container.foo has OwlProperty
        //  Container.foo has AnonIndividual
        //  Child class has OwlIndividual
        //  Child.id has IndividualUri
        //  Child.foo has OwlProperty
        //  SomeClass has OwlIndividual
        //  SomeClass.id has IndividualUri

        assertEquals(9, annotations.size());
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("containerId"), IndividualUri.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("children"), OwlProperty.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("foo"), OwlProperty.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Container.class.getDeclaredField("foo"), AnonIndividual.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Child.class.getDeclaredField("id"), IndividualUri.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Child.class.getDeclaredField("foo"), OwlProperty.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Child.class, OwlIndividual.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(SomeOtherClass.class, OwlIndividual.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(SomeOtherClass.class.getDeclaredField("id"), IndividualUri.class)));

    }

    /**
     * Tests {@code getAnnotationsForInstance} can properly get the annotations off of an {@code Enum} without
     * triggering a loop that will blow the stack.
     *
     * @throws Exception
     */
    @Test
    public void testEnum() throws Exception {
        AnnotatedElementPairMap<AnnotatedElementPair, AnnotationAttributes> annotations = new AnnotatedElementPairMap<>();
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

    /**
     * Insures that a recursive loop will not occur when processing annotations
     * @throws Exception
     */
    @Test
    public void testRecursion() throws Exception {
        Recursive recursive = new Recursive();
        AnnotatedElementPairMap<AnnotatedElementPair, AnnotationAttributes> annotations = new AnnotatedElementPairMap<>();
        OwlAnnotationProcessor.getAnnotationsForInstance(recursive, annotations);

        // Expecting:
        //  OwlProperty annotation on RecursiveContainer recursiveField field
        //  OwlIndividual annotation on Recursive class

        assertEquals(2, annotations.size());
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(Recursive.class, OwlIndividual.class)));
        assertTrue(annotations.keySet().contains(
                AnnotatedElementPair.forPair(RecursiveContainer.class.getDeclaredField("recursiveField"), OwlProperty.class)));
    }



    /**
     * A side-affect of recursion detection was that fields with null values were marked as processed.  Subsequent
     * processing of those fields never occurred because the initial processing of the null mistakenly marked it as
     * 'seen'.  This test triggers the bug and verifies the fix.
     *
     * @throws Exception
     */
    @Test
    public void testNullFieldValueAndInteractionWithSeen() throws Exception {
        // Initial state
        AnnotatedElementPairMap<AnnotatedElementPair, AnnotationAttributes> attributesMap = new AnnotatedElementPairMap<>();
        AContainer container = new AContainer();
        assertNull(container.a_field);
        assertTrue(attributesMap.isEmpty());

        // Obtain annotations.  The container.a_field is null, so no annotations are expected to be found.
        OwlAnnotationProcessor.getAnnotationsForInstance(container, attributesMap);

        // Verify expectations
        assertFalse(attributesMap.containsKey(AnnotatedElementPair.forPair(YetAnotherClass.class, OwlIndividual.class)));
        assertFalse(attributesMap.containsKey(AnnotatedElementPair.forPair(YetAnotherClass.class.getDeclaredField("id"), IndividualUri.class)));
        assertTrue(attributesMap.isEmpty());

        container = new AContainer();              // a new instance of the container
        container.a_field = new YetAnotherClass(); // this time with a not-null field

        // Obtain annotations.  The container.a_field is not null, so we expect that the class YetAnotherClass will
        // be processed for annotations.
        OwlAnnotationProcessor.getAnnotationsForInstance(container, attributesMap);

        // Verify expectations (fails without the fix)
        assertTrue(attributesMap.containsKey(AnnotatedElementPair.forPair(YetAnotherClass.class, OwlIndividual.class)));
        assertTrue(attributesMap.containsKey(AnnotatedElementPair.forPair(YetAnotherClass.class.getDeclaredField("id"), IndividualUri.class)));
        assertEquals(2, attributesMap.size());
    }
}