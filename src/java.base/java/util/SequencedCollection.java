package java.util;

public interface SequencedCollection<E> extends Collection<E> {
    SequencedCollection<E> reversed();

    default void addFirst(E e) {
        throw new UnsupportedOperationException();
    }

    default void addLast(E e) {
        throw new UnsupportedOperationException();
    }

    default E getFirst() {
        return this.iterator().next();
    }

    default E getLast() {
        return this.reversed().iterator().next();
    }

    default E removeFirst() {
        var it = this.iterator();
        E e = it.next();
        it.remove();
        return e;
    }

    default E removeLast() {
        var it = this.reversed().iterator();
        E e = it.next();
        it.remove();
        return e;
    }
}
