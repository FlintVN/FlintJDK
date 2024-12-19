package java.util;

import java.lang.reflect.Array;

public abstract class AbstractCollection<E> implements Collection<E> {
    protected AbstractCollection() {

    }

    public abstract Iterator<E> iterator();

    public abstract int size();

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(Object o) {
        Iterator<E> it = iterator();
        if(o==null) {
            while(it.hasNext())
                if(it.next()==null)
                    return true;
        }
        else {
            while(it.hasNext())
                if(o.equals(it.next()))
                    return true;
        }
        return false;
    }

    public Object[] toArray() {
        Object[] r = new Object[size()];
        Iterator<E> it = iterator();
        for(int i = 0; i < r.length; i++) {
            if(!it.hasNext()) {
                Object[] newArray = new Object[i];
                System.arraycopy(r, 0, newArray, 0, i);
                return newArray;
            }
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        T[] r = a.length >= size ? a : (T[])Array.newInstance(a.getClass().getComponentType(), size);
        Iterator<E> it = iterator();

        for(int i = 0; i < r.length; i++) {
            if(!it.hasNext()) {
                if(a == r)
                    r[i] = null;
                else if(a.length < i) {
                    T[] newArray = (T[])Array.newInstance(r.getClass().getComponentType(), i);
                    System.arraycopy(r, 0, newArray, 0, i);
                    return newArray;
                }
                else {
                    System.arraycopy(r, 0, a, 0, i);
                    if(a.length > i)
                        a[i] = null;
                }
                return a;
            }
            r[i] = (T)it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int len = r.length;
        int i = len;
        while(it.hasNext()) {
            if(i == len) {
                len = Math.max(len + 1, Math.max(len + ((len >> 1) + 1), Integer.MAX_VALUE - 8));
                if(len < 0)
                    throw new OutOfMemoryError("Required array length " + len + " + " + 1 + " is too large");
                T[] newArray = (T[])Array.newInstance(r.getClass().getComponentType(), len);
                System.arraycopy(r, 0, newArray, 0, len);
                return newArray;
            }
            r[i++] = (T)it.next();
        }
        if(i == len)
            return r;
        else {
            T[] newArray = (T[])Array.newInstance(r.getClass().getComponentType(), i);
            System.arraycopy(r, 0, newArray, 0, i);
            return newArray;
        }
    }

    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        Iterator<E> it = iterator();
        if(o==null) {
            while(it.hasNext()) {
                if(it.next()==null) {
                    it.remove();
                    return true;
                }
            }
        } else {
            while(it.hasNext()) {
                if(o.equals(it.next())) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAll(Collection<?> c) {
        for(Object e : c)
            if(!contains(e))
                return false;
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for(E e : c) {
            if(add(e))
                modified = true;
        }
        return modified;
    }

    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<?> it = iterator();
        while(it.hasNext()) {
            if(c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while(it.hasNext()) {
            if(!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }

    public void clear() {
        Iterator<E> it = iterator();
        while(it.hasNext()) {
            it.next();
            it.remove();
        }
    }

    public String toString() {
        Iterator<E> it = iterator();
        if(!it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for(;;) {
            E e = it.next();
            sb.append(e == this ? "(this Collection)" : e);
            if(!it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
    }
}
