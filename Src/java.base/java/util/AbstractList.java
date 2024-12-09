package java.util;

import java.util.function.Consumer;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
    protected AbstractList() {

    }

    public boolean add(E e) {
        add(size(), e);
        return true;
    }

    public abstract E get(int index);

    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    public int indexOf(Object o) {
        ListIterator<E> it = listIterator();
        if(o == null) {
            while(it.hasNext())
                if(it.next() == null)
                    return it.previousIndex();
        } else {
            while(it.hasNext())
                if(o.equals(it.next()))
                    return it.previousIndex();
        }
        return -1;
    }

    public int lastIndexOf(Object o) {
        ListIterator<E> it = listIterator(size());
        if(o == null) {
            while(it.hasPrevious())
                if(it.previous() == null)
                    return it.nextIndex();
        }
        else {
            while(it.hasPrevious())
                if(o.equals(it.previous()))
                    return it.nextIndex();
        }
        return -1;
    }

    public void clear() {
        removeRange(0, size());
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);
        boolean modified = false;
        for(E e : c) {
            add(index++, e);
            modified = true;
        }
        return modified;
    }

    public Iterator<E> iterator() {
        return new Itr();
    }

    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    public ListIterator<E> listIterator(final int index) {
        rangeCheckForAdd(index);

        return new ListItr(index);
    }

    private class Itr implements Iterator<E> {
        int cursor = 0;

        int lastRet = -1;

        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size();
        }

        public E next() {
            checkForComodification();
            try {
                int i = cursor;
                E next = get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            }
            catch(IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException(e);
            }
        }

        public void remove() {
            if(lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                AbstractList.this.remove(lastRet);
                if(lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if(modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public E previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                E previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch(IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException(e);
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor-1;
        }

        public void set(E e) {
            if(lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                AbstractList.this.set(lastRet, e);
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                AbstractList.this.add(i, e);
                lastRet = -1;
                cursor = i + 1;
                expectedModCount = modCount;
            } catch(IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public List<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size());
        return (this instanceof RandomAccess ?
                new RandomAccessSubList<>(this, fromIndex, toIndex) :
                new SubList<>(this, fromIndex, toIndex));
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if(fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if(toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if(fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
    }


    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(!(o instanceof List))
            return false;

        ListIterator<E> e1 = listIterator();
        ListIterator<?> e2 = ((List<?>) o).listIterator();
        while(e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if(!(o1 == null ? o2 == null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    public int hashCode() {
        int hashCode = 1;
        for(E e : this)
            hashCode = 31*hashCode + (e == null ? 0 : e.hashCode());
        return hashCode;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        ListIterator<E> it = listIterator(fromIndex);
        for(int i = 0, n = toIndex - fromIndex; i < n; i++) {
            it.next();
            it.remove();
        }
    }

    protected transient int modCount = 0;

    private void rangeCheckForAdd(int index) {
        if(index < 0 || index > size())
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size();
    }

    static final class RandomAccessSpliterator<E> implements Spliterator<E> {
        private final List<E> list;
        private int index;
        private int fence;

        private final AbstractList<E> alist;
        private int expectedModCount;

        RandomAccessSpliterator(List<E> list) {
            // assert list instanceof RandomAccess;

            this.list = list;
            this.index = 0;
            this.fence = -1;

            this.alist = list instanceof AbstractList ? (AbstractList<E>) list : null;
            this.expectedModCount = alist != null ? alist.modCount : 0;
        }

        private RandomAccessSpliterator(RandomAccessSpliterator<E> parent, int origin, int fence) {
            this.list = parent.list;
            this.index = origin;
            this.fence = fence;

            this.alist = parent.alist;
            this.expectedModCount = parent.expectedModCount;
        }

        private int getFence() {
            int hi;
            List<E> lst = list;
            if((hi = fence) < 0) {
                if(alist != null)
                    expectedModCount = alist.modCount;
                hi = fence = lst.size();
            }
            return hi;
        }

        public Spliterator<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : new RandomAccessSpliterator<>(this, lo, index = mid);
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if(action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if(i < hi) {
                index = i + 1;
                action.accept(get(list, i));
                checkAbstractListModCount(alist, expectedModCount);
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            List<E> lst = list;
            int hi = getFence();
            int i = index;
            index = hi;
            for(; i < hi; i++)
                action.accept(get(lst, i));
            checkAbstractListModCount(alist, expectedModCount);
        }

        public long estimateSize() {
            return (long) (getFence() - index);
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }

        private static <E> E get(List<E> list, int i) {
            try {
                return list.get(i);
            }
            catch(IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        static void checkAbstractListModCount(AbstractList<?> alist, int expectedModCount) {
            if(alist != null && alist.modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private static class SubList<E> extends AbstractList<E> {
        private final AbstractList<E> root;
        private final SubList<E> parent;
        private final int offset;
        protected int size;

        public SubList(AbstractList<E> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = null;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        protected SubList(SubList<E> parent, int fromIndex, int toIndex) {
            this.root = parent.root;
            this.parent = parent;
            this.offset = parent.offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = root.modCount;
        }

        public E set(int index, E element) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.set(offset + index, element);
        }

        public E get(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            return root.get(offset + index);
        }

        public int size() {
            checkForComodification();
            return size;
        }

        public void add(int index, E element) {
            rangeCheckForAdd(index);
            checkForComodification();
            root.add(offset + index, element);
            updateSizeAndModCount(1);
        }

        public E remove(int index) {
            Objects.checkIndex(index, size);
            checkForComodification();
            E result = root.remove(offset + index);
            updateSizeAndModCount(-1);
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            root.removeRange(offset + fromIndex, offset + toIndex);
            updateSizeAndModCount(fromIndex - toIndex);
        }

        public boolean addAll(Collection<? extends E> c) {
            return addAll(size, c);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if(cSize == 0)
                return false;
            checkForComodification();
            root.addAll(offset + index, c);
            updateSizeAndModCount(cSize);
            return true;
        }

        public Iterator<E> iterator() {
            return listIterator();
        }

        public ListIterator<E> listIterator(int index) {
            checkForComodification();
            rangeCheckForAdd(index);

            return new ListIterator<E>() {
                private final ListIterator<E> i = root.listIterator(offset + index);

                public boolean hasNext() {
                    return nextIndex() < size;
                }

                public E next() {
                    if(hasNext())
                        return i.next();
                    else
                        throw new NoSuchElementException();
                }

                public boolean hasPrevious() {
                    return previousIndex() >= 0;
                }

                public E previous() {
                    if(hasPrevious())
                        return i.previous();
                    else
                        throw new NoSuchElementException();
                }

                public int nextIndex() {
                    return i.nextIndex() - offset;
                }

                public int previousIndex() {
                    return i.previousIndex() - offset;
                }

                public void remove() {
                    i.remove();
                    updateSizeAndModCount(-1);
                }

                public void set(E e) {
                    i.set(e);
                }

                public void add(E e) {
                    i.add(e);
                    updateSizeAndModCount(1);
                }
            };
        }

        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList<>(this, fromIndex, toIndex);
        }

        private void rangeCheckForAdd(int index) {
            if(index < 0 || index > size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: " + index + ", Size: " + size;
        }

        private void checkForComodification() {
            if(root.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        private void updateSizeAndModCount(int sizeChange) {
            SubList<E> slist = this;
            do {
                slist.size += sizeChange;
                slist.modCount = root.modCount;
                slist = slist.parent;
            } while(slist != null);
        }
    }

    private static class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {
        RandomAccessSubList(AbstractList<E> root, int fromIndex, int toIndex) {
            super(root, fromIndex, toIndex);
        }

        RandomAccessSubList(RandomAccessSubList<E> parent, int fromIndex, int toIndex) {
            super(parent, fromIndex, toIndex);
        }

        public List<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new RandomAccessSubList<>(this, fromIndex, toIndex);
        }
    }
}
