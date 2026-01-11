package flint.math;

import java.util.List;
import java.util.Objects;
import java.util.AbstractList;
import java.util.RandomAccess;
import java.util.NoSuchElementException;

public class ComplexList extends AbstractList<Complex> implements List<Complex>, RandomAccess, Cloneable {
    private float[] reals;
    private float[] imags;
    private int size;

    ComplexList(float[] reals, float[] imags) {
        this.reals = reals;
        this.imags = imags;
        this.size = reals.length;
    }

    public ComplexList() {
        this.reals = null;
        this.imags = null;
    }

    public ComplexList(int initialCapacity) {
        if(initialCapacity > 0) {
            this.reals = new float[initialCapacity];
            this.imags = new float[initialCapacity];
        }
        else if(initialCapacity == 0) {
            this.reals = null;
            this.imags = null;
        }
        else
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }

    public ComplexList(int[] array) {
        Objects.requireNonNull(array);
        float[] reals = new float[array.length];
        for(int i = 0; i < array.length; i++)
            reals[i] = (float)array[i];
        this.reals = reals;
        this.imags = null;
        this.size = reals.length;
    }

    public ComplexList(float[] array) {
        Objects.requireNonNull(array);
        float[] reals = array.clone();
        this.reals = reals;
        this.imags = null;
        this.size = reals.length;
    }

    public ComplexList(Complex[] array) {
        Objects.requireNonNull(array);
        int len = array.length;
        float[] reals = new float[len];
        float[] imags = new float[len];
        for(int i = 0; i < len; i++) {
            reals[i] = array[i].real;
            imags[i] = array[i].imag;
        }
        this.reals = reals;
        this.imags = imags;
        this.size = reals.length;
    }

    float[] getRealArray() {
        return reals;
    }

    float[] getImagArray() {
        return imags;
    }

    public float getReal(int index) {
        Objects.checkIndex(index, size);
        return reals[index];
    }

    public float getImag(int index) {
        Objects.checkIndex(index, size);
        return (imags != null) ? imags[index] : 0;
    }

    @Override
    public Complex get(int index) {
        Objects.checkIndex(index, size);
        float imag = (imags != null) ? imags[index] : 0;
        return new Complex(reals[index], imag);
    }

    public Complex set(int index, int element) {
        return set(index, (float)element);
    }

    public Complex set(int index, float element) {
        Objects.checkIndex(index, size);
        reals[index] = element;
        if(imags != null)
            imags[index] = 0;
        return new Complex(reals[index], 0);
    }

    @Override
    public Complex set(int index, Complex element) {
        Objects.checkIndex(index, size);
        reals[index] = element.real;
        if(element.imag != 0) {
            if(imags == null)
                imags = new float[reals.length];
            imags[index] = element.imag;
        }
        else if(imags != null)
            imags[index] = 0;
        return element;
    }

    public boolean add(int e) {
        add(size, (float)e);
        return true;
    }

    public boolean add(float e) {
        add(size, e);
        return true;
    }

    @Override
    public boolean add(Complex e) {
        add(size, e);
        return true;
    }

    public void add(int index, int element) {
        add(index, (float)element);
    }

    public void add(int index, float element) {
        rangeCheckForAdd(index);
        ensureCapacity(size + 1);

        System.arraycopy(reals, index, reals, index + 1, size - index);
        reals[index] = element;
        if(imags != null)
            imags[index] = 0;

        size++;
    }

    @Override
    public void add(int index, Complex element) {
        rangeCheckForAdd(index);
        ensureCapacity(size + 1);

        System.arraycopy(reals, index, reals, index + 1, size - index);
        reals[index] = element.real;

        if(element.imag != 0) {
            if(imags == null)
                imags = new float[reals.length];
            else
                System.arraycopy(imags, index, imags, index + 1, size - index);
            imags[index] = element.imag;
        }
        else if(imags != null)
            imags[index] = 0;

        size++;
    }

    public void addFirst(int element) {
        add(0, (float)element);
    }

    public void addFirst(float element) {
        add(0, element);
    }

    @Override
    public void addFirst(Complex element) {
        add(0, element);
    }

    public void addLast(int element) {
        add(size, (float)element);
    }

    public void addLast(float element) {
        add(size, element);
    }

    @Override
    public void addLast(Complex element) {
        add(size, element);
    }

    private void ensureCapacity(int minCapacity) {
        float[] r = reals;
        if(r == null || minCapacity > r.length) {
            minCapacity = (minCapacity + 15) & 0xFFFFFFF0;

            reals = new float[minCapacity];
            if(r != null)
                System.arraycopy(r, 0, reals, 0, r.length);
            
            float[] i = imags;
            if(i != null) {
                imags = new float[minCapacity];
                System.arraycopy(i, 0, imags, 0, i.length);
            }
        }
    }

    @Override
    public Complex remove(int index) {
        Objects.checkIndex(index, size);
        Complex oldValue = get(index);
        fastRemove(index);
        return oldValue;
    }

    @Override
    public Complex removeFirst() {
        if(size == 0)
            throw new NoSuchElementException();
        else {
            Complex oldValue = get(0);
            fastRemove(0);
            return oldValue;
        }
    }

    @Override
    public Complex removeLast() {
        int last = size - 1;
        if(last < 0)
            throw new NoSuchElementException();
        else {
            Complex oldValue = get(last);
            fastRemove(last);
            return oldValue;
        }
    }

    @Override
    public boolean remove(Object o) {
        if(o == null)
            return false;
        if(o instanceof Complex c) {
            final int size = this.size;
            for(int i = 0; i < size; i++) {
                if(reals[i] == c.real && c.imag == ((imags != null) ? imags[i] : 0)) {
                    fastRemove(i);
                    return true;
                }
            }
        }
        return false;
    }

    private void fastRemove(int i) {
        final int newSize = size - 1;
        if(newSize > i) {
            System.arraycopy(reals, i + 1, reals, i, newSize - i);
            if(imags != null)
                System.arraycopy(imags, i + 1, imags, i, newSize - i);
        }
        size = newSize;
        reals[size] = 0;
        if(imags != null)
            imags[size] = 0;
    }

    private void rangeCheckForAdd(int index) {
        if(index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object clone() {
        float[] reals = this.reals != null ? this.reals.clone() : null;
        float[] imags = this.imags != null ? this.imags.clone() : null;
        return new ComplexList(reals, imags);
    }
}
