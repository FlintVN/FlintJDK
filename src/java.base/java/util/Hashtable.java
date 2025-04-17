package java.util;

import java.io.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.BiFunction;
// import jdk.internal.access.SharedSecrets;

public class Hashtable<K,V> extends Dictionary<K,V> implements Map<K,V>, Cloneable {
    private transient Entry<?,?>[] table;

    private transient int count;

    private int threshold;

    private float loadFactor;

    private transient int modCount = 0;

    public Hashtable(int initialCapacity, float loadFactor) {
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        if(loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load: " + loadFactor);

        if(initialCapacity == 0)
            initialCapacity = 1;
        this.loadFactor = loadFactor;
        table = new Entry<?,?>[initialCapacity];
        threshold = (int)Math.min(initialCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
    }

    public Hashtable(int initialCapacity) {
        this(initialCapacity, 0.75f);
    }

    public Hashtable() {
        this(11, 0.75f);
    }

    @SuppressWarnings("this-escape")
    public Hashtable(Map<? extends K, ? extends V> t) {
        this(Math.max(2*t.size(), 11), 0.75f);
        putAll(t);
    }

    Hashtable(Void dummy) {}

    public synchronized int size() {
        return count;
    }

    public synchronized boolean isEmpty() {
        return count == 0;
    }

    public synchronized Enumeration<K> keys() {
        return this.<K>getEnumeration(KEYS);
    }

    public synchronized Enumeration<V> elements() {
        return this.<V>getEnumeration(VALUES);
    }

    public synchronized boolean contains(Object value) {
        if(value == null)
            throw new NullPointerException();

        Entry<?,?> tab[] = table;
        for (int i = tab.length ; i-- > 0 ;) {
            for (Entry<?,?> e = tab[i] ; e != null ; e = e.next) {
                if(e.value.equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsValue(Object value) {
        return contains(value);
    }

    public synchronized boolean containsKey(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if((e.hash == hash) && e.key.equals(key))
                return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public synchronized V get(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if((e.hash == hash) && e.key.equals(key)) {
                return (V)e.value;
            }
        }
        return null;
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    @SuppressWarnings("unchecked")
    protected void rehash() {
        int oldCapacity = table.length;
        Entry<?,?>[] oldMap = table;

        int newCapacity = (oldCapacity << 1) + 1;
        if(newCapacity - MAX_ARRAY_SIZE > 0) {
            if(oldCapacity == MAX_ARRAY_SIZE)
                return;
            newCapacity = MAX_ARRAY_SIZE;
        }
        Entry<?,?>[] newMap = new Entry<?,?>[newCapacity];

        modCount++;
        threshold = (int)Math.min(newCapacity * loadFactor, MAX_ARRAY_SIZE + 1);
        table = newMap;

        for (int i = oldCapacity ; i-- > 0 ;) {
            for (Entry<K,V> old = (Entry<K,V>)oldMap[i] ; old != null ; ) {
                Entry<K,V> e = old;
                old = old.next;

                int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                e.next = (Entry<K,V>)newMap[index];
                newMap[index] = e;
            }
        }
    }

    private void addEntry(int hash, K key, V value, int index) {
        Entry<?,?> tab[] = table;
        if(count >= threshold) {
            rehash();

            tab = table;
            hash = key.hashCode();
            index = (hash & 0x7FFFFFFF) % tab.length;
        }

        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>) tab[index];
        tab[index] = new Entry<>(hash, key, value, e);
        count++;
        modCount++;
    }

    public synchronized V put(K key, V value) {
        if(value == null)
            throw new NullPointerException();

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> entry = (Entry<K,V>)tab[index];
        for(; entry != null ; entry = entry.next) {
            if((entry.hash == hash) && entry.key.equals(key)) {
                V old = entry.value;
                entry.value = value;
                return old;
            }
        }

        addEntry(hash, key, value, index);
        return null;
    }

    public synchronized V remove(Object key) {
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for(Entry<K,V> prev = null ; e != null ; prev = e, e = e.next) {
            if((e.hash == hash) && e.key.equals(key)) {
                if(prev != null)
                    prev.next = e.next;
                else
                    tab[index] = e.next;
                modCount++;
                count--;
                V oldValue = e.value;
                e.value = null;
                return oldValue;
            }
        }
        return null;
    }

    public synchronized void putAll(Map<? extends K, ? extends V> t) {
        for (Map.Entry<? extends K, ? extends V> e : t.entrySet())
            put(e.getKey(), e.getValue());
    }


    public synchronized void clear() {
        Entry<?,?> tab[] = table;
        for (int index = tab.length; --index >= 0; )
            tab[index] = null;
        modCount++;
        count = 0;
    }

    public synchronized Object clone() {
        Hashtable<?,?> t = cloneHashtable();
        t.table = new Entry<?,?>[table.length];
        for (int i = table.length ; i-- > 0 ; ) {
            t.table[i] = (table[i] != null)
                ? (Entry<?,?>) table[i].clone() : null;
        }
        t.keySet = null;
        t.entrySet = null;
        t.values = null;
        t.modCount = 0;
        return t;
    }

    final Hashtable<?,?> cloneHashtable() {
        try {
            return (Hashtable<?,?>)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public synchronized String toString() {
        int max = size() - 1;
        if(max == -1)
            return "{}";

        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<K,V>> it = entrySet().iterator();

        sb.append('{');
        for (int i = 0; ; i++) {
            Map.Entry<K,V> e = it.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key.toString());
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value.toString());

            if(i == max)
                return sb.append('}').toString();
            sb.append(", ");
        }
    }

    private <T> Enumeration<T> getEnumeration(int type) {
        if(count == 0)
            return Collections.emptyEnumeration();
        else
            return new Enumerator<>(type, false);
    }

    private <T> Iterator<T> getIterator(int type) {
        if(count == 0)
            return Collections.emptyIterator();
        else
            return new Enumerator<>(type, true);
    }

    private transient volatile Set<K> keySet;
    private transient volatile Set<Map.Entry<K,V>> entrySet;
    private transient volatile Collection<V> values;

    public Set<K> keySet() {
        if(keySet == null)
            keySet = Collections.synchronizedSet(new KeySet(), this);
        return keySet;
    }

    private class KeySet extends AbstractSet<K> {
        public Iterator<K> iterator() {
            return getIterator(KEYS);
        }

        public int size() {
            return count;
        }

        public boolean contains(Object o) {
            return containsKey(o);
        }

        public boolean remove(Object o) {
            return Hashtable.this.remove(o) != null;
        }

        public void clear() {
            Hashtable.this.clear();
        }
    }

    public Set<Map.Entry<K,V>> entrySet() {
        if(entrySet == null)
            entrySet = Collections.synchronizedSet(new EntrySet(), this);
        return entrySet;
    }

    private class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        public Iterator<Map.Entry<K,V>> iterator() {
            return getIterator(ENTRIES);
        }

        public boolean add(Map.Entry<K,V> o) {
            return super.add(o);
        }

        public boolean contains(Object o) {
            if(!(o instanceof Map.Entry<?, ?> entry))
                return false;
            Object key = entry.getKey();
            Entry<?,?>[] tab = table;
            int hash = key.hashCode();
            int index = (hash & 0x7FFFFFFF) % tab.length;

            for (Entry<?,?> e = tab[index]; e != null; e = e.next)
                if(e.hash == hash && e.equals(entry))
                    return true;
            return false;
        }

        public boolean remove(Object o) {
            if(!(o instanceof Map.Entry<?, ?> entry))
                return false;
            Object key = entry.getKey();
            Entry<?,?>[] tab = table;
            int hash = key.hashCode();
            int index = (hash & 0x7FFFFFFF) % tab.length;

            @SuppressWarnings("unchecked")
            Entry<K,V> e = (Entry<K,V>)tab[index];
            for(Entry<K,V> prev = null; e != null; prev = e, e = e.next) {
                if(e.hash == hash && e.equals(entry)) {
                    if(prev != null)
                        prev.next = e.next;
                    else
                        tab[index] = e.next;

                    e.value = null; // clear for gc.
                    modCount++;
                    count--;
                    return true;
                }
            }
            return false;
        }

        public int size() {
            return count;
        }

        public void clear() {
            Hashtable.this.clear();
        }
    }

    public Collection<V> values() {
        if(values == null)
            values = Collections.synchronizedCollection(new ValueCollection(), this);
        return values;
    }

    private class ValueCollection extends AbstractCollection<V> {
        public Iterator<V> iterator() {
            return getIterator(VALUES);
        }

        public int size() {
            return count;
        }

        public boolean contains(Object o) {
            return containsValue(o);
        }

        public void clear() {
            Hashtable.this.clear();
        }
    }

    public synchronized boolean equals(Object o) {
        if(o == this)
            return true;

        if(!(o instanceof Map<?, ?> t))
            return false;
        if(t.size() != size())
            return false;

        try {
            for (Map.Entry<K, V> e : entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                if(value == null) {
                    if(!(t.get(key) == null && t.containsKey(key)))
                        return false;
                }
                else {
                    if(!value.equals(t.get(key)))
                        return false;
                }
            }
        }
        catch (ClassCastException | NullPointerException unused) {
            return false;
        }

        return true;
    }

    public synchronized int hashCode() {
        int h = 0;
        if(count == 0 || loadFactor < 0)
            return h;

        loadFactor = -loadFactor;
        Entry<?,?>[] tab = table;
        for (Entry<?,?> entry : tab) {
            while (entry != null) {
                h += entry.hashCode();
                entry = entry.next;
            }
        }

        loadFactor = -loadFactor;

        return h;
    }

    @Override
    public synchronized V getOrDefault(Object key, V defaultValue) {
        V result = get(key);
        return (null == result) ? defaultValue : result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void forEach(BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);

        final int expectedModCount = modCount;

        Entry<?, ?>[] tab = table;
        for (Entry<?, ?> entry : tab) {
            while (entry != null) {
                action.accept((K)entry.key, (V)entry.value);
                entry = entry.next;

                if(expectedModCount != modCount)
                    throw new ConcurrentModificationException();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);

        final int expectedModCount = modCount;

        Entry<K, V>[] tab = (Entry<K, V>[])table;
        for (Entry<K, V> entry : tab) {
            while (entry != null) {
                entry.value = Objects.requireNonNull(
                    function.apply(entry.key, entry.value));
                entry = entry.next;

                if(expectedModCount != modCount)
                    throw new ConcurrentModificationException();
            }
        }
    }

    @Override
    public synchronized V putIfAbsent(K key, V value) {
        Objects.requireNonNull(value);

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> entry = (Entry<K,V>)tab[index];
        for (; entry != null; entry = entry.next) {
            if((entry.hash == hash) && entry.key.equals(key)) {
                V old = entry.value;
                if(old == null)
                    entry.value = value;
                return old;
            }
        }

        addEntry(hash, key, value, index);
        return null;
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        Objects.requireNonNull(value);

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (Entry<K,V> prev = null; e != null; prev = e, e = e.next) {
            if((e.hash == hash) && e.key.equals(key) && e.value.equals(value)) {
                if(prev != null)
                    prev.next = e.next;
                else
                    tab[index] = e.next;
                e.value = null;
                modCount++;
                count--;
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        Objects.requireNonNull(oldValue);
        Objects.requireNonNull(newValue);
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (; e != null; e = e.next) {
            if((e.hash == hash) && e.key.equals(key)) {
                if(e.value.equals(oldValue)) {
                    e.value = newValue;
                    return true;
                }
                else
                    return false;
            }
        }
        return false;
    }

    @Override
    public synchronized V replace(K key, V value) {
        Objects.requireNonNull(value);
        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (; e != null; e = e.next) {
            if((e.hash == hash) && e.key.equals(key)) {
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        return null;
    }

    @Override
    public synchronized V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (; e != null; e = e.next) {
            if(e.hash == hash && e.key.equals(key))
                return e.value;
        }

        int mc = modCount;
        V newValue = mappingFunction.apply(key);
        if(mc != modCount) { throw new ConcurrentModificationException(); }
        if(newValue != null)
            addEntry(hash, key, newValue, index);

        return newValue;
    }

    @Override
    public synchronized V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (Entry<K,V> prev = null; e != null; prev = e, e = e.next) {
            if(e.hash == hash && e.key.equals(key)) {
                int mc = modCount;
                V newValue = remappingFunction.apply(key, e.value);
                if(mc != modCount)
                    throw new ConcurrentModificationException();
                if(newValue == null) {
                    if(prev != null)
                        prev.next = e.next;
                    else
                        tab[index] = e.next;
                    modCount = mc + 1;
                    count--;
                }
                else
                    e.value = newValue;
                return newValue;
            }
        }
        return null;
    }

    @Override
    public synchronized V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (Entry<K,V> prev = null; e != null; prev = e, e = e.next) {
            if(e.hash == hash && Objects.equals(e.key, key)) {
                int mc = modCount;
                V newValue = remappingFunction.apply(key, e.value);
                if(mc != modCount) {
                    throw new ConcurrentModificationException();
                }
                if(newValue == null) {
                    if(prev != null) {
                        prev.next = e.next;
                    } else {
                        tab[index] = e.next;
                    }
                    modCount = mc + 1;
                    count--;
                } else {
                    e.value = newValue;
                }
                return newValue;
            }
        }

        int mc = modCount;
        V newValue = remappingFunction.apply(key, null);
        if(mc != modCount)
            throw new ConcurrentModificationException();
        if(newValue != null)
            addEntry(hash, key, newValue, index);

        return newValue;
    }

    @Override
    public synchronized V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);

        Entry<?,?> tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        for (Entry<K,V> prev = null; e != null; prev = e, e = e.next) {
            if(e.hash == hash && e.key.equals(key)) {
                int mc = modCount;
                V newValue = remappingFunction.apply(e.value, value);
                if(mc != modCount)
                    throw new ConcurrentModificationException();
                if(newValue == null) {
                    if(prev != null)
                        prev.next = e.next;
                    else
                        tab[index] = e.next;
                    modCount = mc + 1;
                    count--;
                }
                else
                    e.value = newValue;
                return newValue;
            }
        }

        if(value != null)
            addEntry(hash, key, value, index);

        return value;
    }

    // TODO
    // @java.io.Serial
    // private void writeObject(java.io.ObjectOutputStream s) throws IOException {
    //     writeHashtable(s);
    // }

    // TODO
    // void writeHashtable(java.io.ObjectOutputStream s) throws IOException {
    //     Entry<Object, Object> entryStack = null;

    //     synchronized (this) {
    //         s.defaultWriteObject();

    //         s.writeInt(table.length);
    //         s.writeInt(count);

    //         for (Entry<?, ?> entry : table) {

    //             while (entry != null) {
    //                 entryStack =
    //                     new Entry<>(0, entry.key, entry.value, entryStack);
    //                 entry = entry.next;
    //             }
    //         }
    //     }

    //     while (entryStack != null) {
    //         s.writeObject(entryStack.key);
    //         s.writeObject(entryStack.value);
    //         entryStack = entryStack.next;
    //     }
    // }

    // TODO
    // final void defaultWriteHashtable(java.io.ObjectOutputStream s, int length, float loadFactor) throws IOException {
    //     this.threshold = (int)Math.min(length * loadFactor, MAX_ARRAY_SIZE + 1);
    //     this.loadFactor = loadFactor;
    //     s.defaultWriteObject();
    // }

    // TODO
    // @java.io.Serial
    // private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    //     readHashtable(s);
    // }

    // TODO
    // void readHashtable(ObjectInputStream s) throws IOException, ClassNotFoundException {
    //     ObjectInputStream.GetField fields = s.readFields();

    //     float lf = fields.get("loadFactor", 0.75f);
    //     if(lf <= 0 || Float.isNaN(lf))
    //         throw new StreamCorruptedException("Illegal load factor: " + lf);
    //     lf = Math.clamp(lf, 0.25f, 4.0f);

    //     int origlength = s.readInt();
    //     int elements = s.readInt();

    //     if(elements < 0)
    //         throw new StreamCorruptedException("Illegal # of Elements: " + elements);

    //     origlength = Math.max(origlength, (int)(elements / lf) + 1);

    //     int length = (int)(elements * 1.05f / lf) + 3;
    //     if(length > elements && (length & 1) == 0)
    //         length--;
    //     length = Math.min(length, origlength);

    //     if(length < 0)
    //         length = origlength;

    //     SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Map.Entry[].class, length);
    //     Hashtable.UnsafeHolder.putLoadFactor(this, lf);
    //     table = new Entry<?,?>[length];
    //     threshold = (int)Math.min(length * lf, MAX_ARRAY_SIZE + 1);
    //     count = 0;

    //     for (; elements > 0; elements--) {
    //         @SuppressWarnings("unchecked")
    //         K key = (K)s.readObject();
    //         @SuppressWarnings("unchecked")
    //         V value = (V)s.readObject();
    //         reconstitutionPut(table, key, value);
    //     }
    // }

    // TODO
    // private static final class UnsafeHolder {
    //     private UnsafeHolder() {
    //         throw new InternalError();
    //     }

    //     private static final jdk.internal.misc.Unsafe unsafe = jdk.internal.misc.Unsafe.getUnsafe();
    //     private static final long LF_OFFSET = unsafe.objectFieldOffset(Hashtable.class, "loadFactor");

    //     static void putLoadFactor(Hashtable<?, ?> table, float lf) {
    //         unsafe.putFloat(table, LF_OFFSET, lf);
    //     }
    // }

    private void reconstitutionPut(Entry<?,?>[] tab, K key, V value) throws StreamCorruptedException {
        if(value == null)
            throw new StreamCorruptedException();

        int hash = key.hashCode();
        int index = (hash & 0x7FFFFFFF) % tab.length;
        for (Entry<?,?> e = tab[index] ; e != null ; e = e.next) {
            if((e.hash == hash) && e.key.equals(key))
                throw new StreamCorruptedException();
        }
        @SuppressWarnings("unchecked")
        Entry<K,V> e = (Entry<K,V>)tab[index];
        tab[index] = new Entry<>(hash, key, value, e);
        count++;
    }

    private static class Entry<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Entry<K,V> next;

        protected Entry(int hash, K key, V value, Entry<K,V> next) {
            this.hash = hash;
            this.key =  key;
            this.value = value;
            this.next = next;
        }

        @SuppressWarnings("unchecked")
        protected Object clone() {
            return new Entry<>(hash, key, value, (next == null ? null : (Entry<K,V>) next.clone()));
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            if(value == null)
                throw new NullPointerException();

            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if(!(o instanceof Map.Entry<?, ?> e))
                return false;

            return (key == null ? e.getKey() == null : key.equals(e.getKey())) && (value == null ? e.getValue() == null : value.equals(e.getValue()));
        }

        public int hashCode() {
            return hash ^ Objects.hashCode(value);
        }

        public String toString() {
            return key.toString()+"="+value.toString();
        }
    }

    private static final int KEYS = 0;
    private static final int VALUES = 1;
    private static final int ENTRIES = 2;

    private class Enumerator<T> implements Enumeration<T>, Iterator<T> {
        final Entry<?,?>[] table = Hashtable.this.table;
        int index = table.length;
        Entry<?,?> entry;
        Entry<?,?> lastReturned;
        final int type;

        final boolean iterator;

        protected int expectedModCount = Hashtable.this.modCount;

        Enumerator(int type, boolean iterator) {
            this.type = type;
            this.iterator = iterator;
        }

        public boolean hasMoreElements() {
            Entry<?,?> e = entry;
            int i = index;
            Entry<?,?>[] t = table;
            while (e == null && i > 0)
                e = t[--i];
            entry = e;
            index = i;
            return e != null;
        }

        @SuppressWarnings("unchecked")
        public T nextElement() {
            Entry<?,?> et = entry;
            int i = index;
            Entry<?,?>[] t = table;
            while (et == null && i > 0)
                et = t[--i];
            entry = et;
            index = i;
            if(et != null) {
                Entry<?,?> e = lastReturned = entry;
                entry = e.next;
                return type == KEYS ? (T)e.key : (type == VALUES ? (T)e.value : (T)e);
            }
            throw new NoSuchElementException("Hashtable Enumerator");
        }

        public boolean hasNext() {
            return hasMoreElements();
        }

        public T next() {
            if(Hashtable.this.modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return nextElement();
        }

        public void remove() {
            if(!iterator)
                throw new UnsupportedOperationException();
            if(lastReturned == null)
                throw new IllegalStateException("Hashtable Enumerator");
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();

            synchronized(Hashtable.this) {
                Entry<?,?>[] tab = Hashtable.this.table;
                int index = (lastReturned.hash & 0x7FFFFFFF) % tab.length;

                @SuppressWarnings("unchecked")
                Entry<K,V> e = (Entry<K,V>)tab[index];
                for(Entry<K,V> prev = null; e != null; prev = e, e = e.next) {
                    if(e == lastReturned) {
                        if(prev == null)
                            tab[index] = e.next;
                        else
                            prev.next = e.next;
                        expectedModCount++;
                        lastReturned = null;
                        Hashtable.this.modCount++;
                        Hashtable.this.count--;
                        return;
                    }
                }
                throw new ConcurrentModificationException();
            }
        }
    }
}
