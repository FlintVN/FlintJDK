package java.util;

import java.util.function.Consumer;
import java.util.function.IntFunction;
// import java.util.stream.Stream;
// import java.util.stream.StreamSupport;
import jdk.internal.util.ArraysSupport;

class ReverseOrderSortedSetView<E> implements SortedSet<E> {
    final SortedSet<E> base;
    final Comparator<? super E> comp;

    private ReverseOrderSortedSetView(SortedSet<E> set) {
        base = set;
        comp = Collections.reverseOrder(set.comparator());
    }

    public static <T> SortedSet<T> of(SortedSet<T> set) {
        if(set instanceof ReverseOrderSortedSetView<T> rossv)
            return rossv.base;
        else
            return new ReverseOrderSortedSetView<>(set);
    }

    public boolean equals(Object o) {
        if(o == this)
            return true;

        if(!(o instanceof Set))
            return false;
        Collection<?> c = (Collection<?>) o;
        if(c.size() != size())
            return false;
        try {
            return containsAll(c);
        }
        catch (ClassCastException | NullPointerException unused) {
            return false;
        }
    }

    public int hashCode() {
        int h = 0;
        Iterator<E> i = iterator();
        while(i.hasNext()) {
            E obj = i.next();
            if(obj != null)
                h += obj.hashCode();
        }
        return h;
    }

    public String toString() {
        Iterator<E> it = iterator();
        if(! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for(;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if(! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }

    public void forEach(Consumer<? super E> action) {
        for(E e : this)
            action.accept(e);
    }

    public Iterator<E> iterator() {
        return descendingIterator(base);
    }

    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }

    public boolean add(E e) {
        base.add(e);
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        return base.addAll(c);
    }

    public void clear() {
        base.clear();
    }

    public boolean contains(Object o) {
        return base.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return base.containsAll(c);
    }

    public boolean isEmpty() {
        return base.isEmpty();
    }

    public boolean remove(Object o) {
        return base.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return base.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return base.retainAll(c);
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

    public Comparator<? super E> comparator() {
        return comp;
    }

    public E first() { return base.last(); }

    public E last() { return base.first(); }

    public SortedSet<E> headSet(E to) {
        return new Subset(null, to);
    }

    public SortedSet<E> subSet(E from, E to) {
        return new Subset(from, to);
    }

    public SortedSet<E> tailSet(E from) {
        return new Subset(from, null);
    }

    static <T> Iterator<T> descendingIterator(SortedSet<T> set) {
        return new Iterator<>() {
            SortedSet<T> root = set;
            SortedSet<T> view = set;
            T prev = null;

            public boolean hasNext() {
                return ! view.isEmpty();
            }

            public T next() {
                if(view.isEmpty())
                    throw new NoSuchElementException();
                T t = prev = view.last();
                view = root.headSet(t);
                return t;
            }

            public void remove() {
                if(prev == null)
                    throw new IllegalStateException();
                else {
                    root.remove(prev);
                    prev = null;
                }
            }
        };
    }

    class Subset extends AbstractSet<E> implements SortedSet<E> {
        final E head;
        final E tail;
        final Comparator<E> cmp;

        @SuppressWarnings("unchecked")
        Subset(E head, E tail) {
            this.head = head;
            this.tail = tail;
            Comparator<E> c = (Comparator<E>) ReverseOrderSortedSetView.this.comparator();
            if(c == null)
                c = (Comparator<E>) Comparator.naturalOrder();
            cmp = c;
        }

        boolean aboveHead(E e) {
            return head == null || cmp.compare(e, head) >= 0;
        }

        boolean belowTail(E e) {
            return tail == null || cmp.compare(e, tail) < 0;
        }

        public Iterator<E> iterator() {
            return new Iterator<>() {
                E cache = null;
                boolean dead = false;
                Iterator<E> it = descendingIterator(base);

                public boolean hasNext() {
                    if(dead)
                        return false;

                    if(cache != null)
                        return true;

                    while(it.hasNext()) {
                        E e = it.next();

                        if(! aboveHead(e))
                            continue;

                        if(! belowTail(e)) {
                            dead = true;
                            return false;
                        }

                        cache = e;
                        return true;
                    }

                    return false;
                }

                public E next() {
                    if(hasNext()) {
                        E e = cache;
                        cache = null;
                        return e;
                    }
                    else
                        throw new NoSuchElementException();
                }
            };
        }

        public boolean add(E e) {
            if(aboveHead(e) && belowTail(e))
                return base.add(e);
            else
                throw new IllegalArgumentException();
        }

        public boolean remove(Object o) {
            @SuppressWarnings("unchecked")
            E e = (E) o;
            if(aboveHead(e) && belowTail(e))
                return base.remove(o);
            else
                return false;
        }

        public int size() {
            int sz = 0;
            for(E e : this)
                sz++;
            return sz;
        }

        public Comparator<? super E> comparator() {
            return ReverseOrderSortedSetView.this.comparator();
        }

        public E first() {
            return this.iterator().next();
        }

        public E last() {
            var it = this.iterator();
            if(! it.hasNext())
                throw new NoSuchElementException();
            E last = it.next();
            while(it.hasNext())
                last = it.next();
            return last;
        }

        public SortedSet<E> subSet(E from, E to) {
            if(aboveHead(from) && belowTail(from) &&
                aboveHead(to) && belowTail(to) &&
                cmp.compare(from, to) <= 0) {
                return ReverseOrderSortedSetView.this.new Subset(from, to);
            }
            else
                throw new IllegalArgumentException();
        }

        public SortedSet<E> headSet(E to) {
            if(aboveHead(to) && belowTail(to))
                return ReverseOrderSortedSetView.this.new Subset(head, to);
            else
                throw new IllegalArgumentException();
        }

        public SortedSet<E> tailSet(E from) {
            if(aboveHead(from) && belowTail(from))
                return ReverseOrderSortedSetView.this.new Subset(null, tail);
            else
                throw new IllegalArgumentException();
        }

    }
}
