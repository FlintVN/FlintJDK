package java.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Map<K, V> {
    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    V get(Object key);

    V put(K key, V value);

    V remove(Object key);

    void putAll(Map<? extends K, ? extends V> m);

    void clear();

    Set<K> keySet();

    Collection<V> values();

    Set<Map.Entry<K, V>> entrySet();

    interface Entry<K, V> {
        K getKey();

        V getValue();

        V setValue(V value);

        boolean equals(Object o);

        int hashCode();

        public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K, V>> comparingByKey() {
            return new Comparator<Map.Entry<K, V>>() {
                @Override
                public int compare(Map.Entry<K, V> c1, Map.Entry<K, V> c2) {
                    return c1.getKey().compareTo(c2.getKey());
                }
            };
        }

        public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue() {
            return new Comparator<Map.Entry<K, V>>() {
                @Override
                public int compare(Map.Entry<K, V> c1, Map.Entry<K, V> c2) {
                    return c1.getValue().compareTo(c2.getValue());
                }
            };
        }

        public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
            Objects.requireNonNull(cmp);
            return new Comparator<Map.Entry<K, V>>() {
                @Override
                public int compare(Map.Entry<K, V> c1, Map.Entry<K, V> c2) {
                    return cmp.compare(c1.getKey(), c2.getKey());
                }
            };
        }

        public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
            Objects.requireNonNull(cmp);
            return new Comparator<Map.Entry<K, V>>() {
                @Override
                public int compare(Map.Entry<K, V> c1, Map.Entry<K, V> c2) {
                    return cmp.compare(c1.getValue(), c2.getValue());
                }
            };
        }

        @SuppressWarnings("unchecked")
        public static <K, V> Map.Entry<K, V> copyOf(Map.Entry<? extends K, ? extends V> e) {
            Objects.requireNonNull(e);
            if(e instanceof KeyValueHolder)
                return (Map.Entry<K, V>) e;
            else
                return Map.entry(e.getKey(), e.getValue());
        }
    }

    boolean equals(Object o);

    int hashCode();

    default V getOrDefault(Object key, V defaultValue) {
        V v;
        return (((v = get(key)) != null) || containsKey(key)) ? v : defaultValue;
    }

    default void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            }
            catch (IllegalStateException ise) {
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, v);
        }
    }

    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        for (Map.Entry<K, V> entry : entrySet()) {
            K k;
            V v;
            try {
                k = entry.getKey();
                v = entry.getValue();
            }
            catch (IllegalStateException ise) {
                throw new ConcurrentModificationException(ise);
            }

            v = function.apply(k, v);

            try {
                entry.setValue(v);
            }
            catch (IllegalStateException ise) {
                throw new ConcurrentModificationException(ise);
            }
        }
    }

    default V putIfAbsent(K key, V value) {
        V v = get(key);
        if(v == null)
            v = put(key, value);

        return v;
    }

    default boolean remove(Object key, Object value) {
        Object curValue = get(key);
        if(!Objects.equals(curValue, value) ||
            (curValue == null && !containsKey(key))) {
            return false;
        }
        remove(key);
        return true;
    }

    default boolean replace(K key, V oldValue, V newValue) {
        Object curValue = get(key);
        if(!Objects.equals(curValue, oldValue) ||
            (curValue == null && !containsKey(key))) {
            return false;
        }
        put(key, newValue);
        return true;
    }

    default V replace(K key, V value) {
        V curValue;
        if(((curValue = get(key)) != null) || containsKey(key))
            curValue = put(key, value);
        return curValue;
    }

    default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if((v = get(key)) == null) {
            V newValue;
            if((newValue = mappingFunction.apply(key)) != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    default V computeIfPresent(K key,
            BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue;
        if((oldValue = get(key)) != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if(newValue != null) {
                put(key, newValue);
                return newValue;
            }
            else {
                remove(key);
                return null;
            }
        }
        else
            return null;
    }

    default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);

        V newValue = remappingFunction.apply(key, oldValue);
        if(newValue == null) {
            if(oldValue != null || containsKey(key)) {
                remove(key);
                return null;
            }
            else
                return null;
        }
        else {
            put(key, newValue);
            return newValue;
        }
    }

    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (oldValue == null) ? value : remappingFunction.apply(oldValue, value);
        if(newValue == null)
            remove(key);
        else
            put(key, newValue);
        return newValue;
    }

    // TODO
    // @SuppressWarnings("unchecked")
    // static <K, V> Map<K, V> of() {
    //     return (Map<K,V>) ImmutableCollections.EMPTY_MAP;
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1) {
    //     return new ImmutableCollections.Map1<>(k1, v1);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    // }

    // TODO
    // static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
    //     return new ImmutableCollections.MapN<>(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    // }

    // TODO
    // @SafeVarargs
    // @SuppressWarnings("varargs")
    // static <K, V> Map<K, V> ofEntries(Entry<? extends K, ? extends V>... entries) {
    //     if(entries.length == 0) {
    //         @SuppressWarnings("unchecked")
    //         var map = (Map<K,V>) ImmutableCollections.EMPTY_MAP;
    //         return map;
    //     }
    //     else if(entries.length == 1)
    //         return new ImmutableCollections.Map1<>(entries[0].getKey(), entries[0].getValue());
    //     else {
    //         Object[] kva = new Object[entries.length << 1];
    //         int a = 0;
    //         for (Entry<? extends K, ? extends V> entry : entries) {
    //             kva[a++] = entry.getKey();
    //             kva[a++] = entry.getValue();
    //         }
    //         return new ImmutableCollections.MapN<>(kva);
    //     }
    // }

    static <K, V> Entry<K, V> entry(K k, V v) {
        return new KeyValueHolder<>(k, v);
    }

    // TODO
    // @SuppressWarnings({"rawtypes","unchecked"})
    // static <K, V> Map<K, V> copyOf(Map<? extends K, ? extends V> map) {
    //     if(map instanceof ImmutableCollections.AbstractImmutableMap)
    //         return (Map<K,V>)map;
    //     else
    //         return (Map<K,V>)Map.ofEntries(map.entrySet().toArray(new Entry[0]));
    // }
}
