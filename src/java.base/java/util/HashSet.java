package java.util;

// import java.io.InvalidObjectException;

public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable {
    transient HashMap<E,Object> map;

    static final Object PRESENT = new Object();

    public HashSet() {
        map = new HashMap<>();
    }

    @SuppressWarnings("this-escape")
    public HashSet(Collection<? extends E> c) {
        map = HashMap.newHashMap(Math.max(c.size(), 12));
        addAll(c);
    }

    public HashSet(int initialCapacity, float loadFactor) {
        map = new HashMap<>(initialCapacity, loadFactor);
    }

    public HashSet(int initialCapacity) {
        map = new HashMap<>(initialCapacity);
    }

    HashSet(int initialCapacity, float loadFactor, boolean dummy) {
        map = new LinkedHashMap<>(initialCapacity, loadFactor);
    }

    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }

    public boolean remove(Object o) {
        return map.remove(o)==PRESENT;
    }

    public void clear() {
        map.clear();
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            HashSet<E> newSet = (HashSet<E>) super.clone();
            newSet.map = (HashMap<E, Object>) map.clone();
            return newSet;
        }
        catch(CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    // TODO
    // @java.io.Serial
    // private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
    //     s.defaultWriteObject();

    //     s.writeInt(map.capacity());
    //     s.writeFloat(map.loadFactor());

    //     s.writeInt(map.size());

    //     for(E e : map.keySet())
    //         s.writeObject(e);
    // }

    // TODO
    // @java.io.Serial
    // private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
    //     s.readFields();

    //     int capacity = s.readInt();
    //     if(capacity < 0)
    //         throw new InvalidObjectException("Illegal capacity: " + capacity);

    //     float loadFactor = s.readFloat();
    //     if(loadFactor <= 0 || Float.isNaN(loadFactor))
    //         throw new InvalidObjectException("Illegal load factor: " + loadFactor);
    //     loadFactor = Math.clamp(loadFactor, 0.25f, 4.0f);

    //     int size = s.readInt();
    //     if(size < 0)
    //         throw new InvalidObjectException("Illegal size: " + size);

    //     capacity = (int) Math.min(size * Math.min(1 / loadFactor, 4.0f), HashMap.MAXIMUM_CAPACITY);

    //     SharedSecrets.getJavaObjectInputStreamAccess().checkArray(s, Map.Entry[].class, HashMap.tableSizeFor(capacity));

    //     map = (this instanceof LinkedHashSet ? new LinkedHashMap<>(capacity, loadFactor) : new HashMap<>(capacity, loadFactor));

    //     for(int i=0; i<size; i++) {
    //         @SuppressWarnings("unchecked")
    //         E e = (E) s.readObject();
    //         map.put(e, PRESENT);
    //     }
    // }

    public Spliterator<E> spliterator() {
        return new HashMap.KeySpliterator<>(map, 0, -1, 0, 0);
    }

    @Override
    public Object[] toArray() {
        return map.keysToArray(new Object[map.size()]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.keysToArray(map.prepareArray(a));
    }

    public static <T> HashSet<T> newHashSet(int numElements) {
        if(numElements < 0)
            throw new IllegalArgumentException("Negative number of elements: " + numElements);
        return new HashSet<>(HashMap.calculateHashMapCapacity(numElements));
    }
}
