package java.util;

public interface SortedSet<E> extends Set<E>, SequencedSet<E> {
    Comparator<? super E> comparator();

    SortedSet<E> subSet(E fromElement, E toElement);

    SortedSet<E> headSet(E toElement);

    SortedSet<E> tailSet(E fromElement);

    E first();

    E last();

    @Override
    default Spliterator<E> spliterator() {
        return new Spliterators.IteratorSpliterator<E>(
                this, Spliterator.DISTINCT | Spliterator.SORTED | Spliterator.ORDERED) {
            @Override
            public Comparator<? super E> getComparator() {
                return SortedSet.this.comparator();
            }
        };
    }

    default void addFirst(E e) {
        throw new UnsupportedOperationException();
    }

    default void addLast(E e) {
        throw new UnsupportedOperationException();
    }

    default E getFirst() {
        return this.first();
    }

    default E getLast() {
        return this.last();
    }

    default E removeFirst() {
        E e = this.first();
        this.remove(e);
        return e;
    }

    default E removeLast() {
        E e = this.last();
        this.remove(e);
        return e;
    }

    default SortedSet<E> reversed() {
        return ReverseOrderSortedSetView.of(this);
    }
}
