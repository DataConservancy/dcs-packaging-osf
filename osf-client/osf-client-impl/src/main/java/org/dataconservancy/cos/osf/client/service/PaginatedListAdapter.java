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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Adapts a {@link ResourceList} to the {@code PaginatedList} interface.  In addition to providing the implementation
 * for the {@code PaginatedList} methods, this implementation also enables Java 8 streams, allowing a client
 * to stream elements from all result pages, in encounter order, without having to explicitly request additional pages
 * from the API.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class PaginatedListAdapter<T> implements PaginatedList<T> {

    private static final Logger LOG = LoggerFactory.getLogger(PaginatedListAdapter.class);

    private final ResourceList<T> resources;

    private final OkHttpClient okHttp;

    private final ResourceConverter resourceConverter;

    private final Class<T> clazz;

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
                                final Class<T> clazz, final ResourceList<T> resources) {
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
                return (Integer)resources.getMeta().get("total");
            } catch (Exception e) {
                LOG.debug("Error parsing 'total' value as an Integer: '{}'", resources.getMeta().get("total"), e);
            }
        }

        return -1;
    }

    @Override
    public int perPage() {
        if (resources.getMeta() != null && resources.getMeta().get("per_page") != null) {
            try {
                return (Integer)resources.getMeta().get("per_page");
            } catch (Exception e) {
                LOG.debug("Error parsing 'per_page' value as an Integer: '{}'", resources.getMeta().get("per_page"), e);
            }
        }

        return -1;
    }

    @Override
    public Iterator<T> iterator() {
        return new PagingIterator<>(okHttp, resourceConverter, resources, clazz);
    }

    @Override
    public Spliterator<T> spliterator() {
        final PagingIterator<T> iterator = new PagingIterator<>(okHttp, resourceConverter, resources, clazz);
        final int flags = Spliterator.ORDERED | Spliterator.NONNULL;
        if (resources.getMeta() != null && resources.getMeta().get("total") != null) {
            final Integer total = (Integer) resources.getMeta().get("total");
            if (total > -1) {
                return Spliterators.spliterator(iterator, total, flags);
            }
        }

        return Spliterators.spliteratorUnknownSize(iterator, flags);
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public Stream<T> parallelStream() {
        return stream();
    }

    @Override
    public void forEach(final Consumer<? super T> action) {
        resources.forEach(action);
    }

    @Override
    public boolean removeIf(final Predicate<? super T> filter) {
        return resources.removeIf(filter);
    }

    @Override
    public int size() {
        return resources.size();
    }

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return resources.contains(o);
    }

    @Override
    public Object[] toArray() {
        return resources.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] a) {
        return resources.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        return resources.add(t);
    }

    @Override
    public boolean remove(final Object o) {
        return resources.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return resources.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return resources.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return resources.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return resources.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return resources.retainAll(c);
    }

    @Override
    public void clear() {
        resources.clear();
    }

    @Override
    public T get(final int index) {
        return resources.get(index);
    }

    @Override
    public T set(final int index, final T element) {
        return resources.set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        resources.add(index, element);
    }

    @Override
    public T remove(final int index) {
        return resources.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return resources.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return resources.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return resources.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return resources.listIterator(index);
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return resources.subList(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(final UnaryOperator<T> operator) {
        resources.replaceAll(operator);
    }

    @Override
    public void sort(final Comparator<? super T> c) {
        resources.sort(c);
    }

    @Override
    public boolean equals(final Object o) {
        return resources.equals(o);
    }

    @Override
    public int hashCode() {
        return resources.hashCode();
    }

}

