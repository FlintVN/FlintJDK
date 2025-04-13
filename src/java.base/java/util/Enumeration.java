package java.util;

public interface Enumeration<E> {
    boolean hasMoreElements();

    E nextElement();

    default Iterator<E> asIterator() {
        return new Iterator<>() {
            @Override public boolean hasNext() {
                return hasMoreElements();
            }
            @Override public E next() {
                return nextElement();
            }
        };
    }
}
