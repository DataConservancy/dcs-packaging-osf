/*
 * Copyright 2017 Johns Hopkins University
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
package org.dataconservancy.cos.osf.client.retrofit;


import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.ResourceList;
import com.squareup.okhttp.OkHttpClient;
import org.dataconservancy.cos.osf.client.retrofit.PaginationTestUtils.Meta;
import org.dataconservancy.cos.osf.client.retrofit.PaginationTestUtils.TestResource;
import org.junit.Test;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;

import static org.dataconservancy.cos.osf.client.retrofit.PaginationTestUtils.ofIds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginatedListAdapterTest {

    private OkHttpClient okHttp = mock(OkHttpClient.class);

    private ResourceConverter converter = mock(ResourceConverter.class);

    private final ResourceList<?> resources = mock(ResourceList.class);

    private final Class<?> clazz = Object.class;

    final PaginatedListAdapter<?> underTest = new PaginatedListAdapter(okHttp, converter, clazz, resources);

    @Test
    public void testTotalAndPerPage() throws Exception {
        final Integer total = 1;
        final Integer perPage = 10;
        when(resources.getMeta()).thenReturn(new Meta<>(total, perPage));

        assertEquals(1, underTest.total());
        assertEquals(10, underTest.perPage());
        verify(resources, atLeastOnce()).getMeta();
    }

    @Test
    public void testNoTotalWithNextPage() throws Exception {
        when(resources.getMeta()).thenReturn(null);
        when(resources.getNext()).thenReturn("");

        // The collection can be paginated still (e.g. stream() will still work) but the collection is of unknown size
        assertEquals(-1, underTest.total());
        assertEquals(-1, underTest.perPage());

        verify(resources, atLeastOnce()).getMeta();
        verify(resources, atLeastOnce()).getNext();
    }

    @Test
    public void testNoTotalWithNoNextPage() throws Exception {
        final int size = 30;
        when(resources.getMeta()).thenReturn(null);
        when(resources.getNext()).thenReturn(null);
        when(resources.size()).thenReturn(size);

        // The collection cannot be paginated, there is no next page of results, so the size is equal to the size of the
        // underlying resource
        assertEquals(size, underTest.total());
        assertEquals(-1, underTest.perPage());

        verify(resources, atLeastOnce()).getMeta();
        verify(resources, atLeastOnce()).getNext();
        verify(resources, atLeastOnce()).size();
    }

    @Test
    public void testSpliteratorKnownSize() throws Exception {
        when(resources.getMeta()).thenReturn(new Meta<>(1, 10));

        final Spliterator result = underTest.spliterator();

        assertEquals(Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.SUBSIZED | Spliterator.SIZED,
                result.characteristics());
        assertEquals(1, result.getExactSizeIfKnown());
        verify(resources, atLeastOnce()).getMeta();
    }

    @Test
    public void testSpliteratorUnknownSize() throws Exception {
        when(resources.getMeta()).thenReturn(null);
        when(resources.getNext()).thenReturn("");

        final Spliterator result = underTest.spliterator();

        assertEquals(Spliterator.ORDERED | Spliterator.NONNULL, result.characteristics());
        verify(resources, atLeastOnce()).getMeta();
    }

    @Test
    public void testStreamSpliteratorKnownSize() throws Exception {
        final List testResources = ofIds("1", "2");
        when(resources.getMeta()).thenReturn(new Meta<>(1, 10));
        when(resources.iterator()).thenReturn(testResources.iterator());

        // verify the initial state of the spliterator is sized
        assertEquals(Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.SUBSIZED | Spliterator.SIZED,
                underTest.spliterator().characteristics());

        final List results = underTest.stream().collect(Collectors.toList());

        assertEquals(testResources.size(), results.size());
        assertTrue(results.containsAll(testResources));
        // once for verifying the initial state of the spliterator in the test, once for the execution in
        // PaginatedIterator
        verify(resources, times(2)).iterator();
        verify(resources, atLeastOnce()).getMeta();
    }

    @Test
    public void testStreamSpliteratorUnknownSize() throws Exception {
        final List testResources = ofIds("1", "2");
        when(resources.getMeta()).thenReturn(null);
        when(resources.getNext()).thenReturn("");
        when(resources.iterator()).thenReturn(testResources.iterator());

        // verify the initial state of the spliterator is unsized
        assertEquals(Spliterator.ORDERED | Spliterator.NONNULL, underTest.spliterator().characteristics());

        final List results = underTest.stream().collect(Collectors.toList());

        assertEquals(testResources.size(), results.size());
        assertTrue(results.containsAll(testResources));
        // once for verifying the initial state of the spliterator in the test, once for the execution in
        // PaginatedIterator
        verify(resources, times(2)).iterator();
        verify(resources, atLeastOnce()).getMeta();
    }

    @Test
    public void testIsEmpty() throws Exception {
        when(resources.isEmpty()).thenReturn(true);
        assertTrue(underTest.isEmpty());
        verify(resources).isEmpty();

        reset(resources);

        when(resources.isEmpty()).thenReturn(false);
        assertFalse(underTest.isEmpty());
        verify(resources).isEmpty();
    }

    @Test
    public void testToArray() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        final Object[] arrayResources = underTest.toArray();

        assertEquals(arrayResources.length, testResources.size());
        assertEquals(arrayResources[0], testResources.get(0));
        assertEquals(arrayResources[1], testResources.get(1));
        verifyForStream();
    }

    @Test
    public void testToTypedArray() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        final TestResource[] arrayResources = underTest.toArray(new TestResource[]{});

        assertEquals(arrayResources.length, testResources.size());
        assertEquals(arrayResources[0], testResources.get(0));
        assertEquals(arrayResources[1], testResources.get(1));
        verifyForStream();
    }

    @Test
    public void testToOversizedTypedArray() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        final TestResource[] arrayResources = underTest.toArray(new TestResource[testResources.size() + 1]);

        assertEquals(arrayResources.length, testResources.size() + 1);
        assertEquals(arrayResources[0], testResources.get(0));
        assertEquals(arrayResources[1], testResources.get(1));
        assertEquals(null, arrayResources[2]);
        verifyForStream();
    }

    @Test
    public void testToUndersizedTypedArray() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        final TestResource[] arrayResources = underTest.toArray(new TestResource[testResources.size() - 1]);

        assertEquals(arrayResources.length, testResources.size());
        assertEquals(arrayResources[0], testResources.get(0));
        assertEquals(arrayResources[1], testResources.get(1));
        verifyForStream();
    }

    @Test
    public void testParallelStreamSupport() throws Exception {
        final List testResources = ofIds("1", "2");

        prepareForStream(testResources);
        assertFalse(underTest.stream().isParallel());
        verifyForStream();

        reset(resources);

        prepareForStream(testResources);
        // parallel streams are not supported
        assertFalse(underTest.parallelStream().isParallel());
        verifyForStream();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNegativeIndex() throws Exception {
        underTest.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIndexExceedsSize() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);
        underTest.get(testResources.size() + 1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetIndexExceedsSizeUnknownStreamLength() throws Exception {
        final List testResources = ofIds("1", "2");
        when(resources.getMeta()).thenReturn(null);
        when(resources.getNext()).thenReturn("");
        when(resources.iterator()).thenReturn(testResources.iterator());

        underTest.get(testResources.size() + 1);
    }

    @Test
    public void testGetIndex() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        assertEquals(testResources.get(1), underTest.get(1));

        verifyForStream();
    }

    @Test
    public void testContains() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        assertTrue(underTest.contains(testResources.get(0)));

        verifyForStream();
        reset(resources);
        prepareForStream(testResources);

        assertFalse(underTest.contains(new TestResource("3")));

        verifyForStream();
    }

    @Test
    public void testIndexOf() throws Exception {
        final List testResources = ofIds("1", "2");
        prepareForStream(testResources);

        assertEquals(0, underTest.indexOf(testResources.get(0)));

        verifyForStream();
        reset(resources);
        prepareForStream(testResources);

        assertEquals(-1, underTest.indexOf(new TestResource("3")));

        verifyForStream();
    }

    @Test
    public void testLastIndexOf() throws Exception {
        final List testResources = ofIds("1", "2", "2");
        prepareForStream(testResources);

        assertEquals(2, underTest.lastIndexOf(testResources.get(2)));

        verifyForStream();
        reset(resources);
        prepareForStream(testResources);

        assertEquals(-1, underTest.indexOf(new TestResource("3")));

        verifyForStream();
    }

    @Test
    public void testSubList() throws Exception {
        final List testResources = ofIds("1", "2", "3");
        prepareForStream(testResources);

        assertEquals(testResources.subList(0, 1), underTest.subList(0, 1));

        verifyForStream();
        reset(resources);
        prepareForStream(testResources);

        assertEquals(testResources.subList(1, 2), underTest.subList(1, 2));

        verifyForStream();
        reset(resources);
        prepareForStream(testResources);

        assertEquals(testResources.subList(0, 2), underTest.subList(0, 2));

        verifyForStream();
        reset(resources);
        prepareForStream(testResources);

        assertEquals(testResources.subList(2, 2), underTest.subList(2, 2));
    }

    /**
     * Prepares the mocks such that PaginatedListAdapter.stream will return a stream over the supplied list.
     *
     * @param toStream
     */
    @SuppressWarnings("unchecked")
    void prepareForStream(final List toStream) {
        when(resources.getMeta()).thenReturn(new Meta<>(toStream.size(), toStream.size()));
        when(resources.iterator()).thenReturn(toStream.iterator());
    }

    /**
     * Verify mocks that should have been acted on in order to return a stream.
     */
    void verifyForStream() {
        verify(resources, atLeastOnce()).getMeta();
        verify(resources).iterator();
    }

}