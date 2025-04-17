package jdk.internal.util;

import java.util.Map;
import java.util.Objects;

@jdk.internal.ValueBased
public final class NullableKeyValueHolder<K,V> implements Map.Entry<K,V> {
    final K key;
    final V value;

    public NullableKeyValueHolder(K k, V v) {
        key = k;
        value = v;
    }

    public NullableKeyValueHolder(Map.Entry<K,V> entry) {
        Objects.requireNonNull(entry);
        key = entry.getKey();
        value = entry.getValue();
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Map.Entry<?, ?> e && Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue());
    }

    private int hash(Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }

    @Override
    public int hashCode() {
        return hash(key) ^ hash(value);
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
