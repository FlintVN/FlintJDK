package java.util;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
// import java.util.stream.Stream;
// import java.util.stream.StreamSupport;
import jdk.internal.util.ArraysSupport;

class ReverseOrderListView<E> implements List<E> {

    final List<E> base;
    final boolean modifiable;

    public static <T> List<T> of(List<T> list, boolean modifiable) {
        if (list instanceof ReverseOrderListView<T> rolv)
            return rolv.base;
        else if (list instanceof RandomAccess)
            return new ReverseOrderListView.Rand<>(list, modifiable);
        else
            return new ReverseOrderListView<>(list, modifiable);
    }

    static class Rand<E> extends ReverseOrderListView<E> implements RandomAccess {
        Rand(List<E> list, boolean modifiable) {
            super(list, modifiable);
        }
    }

    private ReverseOrderListView(List<E> list, boolean modifiable) {
        this.base = list;
        this.modifiable = modifiable;
    }

    void checkModifiable() {
        if (! modifiable) {
            throw new UnsupportedOperationException();
        }
    }

    class DescendingIterator implements Iterator<E> {
        final ListIterator<E> it = base.listIterator(base.size());
        public boolean hasNext() { return it.hasPrevious(); }
        public E next() { return it.previous(); }
        public void remove() {
            checkModifiable();
            it.remove();
            // TODO - make sure ListIterator is positioned correctly afterward
        }
    }

    class DescendingListIterator implements ListIterator<E> {
        final ListIterator<E> it;

        DescendingListIterator(int size, int pos) {
            if (pos < 0 || pos > size)
                throw new IndexOutOfBoundsException();
            it = base.listIterator(size - pos);
        }

        public boolean hasNext() {
            return it.hasPrevious();
        }

        public E next() {
            return it.previous();
        }

        public boolean hasPrevious() {
            return it.hasNext();
        }

        public E previous() {
            return it.next();
        }

        public int nextIndex() {
            return base.size() - it.nextIndex();
        }

        public int previousIndex() {
            return nextIndex() - 1;
        }

        public void remove() {
            checkModifiable();
            it.remove();
        }

        public void set(E e) {
            checkModifiable();
            it.set(e);
        }

        public void add(E e) {
            checkModifiable();
            it.add(e);
            it.previous();
        }
    }

    public void forEach(Consumer<? super E> action) {
        for (E e : this)
            action.accept(e);
    }

    public Iterator<E> iterator() {
        return new DescendingIterator();
    }

    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }

    public boolean add(E e) {
        checkModifiable();
        base.add(0, e);
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        checkModifiable();

        @SuppressWarnings("unchecked")
        E[] adds = (E[])c.toArray();
        if (adds.length == 0)
            return false;
        else {
            base.addAll(0, Arrays.asList(ArraysSupport.reverse(adds)));
            return true;
        }
    }

    public void clear() {
        checkModifiable();
        base.clear();
    }

    public boolean contains(Object o) {
        return base.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return base.containsAll(c);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof List))
            return false;

        ListIterator<E> e1 = listIterator();
        ListIterator<?> e2 = ((List<?>)o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    public int hashCode() {
        int hashCode = 1;
        for (E e : this)
            hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
        return hashCode;
    }

    public boolean isEmpty() {
        return base.isEmpty();
    }

    public boolean remove(Object o) {
        checkModifiable();
        Iterator<E> it = iterator();
        if (o==null) {
            while (it.hasNext()) {
                if (it.next()==null) {
                    it.remove();
                    return true;
                }
            }
        }
        else {
            while (it.hasNext()) {
                if (o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        checkModifiable();
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<?> it = iterator();
        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(Collection<?> c) {
        checkModifiable();
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public int size() {
        return base.size();
    }

    // TODO
    // public Stream<E> stream() {
    //     return StreamSupport.stream(spliterator(), false);
    // }

    // TODO
    // public Stream<E> parallelStream() {
    //     return StreamSupport.stream(spliterator(), true);
    // }

    public Object[] toArray() {
        return ArraysSupport.reverse(base.toArray());
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        return ArraysSupport.toArrayReversed(base, a);
    }

    public <T> T[] toArray(IntFunction<T[]> generator) {
        return ArraysSupport.reverse(base.toArray(generator));
    }

    public String toString() {
        Iterator<E> it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

    public void add(int index, E element) {
        checkModifiable();
        int size = base.size();
        checkClosedRange(index, size);
        base.add(size - index, element);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        checkModifiable();
        int size = base.size();
        checkClosedRange(index, size);
        @SuppressWarnings("unchecked")
        E[] adds = (E[])c.toArray();
        if (adds.length == 0)
            return false;
        else {
            base.addAll(size - index, Arrays.asList(ArraysSupport.reverse(adds)));
            return true;
        }
    }

    public E get(int i) {
        int size = base.size();
        Objects.checkIndex(i, size);
        return base.get(size - i - 1);
    }

    public int indexOf(Object o) {
        int i = base.lastIndexOf(o);
        return i == -1 ? -1 : base.size() - i - 1;
    }

    public int lastIndexOf(Object o) {
        int i = base.indexOf(o);
        return i == -1 ? -1 : base.size() - i - 1;
    }

    public ListIterator<E> listIterator() {
        return new DescendingListIterator(base.size(), 0);
    }

    public ListIterator<E> listIterator(int index) {
        int size = base.size();
        checkClosedRange(index, size);
        return new DescendingListIterator(size, index);
    }

    public E remove(int index) {
        checkModifiable();
        int size = base.size();
        Objects.checkIndex(index, size);
        return base.remove(size - index - 1);
    }

    public boolean removeIf(Predicate<? super E> filter) {
        checkModifiable();
        return base.removeIf(filter);
    }

    public void replaceAll(UnaryOperator<E> operator) {
        checkModifiable();
        base.replaceAll(operator);
    }

    public void sort(Comparator<? super E> c) {
        // TODO
        // checkModifiable();
        // base.sort(Collections.reverseOrder(c));
        throw new UnsupportedOperationException();
    }

    public E set(int index, E element) {
        checkModifiable();
        int size = base.size();
        Objects.checkIndex(index, size);
        return base.set(size - index - 1, element);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        int size = base.size();
        Objects.checkFromToIndex(fromIndex, toIndex, size);
        return new ReverseOrderListView<>(base.subList(size - toIndex, size - fromIndex), modifiable);
    }

    static void checkClosedRange(int index, int size) {
        if(index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
}
