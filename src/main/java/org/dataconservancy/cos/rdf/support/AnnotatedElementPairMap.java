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
    public AnnotatedElementPairMap(Map<K, V> delegateMap) {
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
    public AnnotatedElementPairMap(Map<K, V> delegateMap, Set<AnnotatedElement> seen) {
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
    boolean seen(AnnotatedElement annotatedElement) {
        boolean seen = !this.seen.add(annotatedElement);
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
    boolean hasSeen(AnnotatedElement annotatedElement) {
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
    public boolean containsKey(Object key) {
        return delegateMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegateMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return delegateMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return delegateMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return delegateMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
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
    public V getOrDefault(Object key, V defaultValue) {
        return delegateMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        delegateMap.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        delegateMap.replaceAll(function);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return delegateMap.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return delegateMap.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return delegateMap.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return delegateMap.replace(key, value);
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return delegateMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegateMap.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return delegateMap.compute(key, remappingFunction);
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return delegateMap.merge(key, value, remappingFunction);
    }
}
