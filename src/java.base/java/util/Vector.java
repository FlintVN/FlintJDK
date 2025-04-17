package java.util;

import java.io.IOException;
// import java.io.ObjectInputStream;
// import java.io.StreamCorruptedException;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import jdk.internal.util.ArraysSupport;

public class Vector<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable {
    @SuppressWarnings("serial")
    protected Object[] elementData;
    protected int elementCount;
    protected int capacityIncrement;

    public Vector(int initialCapacity, int capacityIncrement) {
        super();
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        this.elementData = new Object[initialCapacity];
        this.capacityIncrement = capacityIncrement;
    }

    public Vector(int initialCapacity) {
        this(initialCapacity, 0);
    }

    public Vector() {
        this(10);
    }

    public Vector(Collection<? extends E> c) {
        Object[] a = c.toArray();
        elementCount = a.length;
        if(c.getClass() == ArrayList.class)
            elementData = a;
        else
            elementData = Arrays.copyOf(a, elementCount, Object[].class);
    }

    public synchronized void copyInto(Object[] anArray) {
        System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }

    public synchronized void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if(elementCount < oldCapacity)
            elementData = Arrays.copyOf(elementData, elementCount);
    }

    public synchronized void ensureCapacity(int minCapacity) {
        if(minCapacity > 0) {
            modCount++;
            if(minCapacity > elementData.length)
                grow(minCapacity);
        }
    }

    private Object[] grow(int minCapacity) {
        int oldCapacity = elementData.length;
        int newCapacity = ArraysSupport.newLength(oldCapacity, minCapacity - oldCapacity, capacityIncrement > 0 ? capacityIncrement : oldCapacity);
        return elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private Object[] grow() {
        return grow(elementCount + 1);
    }

    public synchronized void setSize(int newSize) {
        modCount++;
        if(newSize > elementData.length)
            grow(newSize);
        final Object[] es = elementData;
        for(int to = elementCount, i = newSize; i < to; i++)
            es[i] = null;
        elementCount = newSize;
    }

    public synchronized int capacity() {
        return elementData.length;
    }

    public synchronized int size() {
        return elementCount;
    }

    public synchronized boolean isEmpty() {
        return elementCount == 0;
    }

    public Enumeration<E> elements() {
        return new Enumeration<E>() {
            int count = 0;

            public boolean hasMoreElements() {
                return count < elementCount;
            }

            public E nextElement() {
                synchronized(Vector.this) {
                    if(count < elementCount)
                        return elementData(count++);
                }
                throw new NoSuchElementException("Vector Enumeration");
            }
        };
    }

    public boolean contains(Object o) {
        return indexOf(o, 0) >= 0;
    }

    public int indexOf(Object o) {
        return indexOf(o, 0);
    }

    public synchronized int indexOf(Object o, int index) {
        if(o == null) {
            for(int i = index ; i < elementCount ; i++)
                if(elementData[i] == null)
                    return i;
        }
        else {
            for(int i = index ; i < elementCount ; i++)
                if(o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public synchronized int lastIndexOf(Object o) {
        return lastIndexOf(o, elementCount-1);
    }

    public synchronized int lastIndexOf(Object o, int index) {
        if(index >= elementCount)
            throw new IndexOutOfBoundsException(index + " >= "+ elementCount);

        if(o == null) {
            for(int i = index; i >= 0; i--)
                if(elementData[i] == null)
                    return i;
        }
        else {
            for(int i = index; i >= 0; i--)
                if(o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    public synchronized E elementAt(int index) {
        if(index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        return elementData(index);
    }

    public synchronized E firstElement() {
        if(elementCount == 0)
            throw new NoSuchElementException();
        return elementData(0);
    }

    public synchronized E lastElement() {
        if(elementCount == 0)
            throw new NoSuchElementException();
        return elementData(elementCount - 1);
    }

    public synchronized void setElementAt(E obj, int index) {
        if(index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        elementData[index] = obj;
    }

    public synchronized void removeElementAt(int index) {
        if(index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
        else if(index < 0)
            throw new ArrayIndexOutOfBoundsException(index);
        int j = elementCount - index - 1;
        if(j > 0)
            System.arraycopy(elementData, index + 1, elementData, index, j);
        modCount++;
        elementCount--;
        elementData[elementCount] = null;
    }

    public synchronized void insertElementAt(E obj, int index) {
        if(index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index + " > " + elementCount);
        modCount++;
        final int s = elementCount;
        Object[] elementData = this.elementData;
        if(s == elementData.length)
            elementData = grow();
        System.arraycopy(elementData, index, elementData, index + 1, s - index);
        elementData[index] = obj;
        elementCount = s + 1;
    }

    public synchronized void addElement(E obj) {
        modCount++;
        add(obj, elementData, elementCount);
    }

    public synchronized boolean removeElement(Object obj) {
        modCount++;
        int i = indexOf(obj);
        if(i >= 0) {
            removeElementAt(i);
            return true;
        }
        return false;
    }

    public synchronized void removeAllElements() {
        final Object[] es = elementData;
        for(int to = elementCount, i = elementCount = 0; i < to; i++)
            es[i] = null;
        modCount++;
    }

    public synchronized Object clone() {
        try {
            @SuppressWarnings("unchecked")
            Vector<E> v = (Vector<E>)super.clone();
            v.elementData = Arrays.copyOf(elementData, elementCount);
            v.modCount = 0;
            return v;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public synchronized Object[] toArray() {
        return Arrays.copyOf(elementData, elementCount);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> T[] toArray(T[] a) {
        if(a.length < elementCount)
            return (T[])Arrays.copyOf(elementData, elementCount, a.getClass());
        System.arraycopy(elementData, 0, a, 0, elementCount);
        if(a.length > elementCount)
            a[elementCount] = null;
        return a;
    }

    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E)elementData[index];
    }

    @SuppressWarnings("unchecked")
    static <E> E elementAt(Object[] es, int index) {
        return (E)es[index];
    }

    public synchronized E get(int index) {
        if(index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        return elementData(index);
    }

    public synchronized E set(int index, E element) {
        if(index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        E oldValue = elementData(index);
        elementData[index] = element;
        return oldValue;
    }

    private void add(E e, Object[] elementData, int s) {
        if(s == elementData.length)
            elementData = grow();
        elementData[s] = e;
        elementCount = s + 1;
    }

    public synchronized boolean add(E e) {
        modCount++;
        add(e, elementData, elementCount);
        return true;
    }

    public boolean remove(Object o) {
        return removeElement(o);
    }

    public void add(int index, E element) {
        insertElementAt(element, index);
    }

    public synchronized E remove(int index) {
        modCount++;
        if(index >= elementCount)
            throw new ArrayIndexOutOfBoundsException(index);
        E oldValue = elementData(index);

        int numMoved = elementCount - index - 1;
        if(numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index, numMoved);
        elementData[--elementCount] = null;

        return oldValue;
    }

    public void clear() {
        removeAllElements();
    }

    public synchronized boolean containsAll(Collection<?> c) {
        return super.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if(numNew == 0)
            return false;
        synchronized(this) {
            Object[] elementData = this.elementData;
            final int s = elementCount;
            if(numNew > elementData.length - s)
                elementData = grow(s + numNew);
            System.arraycopy(a, 0, elementData, s, numNew);
            elementCount = s + numNew;
            return true;
        }
    }

    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(new Predicate<Object>() {
            @Override
            public boolean test(Object e) {
                return c.contains(e);
            }
        });
    }

    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return bulkRemove(new Predicate<Object>() {
            @Override
            public boolean test(Object e) {
                return !c.contains(e);
            }
        });
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        return bulkRemove(filter);
    }

    private static long[] nBits(int n) {
        return new long[((n - 1) >> 6) + 1];
    }

    private static void setBit(long[] bits, int i) {
        bits[i >> 6] |= 1L << i;
    }

    private static boolean isClear(long[] bits, int i) {
        return (bits[i >> 6] & (1L << i)) == 0;
    }

    private synchronized boolean bulkRemove(Predicate<? super E> filter) {
        int expectedModCount = modCount;
        final Object[] es = elementData;
        final int end = elementCount;
        int i;
        for(i = 0; i < end && !filter.test(elementAt(es, i)); i++);
        if(i < end) {
            final int beg = i;
            final long[] deathRow = nBits(end - beg);
            deathRow[0] = 1L;
            for(i = beg + 1; i < end; i++)
                if(filter.test(elementAt(es, i)))
                    setBit(deathRow, i - beg);
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
            modCount++;
            int w = beg;
            for(i = beg; i < end; i++)
                if(isClear(deathRow, i - beg))
                    es[w++] = es[i];
            for(i = elementCount = w; i < end; i++)
                es[i] = null;
            return true;
        }
        else {
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
            return false;
        }
    }

    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        if(index < 0 || index > elementCount)
            throw new ArrayIndexOutOfBoundsException(index);

        Object[] a = c.toArray();
        modCount++;
        int numNew = a.length;
        if(numNew == 0)
            return false;
        Object[] elementData = this.elementData;
        final int s = elementCount;
        if(numNew > elementData.length - s)
            elementData = grow(s + numNew);

        int numMoved = s - index;
        if(numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        System.arraycopy(a, 0, elementData, index, numNew);
        elementCount = s + numNew;
        return true;
    }

    public synchronized boolean equals(Object o) {
        return super.equals(o);
    }

    public synchronized int hashCode() {
        return super.hashCode();
    }

    public synchronized String toString() {
        return super.toString();
    }

    public synchronized List<E> subList(int fromIndex, int toIndex) {
        return Collections.synchronizedList(super.subList(fromIndex, toIndex), this);
    }

    protected synchronized void removeRange(int fromIndex, int toIndex) {
        modCount++;
        shiftTailOverGap(elementData, fromIndex, toIndex);
    }

    private void shiftTailOverGap(Object[] es, int lo, int hi) {
        System.arraycopy(es, hi, es, lo, elementCount - hi);
        for(int to = elementCount, i = (elementCount -= hi - lo); i < to; i++)
            es[i] = null;
    }

    public synchronized ListIterator<E> listIterator(int index) {
        if(index < 0 || index > elementCount)
            throw new IndexOutOfBoundsException("Index: "+index);
        return new ListItr(index);
    }

    public synchronized ListIterator<E> listIterator() {
        return new ListItr(0);
    }

    public synchronized Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        int cursor;
        int lastRet = -1;
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != elementCount;
        }

        public E next() {
            synchronized(Vector.this) {
                checkForComodification();
                int i = cursor;
                if(i >= elementCount)
                    throw new NoSuchElementException();
                cursor = i + 1;
                return elementData(lastRet = i);
            }
        }

        public void remove() {
            if(lastRet == -1)
                throw new IllegalStateException();
            synchronized(Vector.this) {
                checkForComodification();
                Vector.this.remove(lastRet);
                expectedModCount = modCount;
            }
            cursor = lastRet;
            lastRet = -1;
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            synchronized(Vector.this) {
                final int size = elementCount;
                int i = cursor;
                if(i >= size)
                    return;
                final Object[] es = elementData;
                if(i >= es.length)
                    throw new ConcurrentModificationException();
                while(i < size && modCount == expectedModCount)
                    action.accept(elementAt(es, i++));
                cursor = i;
                lastRet = i - 1;
                checkForComodification();
            }
        }

        final void checkForComodification() {
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    final class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public E previous() {
            synchronized(Vector.this) {
                checkForComodification();
                int i = cursor - 1;
                if(i < 0)
                    throw new NoSuchElementException();
                cursor = i;
                return elementData(lastRet = i);
            }
        }

        public void set(E e) {
            if(lastRet == -1)
                throw new IllegalStateException();
            synchronized(Vector.this) {
                checkForComodification();
                Vector.this.set(lastRet, e);
            }
        }

        public void add(E e) {
            int i = cursor;
            synchronized(Vector.this) {
                checkForComodification();
                Vector.this.add(i, e);
                expectedModCount = modCount;
            }
            cursor = i + 1;
            lastRet = -1;
        }
    }

    @Override
    public synchronized void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = elementCount;
        for(int i = 0; modCount == expectedModCount && i < size; i++)
            action.accept(elementAt(es, i));
        if(modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }

    @Override
    public synchronized void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final Object[] es = elementData;
        final int size = elementCount;
        for(int i = 0; modCount == expectedModCount && i < size; i++)
            es[i] = operator.apply(elementAt(es, i));
        if(modCount != expectedModCount)
            throw new ConcurrentModificationException();
        // TODO(8203662): remove increment of modCount from ...
        modCount++;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[])elementData, 0, elementCount, c);
        if(modCount != expectedModCount)
            throw new ConcurrentModificationException();
        modCount++;
    }

    @Override
    public Spliterator<E> spliterator() {
        return new VectorSpliterator(null, 0, -1, 0);
    }

    final class VectorSpliterator implements Spliterator<E> {
        private Object[] array;
        private int index;
        private int fence;
        private int expectedModCount;

        VectorSpliterator(Object[] array, int origin, int fence, int expectedModCount) {
            this.array = array;
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() {
            int hi;
            if((hi = fence) < 0) {
                synchronized(Vector.this) {
                    array = elementData;
                    expectedModCount = modCount;
                    hi = fence = elementCount;
                }
            }
            return hi;
        }

        public Spliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null :
                new VectorSpliterator(array, lo, index = mid, expectedModCount);
        }

        @SuppressWarnings("unchecked")
        public boolean tryAdvance(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            int i;
            if(getFence() > (i = index)) {
                index = i + 1;
                action.accept((E)array[i]);
                if(modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            final int hi = getFence();
            final Object[] a = array;
            int i;
            for(i = index, index = hi; i < hi; i++)
                action.accept((E)a[i]);
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return getFence() - index;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }
}
