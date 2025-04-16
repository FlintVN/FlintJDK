
package java.util;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
// import java.util.stream.Stream;

public class LinkedList<E> extends AbstractSequentialList<E> implements List<E>, Deque<E>, Cloneable {
    transient int size = 0;

    transient Node<E> first;

    transient Node<E> last;

    public LinkedList() {

    }

    @SuppressWarnings("this-escape")
    public LinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if(f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }

    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if(l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    void linkBefore(E e, Node<E> succ) {
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if(pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }

    private E unlinkFirst(Node<E> f) {
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null;
        first = next;
        if(next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }

    private E unlinkLast(Node<E> l) {
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null;
        last = prev;
        if(prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    E unlink(Node<E> x) {
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if(prev == null) {
            first = next;
        }
        else {
            prev.next = next;
            x.prev = null;
        }

        if(next == null) {
            last = prev;
        }
        else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }

    public E getFirst() {
        final Node<E> f = first;
        if(f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    public E getLast() {
        final Node<E> l = last;
        if(l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    public E removeFirst() {
        final Node<E> f = first;
        if(f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    public E removeLast() {
        final Node<E> l = last;
        if(l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }

    public void addFirst(E e) {
        linkFirst(e);
    }

    public void addLast(E e) {
        linkLast(e);
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public int size() {
        return size;
    }

    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    public boolean remove(Object o) {
        if(o == null) {
            for(Node<E> x = first; x != null; x = x.next) {
                if(x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        }
        else {
            for(Node<E> x = first; x != null; x = x.next) {
                if(o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        if(numNew == 0)
            return false;

        Node<E> pred, succ;
        if(index == size) {
            succ = null;
            pred = last;
        }
        else {
            succ = node(index);
            pred = succ.prev;
        }

        for(Object o : a) {
            @SuppressWarnings("unchecked")
            E e = (E)o;
            Node<E> newNode = new Node<>(pred, e, null);
            if(pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if(succ == null) {
            last = pred;
        }
        else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }

    public void clear() {
        for(Node<E> x = first; x != null; ) {
            Node<E> next = x.next;
            x.item = null;
            x.next = null;
            x.prev = null;
            x = next;
        }
        first = last = null;
        size = 0;
        modCount++;
    }

    public E get(int index) {
        checkElementIndex(index);
        return node(index).item;
    }

    public E set(int index, E element) {
        checkElementIndex(index);
        Node<E> x = node(index);
        E oldVal = x.item;
        x.item = element;
        return oldVal;
    }

    public void add(int index, E element) {
        checkPositionIndex(index);

        if(index == size)
            linkLast(element);
        else
            linkBefore(element, node(index));
    }

    public E remove(int index) {
        checkElementIndex(index);
        return unlink(node(index));
    }

    private boolean isElementIndex(int index) {
        return index >= 0 && index < size;
    }

    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    private void checkElementIndex(int index) {
        if(!isElementIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void checkPositionIndex(int index) {
        if(!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    Node<E> node(int index) {
        if(index < (size >> 1)) {
            Node<E> x = first;
            for(int i = 0; i < index; i++)
                x = x.next;
            return x;
        }
        else {
            Node<E> x = last;
            for(int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    public int indexOf(Object o) {
        int index = 0;
        if(o == null) {
            for(Node<E> x = first; x != null; x = x.next) {
                if(x.item == null)
                    return index;
                index++;
            }
        }
        else {
            for(Node<E> x = first; x != null; x = x.next) {
                if(o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        int index = size;
        if(o == null) {
            for(Node<E> x = last; x != null; x = x.prev) {
                index--;
                if(x.item == null)
                    return index;
            }
        }
        else {
            for(Node<E> x = last; x != null; x = x.prev) {
                index--;
                if(o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }

    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    public E element() {
        return getFirst();
    }

    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    public E remove() {
        return removeFirst();
    }

    public boolean offer(E e) {
        return add(e);
    }

    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    public void push(E e) {
        addFirst(e);
    }

    public E pop() {
        return removeFirst();
    }

    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    public boolean removeLastOccurrence(Object o) {
        if(o == null) {
            for(Node<E> x = last; x != null; x = x.prev) {
                if(x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        }
        else {
            for(Node<E> x = last; x != null; x = x.prev) {
                if(o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }

    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if(!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if(!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if(lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if(next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e) {
            if(lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            if(next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while(modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private LinkedList<E> superClone() {
        try {
            return (LinkedList<E>)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    public Object clone() {
        LinkedList<E> clone = superClone();

        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;

        for(Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for(Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if(a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                                a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for(Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if(a.length > size)
            a[size] = null;

        return a;
    }

    // TODO
    // @java.io.Serial
    // private void writeObject(java.io.ObjectOutputStream s)
    //     throws java.io.IOException {
    //     // Write out any hidden serialization magic
    //     s.defaultWriteObject();

    //     // Write out size
    //     s.writeInt(size);

    //     // Write out all elements in the proper order.
    //     for(Node<E> x = first; x != null; x = x.next)
    //         s.writeObject(x.item);
    // }

    // TODO
    // @SuppressWarnings("unchecked")
    // @java.io.Serial
    // private void readObject(java.io.ObjectInputStream s)
    //     throws java.io.IOException, ClassNotFoundException {
    //     // Read in any hidden serialization magic
    //     s.defaultReadObject();

    //     // Read in size
    //     int size = s.readInt();

    //     // Read in all elements in the proper order.
    //     for(int i = 0; i < size; i++)
    //         linkLast((E)s.readObject());
    // }

    @Override
    public Spliterator<E> spliterator() {
        return new LLSpliterator<>(this, -1, 0);
    }

    static final class LLSpliterator<E> implements Spliterator<E> {
        static final int BATCH_UNIT = 1 << 10;
        static final int MAX_BATCH = 1 << 25;
        final LinkedList<E> list;
        Node<E> current;
        int est;
        int expectedModCount;
        int batch;

        LLSpliterator(LinkedList<E> list, int est, int expectedModCount) {
            this.list = list;
            this.est = est;
            this.expectedModCount = expectedModCount;
        }

        final int getEst() {
            int s;
            final LinkedList<E> lst;
            if((s = est) < 0) {
                if((lst = list) == null)
                    s = est = 0;
                else {
                    expectedModCount = lst.modCount;
                    current = lst.first;
                    s = est = lst.size;
                }
            }
            return s;
        }

        public long estimateSize() {
            return (long)getEst();
        }

        public Spliterator<E> trySplit() {
            Node<E> p;
            int s = getEst();
            if(s > 1 && (p = current) != null) {
                int n = batch + BATCH_UNIT;
                if(n > s)
                    n = s;
                if(n > MAX_BATCH)
                    n = MAX_BATCH;
                Object[] a = new Object[n];
                int j = 0;
                do {
                    a[j++] = p.item;
                } while((p = p.next) != null && j < n);
                current = p;
                batch = j;
                est = s - j;
                return Spliterators.spliterator(a, 0, j, Spliterator.ORDERED);
            }
            return null;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Node<E> p; int n;
            if(action == null) throw new NullPointerException();
            if((n = getEst()) > 0 && (p = current) != null) {
                current = null;
                est = 0;
                do {
                    E e = p.item;
                    p = p.next;
                    action.accept(e);
                } while(p != null && --n > 0);
            }
            if(list.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            Node<E> p;
            if(action == null) throw new NullPointerException();
            if(getEst() > 0 && (p = current) != null) {
                --est;
                E e = p.item;
                current = p.next;
                action.accept(e);
                if(list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    public LinkedList<E> reversed() {
        return new ReverseOrderLinkedListView<>(this, super.reversed(), Deque.super.reversed());
    }

    @SuppressWarnings("serial")
    static class ReverseOrderLinkedListView<E> extends LinkedList<E> {
        final LinkedList<E> list;
        final List<E> rlist;
        final Deque<E> rdeque;

        ReverseOrderLinkedListView(LinkedList<E> list, List<E> rlist, Deque<E> rdeque) {
            this.list = list;
            this.rlist = rlist;
            this.rdeque = rdeque;
        }

        public String toString() {
            return rlist.toString();
        }

        public boolean retainAll(Collection<?> c) {
            return rlist.retainAll(c);
        }

        public boolean removeAll(Collection<?> c) {
            return rlist.removeAll(c);
        }

        public boolean containsAll(Collection<?> c) {
            return rlist.containsAll(c);
        }

        public boolean isEmpty() {
            return rlist.isEmpty();
        }

        // TODO
        // public Stream<E> parallelStream() {
        //     return rlist.parallelStream();
        // }

        // TODO
        // public Stream<E> stream() {
        //     return rlist.stream();
        // }

        public boolean removeIf(Predicate<? super E> filter) {
            return rlist.removeIf(filter);
        }

        public <T> T[] toArray(IntFunction<T[]> generator) {
            return rlist.toArray(generator);
        }

        public void forEach(Consumer<? super E> action) {
            rlist.forEach(action);
        }

        public Iterator<E> iterator() {
            return rlist.iterator();
        }

        public int hashCode() {
            return rlist.hashCode();
        }

        public boolean equals(Object o) {
            return rlist.equals(o);
        }

        public List<E> subList(int fromIndex, int toIndex) {
            return rlist.subList(fromIndex, toIndex);
        }

        public ListIterator<E> listIterator() {
            return rlist.listIterator();
        }

        public void sort(Comparator<? super E> c) {
            rlist.sort(c);
        }

        public void replaceAll(UnaryOperator<E> operator) {
            rlist.replaceAll(operator);
        }

        public LinkedList<E> reversed() {
            return list;
        }

        public Spliterator<E> spliterator() {
            return rlist.spliterator();
        }

        public <T> T[] toArray(T[] a) {
            return rlist.toArray(a);
        }

        public Object[] toArray() {
            return rlist.toArray();
        }

        public Iterator<E> descendingIterator() {
            return rdeque.descendingIterator();
        }

        public ListIterator<E> listIterator(int index) {
            return rlist.listIterator(index);
        }

        public boolean removeLastOccurrence(Object o) {
            return rdeque.removeLastOccurrence(o);
        }

        public boolean removeFirstOccurrence(Object o) {
            return rdeque.removeFirstOccurrence(o);
        }

        public E pop() {
            return rdeque.pop();
        }

        public void push(E e) {
            rdeque.push(e);
        }

        public E pollLast() {
            return rdeque.pollLast();
        }

        public E pollFirst() {
            return rdeque.pollFirst();
        }

        public E peekLast() {
            return rdeque.peekLast();
        }

        public E peekFirst() {
            return rdeque.peekFirst();
        }

        public boolean offerLast(E e) {
            return rdeque.offerLast(e);
        }

        public boolean offerFirst(E e) {
            return rdeque.offerFirst(e);
        }

        public boolean offer(E e) {
            return rdeque.offer(e);
        }

        public E remove() {
            return rdeque.remove();
        }

        public E poll() {
            return rdeque.poll();
        }

        public E element() {
            return rdeque.element();
        }

        public E peek() {
            return rdeque.peek();
        }

        public int lastIndexOf(Object o) {
            return rlist.lastIndexOf(o);
        }

        public int indexOf(Object o) {
            return rlist.indexOf(o);
        }

        public E remove(int index) {
            return rlist.remove(index);
        }

        public void add(int index, E element) {
            rlist.add(index, element);
        }

        public E set(int index, E element) {
            return rlist.set(index, element);
        }

        public E get(int index) {
            return rlist.get(index);
        }

        public void clear() {
            rlist.clear();
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            return rlist.addAll(index, c);
        }

        public boolean addAll(Collection<? extends E> c) {
            return rlist.addAll(c);
        }

        public boolean remove(Object o) {
            return rlist.remove(o);
        }

        public boolean add(E e) {
            return rlist.add(e);
        }

        public int size() {
            return rlist.size();
        }

        public boolean contains(Object o) {
            return rlist.contains(o);
        }

        public void addLast(E e) {
            rdeque.addLast(e);
        }

        public void addFirst(E e) {
            rdeque.addFirst(e);
        }

        public E removeLast() {
            return rdeque.removeLast();
        }

        public E removeFirst() {
            return rdeque.removeFirst();
        }

        public E getLast() {
            return rdeque.getLast();
        }

        public E getFirst() {
            return rdeque.getFirst();
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            throw new java.io.InvalidObjectException("not serializable");
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            throw new java.io.InvalidObjectException("not serializable");
        }
    }
}
