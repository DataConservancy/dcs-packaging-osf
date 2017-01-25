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
package org.dataconservancy.cos.osf.client.service;

import com.github.jasminb.jsonapi.ResourceConverter;
import com.github.jasminb.jsonapi.ResourceList;
import com.squareup.okhttp.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Adapts a {@link ResourceList} to the {@code PaginatedList} interface.  In addition to providing the implementation
 * for the {@code PaginatedList} methods, this implementation also enables Java 8 streams, allowing a client
 * to stream elements from all result pages, in encounter order, without having to explicitly request additional pages
 * from the API.
 *
 * Implementation notes:
 * <ul>
 *     <li>Sequential access only, parallel access is not supported</li>
 *     <li>Instances are read only; methods that attempt to modify the list throw
 *         {@code UnsupportedOperationException}</li>
 *     <li>List methods that would be complex or difficult to implement efficiently when limited to
 *         sequential access of the OSF API also throw {@code UnsupportedOperationException}</li>
 * </ul>
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginatedListAdapter<E> implements PaginatedList<E> {

    private static final Logger LOG = LoggerFactory.getLogger(PaginatedListAdapter.class);

    private static final String READ_ONLY = "This list is read-only.";

    private static final String SORT_NOT_SUPPORTED = "Stream the elements of this List into a new List before sorting.";

    private static final String LIST_ITR_NOT_SUPPORTED = "Stream the elements of this List into a new List before " +
            "obtaining a ListIterator.";

    private static final String CONTAINS_ALL_NOT_SUPPORTED = "Stream the elements of this List into a new List before" +
            " performing Collection.containsAll(Collection).";

    private final ResourceList<E> resources;

    private final OkHttpClient okHttp;

    private final ResourceConverter resourceConverter;

    private final Class<E> clazz;

    /**
     * Adapts the supplied {@code ResourceList} as a {@code PaginatedList}.  The supplied {@code ResourceList}
     * represents the results from an API call that returns a collection of objects.  The response from the API may
     * be paginated.  In that case, supplied {@code ResourceList} will only represent the first page of the results.
     * The OkHttp client, ResourceConverter, and class type are used to retrieve and unmarshal additional pages as
     * requested by the caller.
     *
     * @param okHttp the OkHttp client used to request additional pages of a paginated response
     * @param resourceConverter the JSONAPI-converter used to unmarshal JSON from a response into Java objects
     * @param clazz the type of object being retrieved in the response
     * @param resources the first page of a response, which may have additional pages
     */
    public PaginatedListAdapter(final OkHttpClient okHttp, final ResourceConverter resourceConverter,
                                final Class<E> clazz, final ResourceList<E> resources) {
        if (okHttp == null) {
            throw new IllegalArgumentException("OkHttpClient must not be null.");
        }

        if (resourceConverter == null) {
            throw new IllegalArgumentException("ResourceConverter must not be null.");
        }

        if (clazz == null) {
            throw new IllegalArgumentException("Class type must not be null.");
        }

        if (resources == null) {
            throw new IllegalArgumentException("Supplied resources list must not be null.");
        }

        this.resources = resources;
        this.resourceConverter = resourceConverter;
        this.okHttp = okHttp;
        this.clazz = clazz;
    }

    @Override
    public String getNext() {
        return resources.getNext();
    }

    @Override
    public String getPrevious() {
        return resources.getPrevious();
    }

    @Override
    public String getFirst() {
        return resources.getFirst();
    }

    @Override
    public String getLast() {
        return resources.getLast();
    }

    @Override
    public int total() {
        if (resources.getMeta() != null && resources.getMeta().get("total") != null) {
            try {
                return (Integer) resources.getMeta().get("total");
            } catch (Exception e) {
                LOG.debug("Error parsing 'total' value as an Integer: '{}'", resources.getMeta().get("total"), e);
            }
        }

        // If there is no total, but there is a "next" link, then pagination is possible, but the size of the result
        // is unknown, so it is proper to return -1.
        //
        // Note that OSF API versions prior to 2.2 will exhibit this behavior
        if (resources.getNext() != null) {
            return -1;
        }

        // If there is no "next" link, pagination is not possible, so simply use the size if the underlying ResourceList
        return resources.size();
    }

    @Override
    public int perPage() {
        if (resources.getMeta() != null && resources.getMeta().get("per_page") != null) {
            try {
                return (Integer) resources.getMeta().get("per_page");
            } catch (Exception e) {
                LOG.debug("Error parsing 'per_page' value as an Integer: '{}'", resources.getMeta().get("per_page"), e);
            }
        }

        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new PagingIterator<>(okHttp, resourceConverter, resources, clazz);
    }

    /**
     * <p>
     * Implementation note: returns a Spliterator that is ORDERED and NONNULL.  If the total size of the collection is
     * known, it will also carry the SIZED and SUBSIZED characteristics.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Spliterator<E> spliterator() {
        final PagingIterator<E> iterator = new PagingIterator<>(okHttp, resourceConverter, resources, clazz);
        final int flags = Spliterator.ORDERED | Spliterator.NONNULL;

        if (total() > -1) {
            return Spliterators.spliterator(iterator, total(), flags);
        }

        return Spliterators.spliteratorUnknownSize(iterator, flags);
    }

    /**
     * <p>
     * Implementation note: provides sequential access to the collection by requesting additional elements in the
     * background.  Therefore, access to elements of the stream may block as new elements are requested by this
     * implementation.  Callers are advised to catch {@code RuntimeException} when processing the stream.  This
     * implementation does not hold any resources, so explicitly (via {@link Stream#close()}) or implicitly (via
     * {@link AutoCloseable}) closing this stream is not required.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public Stream<E> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * <p>
     * Implementation note: simply returns a sequential stream; parallel streams are not supported.
     * </p>
     *
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public Stream<E> parallelStream() {
        return stream();
    }

    @Override
    public void forEach(final Consumer<? super E> action) {
        stream().forEach(action);
    }

    @Override
    public int size() {
        return total();
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public Object[] toArray() {
        return stream().toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        final Object[] elements = toArray();

        if (a.length < elements.length) {
            return (T[]) Arrays.copyOf(elements, elements.length, a.getClass());
        }

        System.arraycopy(elements, 0, a, 0, elements.length);
        if (a.length > elements.length) {
            a[elements.length] = null;
        }

        return a;
    }

    @Override
    public E get(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index must be a positive integer");
        }

        // if size is supported, check the upper bounds of the index
        if (size() > -1 && index > size()) {
            throw new IndexOutOfBoundsException("Supplied index '" + index + "' was greater than the list size '" +
                    size() + "'");
        }

        return stream().skip(index).findFirst().orElseThrow(() ->
                new IndexOutOfBoundsException("Unable to retrieve element at index " + index));
    }

    @Override
    public boolean contains(final Object o) {
        return indexOfInternal(o, true) > -1;
    }

    @Override
    public int indexOf(final Object o) {
        return indexOfInternal(o, true);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return indexOfInternal(o, false);
    }

    /**
     * Advances sequentially through the stream, stopping at the first matching object if {@code shortCircuit} is
     * {@code true}.  Will scan the entire stream if {@code shortCircuit} is {@code false}.  It may make sense to
     * start at the end of the results and work backward if this implementation is too inefficient.
     *
     * @param o the object to match
     * @param shortCircuit {@code true} to stop at the first match, will scan the entire stream otherwise
     * @return the first ({@code shortCircuit} = {@code true}) or last ({@code shortCircuit} = {@code false}) index of
     *         {@code o} in the list.  Returns {@code -1} if {@code o} is not found.
     */
    private int indexOfInternal(final Object o, final boolean shortCircuit) {
        if (size() <= 0) {
            return -1;
        }

        // current position in the list
        final AtomicInteger i = new AtomicInteger(0);

        // position of the most recently matched object
        final AtomicInteger j = new AtomicInteger(-1);

        final Stream<E> stream = stream().filter(e -> {
            if (o == null ? e == null : o.equals(e)) {
                j.set(i.getAndIncrement());
                return true;
            }
            i.getAndIncrement();
            return false;
        });

        if (shortCircuit) {
            stream.findFirst();
        } else {
            // any terminal operation will work
            stream.count();
        }

        // will contain the first index found (short-circuit = true) or the last index found (short-circuit = false)
        return j.get();
    }

    /**
     * {@inheritDoc}
     *
     * @param fromIndex {@inheritDoc}
     * @param toIndex {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be less than or equal toIndex");
        }

        if (fromIndex < 0) {
            throw new IllegalArgumentException("fromIndex must be a positive integer");
        }

        if (size() > -1 && toIndex > size()) {
            throw new IllegalArgumentException("toIndex '" + toIndex + "' must be less than or equal to the size of" +
                    "this List '" + size() + "'");
        }

        return stream()
                .skip(fromIndex)
                .limit(toIndex - fromIndex)
                .collect(Collectors.toList());
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new UnsupportedOperationException(CONTAINS_ALL_NOT_SUPPORTED);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException(LIST_ITR_NOT_SUPPORTED);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public ListIterator<E> listIterator(final int index) {
        throw new UnsupportedOperationException(LIST_ITR_NOT_SUPPORTED);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     */
    @Override
    public void sort(final Comparator<? super E> c) {
        throw new UnsupportedOperationException(SORT_NOT_SUPPORTED);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param operator {@inheritDoc}
     */
    @Override
    public void replaceAll(final UnaryOperator<E> operator) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param c {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @param element {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public E set(final int index, final E element) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @param element {@inheritDoc}
     */
    @Override
    public void add(final int index, final E element) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param index {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param e {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean add(final E e) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param o {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

    /**
     * <p>
     * Implementation note: throws {@code UnsupportedOperationException}
     * </p>
     * {@inheritDoc}
     *
     * @param filter {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean removeIf(final Predicate<? super E> filter) {
        throw new UnsupportedOperationException(READ_ONLY);
    }

}

