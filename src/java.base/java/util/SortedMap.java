package java.util;

public interface SortedMap<K,V> extends SequencedMap<K,V> {
    Comparator<? super K> comparator();

    SortedMap<K,V> subMap(K fromKey, K toKey);

    SortedMap<K,V> headMap(K toKey);

    SortedMap<K,V> tailMap(K fromKey);

    K firstKey();

    K lastKey();

    Set<K> keySet();

    Collection<V> values();

    Set<Map.Entry<K, V>> entrySet();

    default V putFirst(K k, V v) {
        throw new UnsupportedOperationException();
    }

    default V putLast(K k, V v) {
        throw new UnsupportedOperationException();
    }

    default SortedMap<K, V> reversed() {
        return ReverseOrderSortedMapView.of(this);
    }
}
