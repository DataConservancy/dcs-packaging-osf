/*
 * Copyright 2016 Johns Hopkins University
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.dataconservancy.cos.rdf.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A {@code Map} of {@link AnnotatedElementPair}s that carries an extra bit of state: a {@code Set} of
 * {@code AnnotatedElement}s that have been seen by an annotation processor.  This state is an implementation
 * detail, and not exposed to users of this class.  This implementation delegates all {@code Map} methods to the
 * underlying {@code Map}.
 *
 * @author Elliot Metsger (emetsger@jhu.edu)
 */
public class AnnotatedElementPairMap<K, V> implements Map<K, V> {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotatedElementPairMap.class);

    private final Set<AnnotatedElement> seen;

    private final Map<K, V> delegateMap;

    /**
     * Instantiates a default, empty, {@code Map} to back this {@code AnnotatedElementPairMap}, and an empty
     * {@code Set} to track seen {@code AnnotatedElement}s.
     */
    public AnnotatedElementPairMap() {
        delegateMap = new HashMap<>();
        seen = new HashSet<>();
    }

    /**
     * Uses the supplied {@code delegateMap} to back this {@code AnnotatedElementPairMap}, and an empty {@code Set} to
     * track seen {@code AnnotatedElement}s.
     *
     * @param delegateMap the {@code Map} used to store {@code AnnotatedElementPair}s
     */
    public AnnotatedElementPairMap(final Map<K, V> delegateMap) {
        this.delegateMap = delegateMap;
        seen = new HashSet<>();
    }

    /**
     * Uses the supplied {@code delegateMap} to back this {@code AnnotatedElementPairMap}, and the supplied {@code Set}
     * to track seen {@code AnnotatedElement}s.
     *
     * @param delegateMap the {@code Map} used to store {@code AnnotatedElementPair}s
     * @param seen the {@code Set} used to record seen {@code AnnotatedElement}s
     */
    public AnnotatedElementPairMap(final Map<K, V> delegateMap, final Set<AnnotatedElement> seen) {
        this.delegateMap = delegateMap;
        this.seen = seen;
    }

    /**
     * Records the supplied {@code annotatedElement} as being seen by an annotation processor and returns
     * true if the {@code annotatedElement} has already been seen.  If this method returns {@code true}, then this
     * {@code Map} already contains the {@link AnnotatedElementPair}s that may annotate the {@code annotatedElement}.
     *
     * @param annotatedElement the {@code AnnotatedElement} that has been seen by an annotation processor
     * @return if it has already been seen
     */
    boolean seen(final AnnotatedElement annotatedElement) {
        final boolean seen = !this.seen.add(annotatedElement);
        if (seen) {
            LOG.trace("Ignoring already seen AnnotatedElement: '{}'", annotatedElement.toString());
        }
        return seen;
    }

    /**
     * Checks to see if the supplied {@code annotatedElement} has been seen, without modifying any state internal to
     * this class.
     *
     * @param annotatedElement the {@code AnnotatedElement} that may or may not have been marked as 'seen'
     * @return true if the {@code annotatedElement} has been marked as 'seen'
     * @see #seen
     */
    boolean hasSeen(final AnnotatedElement annotatedElement) {
        return this.seen.contains(annotatedElement);
    }

    @Override
    public int size() {
        return delegateMap.size();
    }

    @Override
    public boolean isEmpty() {
        return delegateMap.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return delegateMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegateMap.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return delegateMap.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return delegateMap.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return delegateMap.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        delegateMap.putAll(m);
    }

    @Override
    public void clear() {
        delegateMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegateMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegateMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return delegateMap.entrySet();
    }

    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        return delegateMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(final BiConsumer<? super K, ? super V> action) {
        delegateMap.forEach(action);
    }

    @Override
    public void replaceAll(final BiFunction<? super K, ? super V, ? extends V> function) {
        delegateMap.replaceAll(function);
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        return delegateMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(final Object key, final Object value) {
        return delegateMap.remove(key, value);
    }

    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return delegateMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(final K key, final V value) {
        return delegateMap.replace(key, value);
    }

    @Override
    public V computeIfAbsent(final K key, final Function<? super K, ? extends V> mappingFunction) {
        return delegateMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegateMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(final K key, final BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegateMap.compute(key, remappingFunction);
    }

    @Override
    public V merge(final K key, final V value, final BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegateMap.merge(key, value, remappingFunction);
    }
}
