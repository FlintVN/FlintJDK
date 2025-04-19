package java.util;

import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.io.IOException;
import java.util.function.Function;

public class LinkedHashMap<K,V> extends HashMap<K,V> implements SequencedMap<K,V> {
    static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

    transient LinkedHashMap.Entry<K,V> head;

    transient LinkedHashMap.Entry<K,V> tail;

    final boolean accessOrder;

    private void linkNodeAtEnd(LinkedHashMap.Entry<K,V> p) {
        if(putMode == PUT_FIRST) {
            LinkedHashMap.Entry<K,V> first = head;
            head = p;
            if(first == null)
                tail = p;
            else {
                p.after = first;
                first.before = p;
            }
        }
        else {
            LinkedHashMap.Entry<K,V> last = tail;
            tail = p;
            if(last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
        }
    }

    private void transferLinks(LinkedHashMap.Entry<K,V> src, LinkedHashMap.Entry<K,V> dst) {
        LinkedHashMap.Entry<K,V> b = dst.before = src.before;
        LinkedHashMap.Entry<K,V> a = dst.after = src.after;
        if(b == null)
            head = dst;
        else
            b.after = dst;
        if(a == null)
            tail = dst;
        else
            a.before = dst;
    }

    void reinitialize() {
        super.reinitialize();
        head = tail = null;
    }

    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p = new LinkedHashMap.Entry<>(hash, key, value, e);
        linkNodeAtEnd(p);
        return p;
    }

    Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
        LinkedHashMap.Entry<K,V> q = (LinkedHashMap.Entry<K,V>)p;
        LinkedHashMap.Entry<K,V> t = new LinkedHashMap.Entry<>(q.hash, q.key, q.value, next);
        transferLinks(q, t);
        return t;
    }

    TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
        TreeNode<K,V> p = new TreeNode<>(hash, key, value, next);
        linkNodeAtEnd(p);
        return p;
    }

    TreeNode<K,V> replacementTreeNode(Node<K,V> p, Node<K,V> next) {
        LinkedHashMap.Entry<K,V> q = (LinkedHashMap.Entry<K,V>)p;
        TreeNode<K,V> t = new TreeNode<>(q.hash, q.key, q.value, next);
        transferLinks(q, t);
        return t;
    }

    void afterNodeRemoval(Node<K,V> e) {
        LinkedHashMap.Entry<K,V> p = (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
        p.before = p.after = null;
        if(b == null)
            head = a;
        else
            b.after = a;
        if(a == null)
            tail = b;
        else
            a.before = b;
    }

    void afterNodeInsertion(boolean evict) {
        LinkedHashMap.Entry<K,V> first;
        if(evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }

    static final int PUT_NORM = 0;
    static final int PUT_FIRST = 1;
    static final int PUT_LAST = 2;
    transient int putMode = PUT_NORM;

    void afterNodeAccess(Node<K,V> e) {
        LinkedHashMap.Entry<K,V> last;
        LinkedHashMap.Entry<K,V> first;
        if((putMode == PUT_LAST || (putMode == PUT_NORM && accessOrder)) && (last = tail) != e) {
            LinkedHashMap.Entry<K,V> p = (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            p.after = null;
            if(b == null)
                head = a;
            else
                b.after = a;
            if(a != null)
                a.before = b;
            else
                last = b;
            if(last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
        else if(putMode == PUT_FIRST && (first = head) != e) {
            LinkedHashMap.Entry<K,V> p = (LinkedHashMap.Entry<K,V>)e, b = p.before, a = p.after;
            p.before = null;
            if(a == null)
                tail = b;
            else
                a.before = b;
            if(b != null)
                b.after = a;
            else
                first = a;
            if(first == null)
                tail = p;
            else {
                p.after = first;
                first.before = p;
            }
            head = p;
            ++modCount;
        }
    }

    public V putFirst(K k, V v) {
        try {
            putMode = PUT_FIRST;
            return this.put(k, v);
        }
        finally {
            putMode = PUT_NORM;
        }
    }

    public V putLast(K k, V v) {
        try {
            putMode = PUT_LAST;
            return this.put(k, v);
        } finally {
            putMode = PUT_NORM;
        }
    }

    // TODO
    // void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {
    //     for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {
    //         s.writeObject(e.key);
    //         s.writeObject(e.value);
    //     }
    // }

    public LinkedHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }

    public LinkedHashMap(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }

    public LinkedHashMap() {
        super();
        accessOrder = false;
    }

    @SuppressWarnings("this-escape")
    public LinkedHashMap(Map<? extends K, ? extends V> m) {
        super();
        accessOrder = false;
        putMapEntries(m, false);
    }

    public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }

    public boolean containsValue(Object value) {
        for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after) {
            V v = e.value;
            if(v == value || (value != null && value.equals(v)))
                return true;
        }
        return false;
    }

    public V get(Object key) {
        Node<K,V> e;
        if((e = getNode(key)) == null)
            return null;
        if(accessOrder)
            afterNodeAccess(e);
        return e.value;
    }

    public V getOrDefault(Object key, V defaultValue) {
       Node<K,V> e;
       if((e = getNode(key)) == null)
           return defaultValue;
       if(accessOrder)
           afterNodeAccess(e);
       return e.value;
    }

    public void clear() {
        super.clear();
        head = tail = null;
    }

    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return false;
    }

    public Set<K> keySet() {
        return sequencedKeySet();
    }

    public SequencedSet<K> sequencedKeySet() {
        Set<K> ks = keySet;
        if(ks == null) {
            SequencedSet<K> sks = new LinkedKeySet(false);
            keySet = sks;
            return sks;
        }
        else
            return (SequencedSet<K>) ks;
    }

    static <K1,V1> Node<K1,V1> nsee(Node<K1,V1> node) {
        if(node == null)
            throw new NoSuchElementException();
        else
            return node;
    }

    final <T> T[] keysToArray(T[] a) {
        return keysToArray(a, false);
    }

    final <T> T[] keysToArray(T[] a, boolean reversed) {
        Object[] r = a;
        int idx = 0;
        if(reversed) {
            for(LinkedHashMap.Entry<K,V> e = tail; e != null; e = e.before)
                r[idx++] = e.key;
        }
        else {
            for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
                r[idx++] = e.key;
        }
        return a;
    }

    final <T> T[] valuesToArray(T[] a, boolean reversed) {
        Object[] r = a;
        int idx = 0;
        if(reversed) {
            for(LinkedHashMap.Entry<K,V> e = tail; e != null; e = e.before) {
                r[idx++] = e.value;
            }
        }
        else {
            for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
                r[idx++] = e.value;
        }
        return a;
    }

    final class LinkedKeySet extends AbstractSet<K> implements SequencedSet<K> {
        final boolean reversed;
        LinkedKeySet(boolean reversed) {
            this.reversed = reversed;
        }

        public final int size() {
            return size;
        }

        public final void clear() {
            LinkedHashMap.this.clear();
        }

        public final Iterator<K> iterator() {
            return new LinkedKeyIterator(reversed);
        }

        public final boolean contains(Object o) {
            return containsKey(o);
        }

        public final boolean remove(Object key) {
            return removeNode(hash(key), key, null, false, true) != null;
        }

        public final Spliterator<K> spliterator()  {
            return Spliterators.spliterator(this, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.DISTINCT);
        }

        public Object[] toArray() {
            return keysToArray(new Object[size], reversed);
        }

        public <T> T[] toArray(T[] a) {
            return keysToArray(prepareArray(a), reversed);
        }

        public final void forEach(Consumer<? super K> action) {
            if(action == null)
                throw new NullPointerException();
            int mc = modCount;
            if(reversed) {
                for(LinkedHashMap.Entry<K,V> e = tail; e != null; e = e.before)
                    action.accept(e.key);
            }
            else {
                for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
                    action.accept(e.key);
            }
            if(modCount != mc)
                throw new ConcurrentModificationException();
        }

        public final void addFirst(K k) {
            throw new UnsupportedOperationException();
        }

        public final void addLast(K k) {
            throw new UnsupportedOperationException();
        }

        public final K getFirst() {
            return nsee(reversed ? tail : head).key;
        }

        public final K getLast() {
            return nsee(reversed ? head : tail).key;
        }

        public final K removeFirst() {
            var node = nsee(reversed ? tail : head);
            removeNode(node.hash, node.key, null, false, false);
            return node.key;
        }

        public final K removeLast() {
            var node = nsee(reversed ? head : tail);
            removeNode(node.hash, node.key, null, false, false);
            return node.key;
        }

        public SequencedSet<K> reversed() {
            if(reversed)
                return LinkedHashMap.this.sequencedKeySet();
            else
                return new LinkedKeySet(true);
        }
    }

    public Collection<V> values() {
        return sequencedValues();
    }

    public SequencedCollection<V> sequencedValues() {
        Collection<V> vs = values;
        if(vs == null) {
            SequencedCollection<V> svs = new LinkedValues(false);
            values = svs;
            return svs;
        }
        else
            return (SequencedCollection<V>) vs;
    }

    final class LinkedValues extends AbstractCollection<V> implements SequencedCollection<V> {
        final boolean reversed;
        LinkedValues(boolean reversed) {
            this.reversed = reversed;
        }

        public final int size() {
            return size;
        }

        public final void clear() {
            LinkedHashMap.this.clear();
        }

        public final Iterator<V> iterator() {
            return new LinkedValueIterator(reversed);
        }

        public final boolean contains(Object o) {
            return containsValue(o);
        }

        public final Spliterator<V> spliterator() {
            return Spliterators.spliterator(this, Spliterator.SIZED | Spliterator.ORDERED);
        }

        public Object[] toArray() {
            return valuesToArray(new Object[size], reversed);
        }

        public <T> T[] toArray(T[] a) {
            return valuesToArray(prepareArray(a), reversed);
        }

        public final void forEach(Consumer<? super V> action) {
            if(action == null)
                throw new NullPointerException();
            int mc = modCount;
            if(reversed) {
                for(LinkedHashMap.Entry<K,V> e = tail; e != null; e = e.before)
                    action.accept(e.value);
            }
            else {
                for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
                    action.accept(e.value);
            }
            if(modCount != mc)
                throw new ConcurrentModificationException();
        }
        public final void addFirst(V v) {
            throw new UnsupportedOperationException();
        }

        public final void addLast(V v) {
            throw new UnsupportedOperationException();
        }

        public final V getFirst() {
            return nsee(reversed ? tail : head).value;
        }

        public final V getLast() {
            return nsee(reversed ? head : tail).value;
        }

        public final V removeFirst() {
            var node = nsee(reversed ? tail : head);
            removeNode(node.hash, node.key, null, false, false);
            return node.value;
        }

        public final V removeLast() {
            var node = nsee(reversed ? head : tail);
            removeNode(node.hash, node.key, null, false, false);
            return node.value;
        }

        public SequencedCollection<V> reversed() {
            if(reversed)
                return LinkedHashMap.this.sequencedValues();
            else
                return new LinkedValues(true);
        }
    }

    public Set<Map.Entry<K,V>> entrySet() {
        return sequencedEntrySet();
    }

    public SequencedSet<Map.Entry<K, V>> sequencedEntrySet() {
        Set<Map.Entry<K, V>> es = entrySet;
        if(es == null) {
            SequencedSet<Map.Entry<K, V>> ses = new LinkedEntrySet(false);
            entrySet = ses;
            return ses;
        }
        else
            return (SequencedSet<Map.Entry<K, V>>) es;
    }

    final class LinkedEntrySet extends AbstractSet<Map.Entry<K,V>> implements SequencedSet<Map.Entry<K,V>> {
        final boolean reversed;
        LinkedEntrySet(boolean reversed) {
            this.reversed = reversed;
        }

        public final int size() {
            return size;
        }

        public final void clear() {
            LinkedHashMap.this.clear();
        }

        public final Iterator<Map.Entry<K,V>> iterator() {
            return new LinkedEntryIterator(reversed);
        }

        public final boolean contains(Object o) {
            if(!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            Node<K,V> candidate = getNode(key);
            return candidate != null && candidate.equals(e);
        }

        public final boolean remove(Object o) {
            if(o instanceof Map.Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }

        public final Spliterator<Map.Entry<K,V>> spliterator() {
            return Spliterators.spliterator(this, Spliterator.SIZED | Spliterator.ORDERED | Spliterator.DISTINCT);
        }

        public final void forEach(Consumer<? super Map.Entry<K,V>> action) {
            if(action == null)
                throw new NullPointerException();
            int mc = modCount;
            if(reversed) {
                for(LinkedHashMap.Entry<K,V> e = tail; e != null; e = e.before)
                    action.accept(e);
            }
            else {
                for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
                    action.accept(e);
            }
            if(modCount != mc)
                throw new ConcurrentModificationException();
        }

        final Node<K,V> nsee(Node<K,V> e) {
            if(e == null)
                throw new NoSuchElementException();
            else
                return e;
        }

        public final void addFirst(Map.Entry<K,V> e) {
            throw new UnsupportedOperationException();
        }

        public final void addLast(Map.Entry<K,V> e) {
            throw new UnsupportedOperationException();
        }

        public final Map.Entry<K,V> getFirst() {
            return nsee(reversed ? tail : head);
        }

        public final Map.Entry<K,V> getLast() {
            return nsee(reversed ? head : tail);
        }

        public final Map.Entry<K,V> removeFirst() {
            var node = nsee(reversed ? tail : head);
            removeNode(node.hash, node.key, null, false, false);
            return node;
        }

        public final Map.Entry<K,V> removeLast() {
            var node = nsee(reversed ? head : tail);
            removeNode(node.hash, node.key, null, false, false);
            return node;
        }

        public SequencedSet<Map.Entry<K,V>> reversed() {
            if(reversed)
                return LinkedHashMap.this.sequencedEntrySet();
            else
                return new LinkedEntrySet(true);
        }
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        if(action == null)
            throw new NullPointerException();
        int mc = modCount;
        for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
            action.accept(e.key, e.value);
        if(modCount != mc)
            throw new ConcurrentModificationException();
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if(function == null)
            throw new NullPointerException();
        int mc = modCount;
        for(LinkedHashMap.Entry<K,V> e = head; e != null; e = e.after)
            e.value = function.apply(e.key, e.value);
        if(modCount != mc)
            throw new ConcurrentModificationException();
    }

    abstract class LinkedHashIterator {
        LinkedHashMap.Entry<K,V> next;
        LinkedHashMap.Entry<K,V> current;
        int expectedModCount;
        boolean reversed;

        LinkedHashIterator(boolean reversed) {
            this.reversed = reversed;
            next = reversed ? tail : head;
            expectedModCount = modCount;
            current = null;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final LinkedHashMap.Entry<K,V> nextNode() {
            LinkedHashMap.Entry<K,V> e = next;
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if(e == null)
                throw new NoSuchElementException();
            current = e;
            next = reversed ? e.before : e.after;
            return e;
        }

        public final void remove() {
            Node<K,V> p = current;
            if(p == null)
                throw new IllegalStateException();
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            removeNode(p.hash, p.key, null, false, false);
            expectedModCount = modCount;
        }
    }

    final class LinkedKeyIterator extends LinkedHashIterator implements Iterator<K> {
        LinkedKeyIterator(boolean reversed) { super(reversed); }
        public final K next() { return nextNode().getKey(); }
    }

    final class LinkedValueIterator extends LinkedHashIterator implements Iterator<V> {
        LinkedValueIterator(boolean reversed) { super(reversed); }
        public final V next() { return nextNode().value; }
    }

    final class LinkedEntryIterator extends LinkedHashIterator implements Iterator<Map.Entry<K,V>> {
        LinkedEntryIterator(boolean reversed) { super(reversed); }
        public final Map.Entry<K,V> next() { return nextNode(); }
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(int numMappings) {
        if(numMappings < 0)
            throw new IllegalArgumentException("Negative number of mappings: " + numMappings);
        return new LinkedHashMap<>(HashMap.calculateHashMapCapacity(numMappings));
    }

    public SequencedMap<K, V> reversed() {
        return new ReversedLinkedHashMapView<>(this);
    }

    static class ReversedLinkedHashMapView<K, V> extends AbstractMap<K, V> implements SequencedMap<K, V> {
        final LinkedHashMap<K, V> base;

        ReversedLinkedHashMapView(LinkedHashMap<K, V> lhm) {
            base = lhm;
        }

        public boolean equals(Object o) {
            return base.equals(o);
        }

        public int hashCode() {
            return base.hashCode();
        }

        public int size() {
            return base.size();
        }

        public boolean isEmpty() {
            return base.isEmpty();
        }

        public boolean containsKey(Object key) {
            return base.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return base.containsValue(value);
        }

        public V get(Object key) {
            return base.get(key);
        }

        public V put(K key, V value) {
            return base.put(key, value);
        }

        public V remove(Object key) {
            return base.remove(key);
        }

        public void putAll(Map<? extends K, ? extends V> m) {
            base.putAll(m);
        }

        public void clear() {
            base.clear();
        }

        public Set<K> keySet() {
            return base.sequencedKeySet().reversed();
        }

        public Collection<V> values() {
            return base.sequencedValues().reversed();
        }

        public Set<Entry<K, V>> entrySet() {
            return base.sequencedEntrySet().reversed();
        }

        public V getOrDefault(Object key, V defaultValue) {
            return base.getOrDefault(key, defaultValue);
        }

        public void forEach(BiConsumer<? super K, ? super V> action) {
            if(action == null)
                throw new NullPointerException();
            int mc = base.modCount;
            for(LinkedHashMap.Entry<K,V> e = base.tail; e != null; e = e.before)
                action.accept(e.key, e.value);
            if(base.modCount != mc)
                throw new ConcurrentModificationException();
        }

        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            if(function == null)
                throw new NullPointerException();
            int mc = base.modCount;
            for(LinkedHashMap.Entry<K,V> e = base.tail; e != null; e = e.before)
                e.value = function.apply(e.key, e.value);
            if(base.modCount != mc)
                throw new ConcurrentModificationException();
        }

        public V putIfAbsent(K key, V value) {
            return base.putIfAbsent(key, value);
        }

        public boolean remove(Object key, Object value) {
            return base.remove(key, value);
        }

        public boolean replace(K key, V oldValue, V newValue) {
            return base.replace(key, oldValue, newValue);
        }

        public V replace(K key, V value) {
            return base.replace(key, value);
        }

        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return base.computeIfAbsent(key, mappingFunction);
        }

        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return base.computeIfPresent(key, remappingFunction);
        }

        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return base.compute(key, remappingFunction);
        }

        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return base.merge(key, value, remappingFunction);
        }

        public SequencedMap<K, V> reversed() {
            return base;
        }

        public Entry<K, V> firstEntry() {
            return base.lastEntry();
        }

        public Entry<K, V> lastEntry() {
            return base.firstEntry();
        }

        public Entry<K, V> pollFirstEntry() {
            return base.pollLastEntry();
        }

        public Entry<K, V> pollLastEntry() {
            return base.pollFirstEntry();
        }

        public V putFirst(K k, V v) {
            return base.putLast(k, v);
        }

        public V putLast(K k, V v) {
            return base.putFirst(k, v);
        }
    }
}
