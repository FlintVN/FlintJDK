package java.util;

import jdk.internal.util.NullableKeyValueHolder;

public interface SequencedMap<K, V> extends Map<K, V> {
    SequencedMap<K, V> reversed();

    default Map.Entry<K,V> firstEntry() {
        var it = entrySet().iterator();
        return it.hasNext() ? new NullableKeyValueHolder<>(it.next()) : null;
    }

    default Map.Entry<K,V> lastEntry() {
        var it = reversed().entrySet().iterator();
        return it.hasNext() ? new NullableKeyValueHolder<>(it.next()) : null;
    }

    default Map.Entry<K,V> pollFirstEntry() {
        var it = entrySet().iterator();
        if(it.hasNext()) {
            var entry = new NullableKeyValueHolder<>(it.next());
            it.remove();
            return entry;
        }
        else
            return null;
    }

    default Map.Entry<K,V> pollLastEntry() {
        var it = reversed().entrySet().iterator();
        if(it.hasNext()) {
            var entry = new NullableKeyValueHolder<>(it.next());
            it.remove();
            return entry;
        }
        else
            return null;
    }

    default V putFirst(K k, V v) {
        throw new UnsupportedOperationException();
    }

    default V putLast(K k, V v) {
        throw new UnsupportedOperationException();
    }

    default SequencedSet<K> sequencedKeySet() {
        class SeqKeySet extends AbstractMap.ViewCollection<K> implements SequencedSet<K> {
            Collection<K> view() {
                return SequencedMap.this.keySet();
            }

            public SequencedSet<K> reversed() {
                return SequencedMap.this.reversed().sequencedKeySet();
            }

            public boolean equals(Object other) {
                return view().equals(other);
            }

            public int hashCode() {
                return view().hashCode();
            }
        }
        return new SeqKeySet();
    }

    default SequencedCollection<V> sequencedValues() {
        class SeqValues extends AbstractMap.ViewCollection<V> implements SequencedCollection<V> {
            Collection<V> view() {
                return SequencedMap.this.values();
            }

            public SequencedCollection<V> reversed() {
                return SequencedMap.this.reversed().sequencedValues();
            }
        }
        return new SeqValues();
    }

    default SequencedSet<Map.Entry<K, V>> sequencedEntrySet() {
        class SeqEntrySet extends AbstractMap.ViewCollection<Map.Entry<K, V>> implements SequencedSet<Map.Entry<K, V>> {
            Collection<Map.Entry<K, V>> view() {
                return SequencedMap.this.entrySet();
            }

            public SequencedSet<Map.Entry<K, V>> reversed() {
                return SequencedMap.this.reversed().sequencedEntrySet();
            }

            public boolean equals(Object other) {
                return view().equals(other);
            }

            public int hashCode() {
                return view().hashCode();
            }
        }
        return new SeqEntrySet();
    }
}
