package java.util;

import jdk.internal.util.ArraysSupport;
import jdk.internal.vm.annotation.IntrinsicCandidate;

import java.io.Serializable;
import java.lang.reflect.Array;
// import java.util.concurrent.ForkJoinPool;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongBinaryOperator;
import java.util.function.UnaryOperator;
// import java.util.stream.DoubleStream;
// import java.util.stream.IntStream;
// import java.util.stream.LongStream;
// import java.util.stream.Stream;
// import java.util.stream.StreamSupport;

public class Arrays {
    private Arrays() {

    }

    public static void sort(int[] a) {
        DualPivotQuicksort.sort(a, 0, a.length);
    }

    public static void sort(int[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, fromIndex, toIndex);
    }

    public static void sort(long[] a) {
        DualPivotQuicksort.sort(a, 0, a.length);
    }

    public static void sort(long[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, fromIndex, toIndex);
    }

    public static void sort(short[] a) {
        DualPivotQuicksort.sort(a, 0, a.length);
    }

    public static void sort(short[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, fromIndex, toIndex);
    }

    public static void sort(char[] a) {
        DualPivotQuicksort.sort(a, 0, a.length);
    }

    public static void sort(char[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, fromIndex, toIndex);
    }

    public static void sort(byte[] a) {
        DualPivotQuicksort.sort(a, 0, a.length);
    }

    public static void sort(byte[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, fromIndex, toIndex);
    }

    public static void sort(float[] a) {
        DualPivotQuicksort.sort(a, 0, 0, a.length);
    }

    public static void sort(float[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, 0, fromIndex, toIndex);
    }

    public static void sort(double[] a) {
        DualPivotQuicksort.sort(a, 0, 0, a.length);
    }

    public static void sort(double[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        DualPivotQuicksort.sort(a, 0, fromIndex, toIndex);
    }

    // TODO
    // public static void parallelSort(byte[] a) {
    //     DualPivotQuicksort.sort(a, 0, a.length);
    // }

    // TODO
    // public static void parallelSort(byte[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, fromIndex, toIndex);
    // }

    // TODO
    // public static void parallelSort(char[] a) {
    //     DualPivotQuicksort.sort(a, 0, a.length);
    // }

    // TODO
    // public static void parallelSort(char[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, fromIndex, toIndex);
    // }

    // TODO
    // public static void parallelSort(short[] a) {
    //     DualPivotQuicksort.sort(a, 0, a.length);
    // }

    // TODO
    // public static void parallelSort(short[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, fromIndex, toIndex);
    // }

    // TODO
    // public static void parallelSort(int[] a) {
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), 0, a.length);
    // }

    // TODO
    // public static void parallelSort(int[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), fromIndex, toIndex);
    // }

    // TODO
    // public static void parallelSort(long[] a) {
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), 0, a.length);
    // }

    // TODO
    // public static void parallelSort(long[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), fromIndex, toIndex);
    // }

    // TODO
    // public static void parallelSort(float[] a) {
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), 0, a.length);
    // }

    // TODO
    // public static void parallelSort(float[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), fromIndex, toIndex);
    // }

    // TODO
    // public static void parallelSort(double[] a) {
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), 0, a.length);
    // }

    // TODO
    // public static void parallelSort(double[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     DualPivotQuicksort.sort(a, ForkJoinPool.getCommonPoolParallelism(), fromIndex, toIndex);
    // }

    static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if(fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        if(fromIndex < 0)
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        if(toIndex > arrayLength)
            throw new ArrayIndexOutOfBoundsException(toIndex);
    }

    static final class NaturalOrder implements Comparator<Object> {
        @SuppressWarnings("unchecked")
        public int compare(Object first, Object second) {
            return ((Comparable<Object>)first).compareTo(second);
        }
        static final NaturalOrder INSTANCE = new NaturalOrder();
    }

    // TODO
    // private static final int MIN_ARRAY_SORT_GRAN = 1 << 13;

    // TODO
    // @SuppressWarnings("unchecked")
    // public static <T extends Comparable<? super T>> void parallelSort(T[] a) {
    //     int n = a.length, p, g;
    //     if(n <= MIN_ARRAY_SORT_GRAN || (p = ForkJoinPool.getCommonPoolParallelism()) == 1)
    //         TimSort.sort(a, 0, n, NaturalOrder.INSTANCE, null, 0, 0);
    //     else
    //         new ArraysParallelSortHelpers.FJObject.Sorter<>(
    //             null, a,
    //             (T[])Array.newInstance(a.getClass().getComponentType(), n),
    //             0, n, 0, ((g = n / (p << 2)) <= MIN_ARRAY_SORT_GRAN) ?
    //             MIN_ARRAY_SORT_GRAN : g, NaturalOrder.INSTANCE
    //         ).invoke();
    // }

    // TODO
    // @SuppressWarnings("unchecked")
    // public static <T extends Comparable<? super T>> void parallelSort(T[] a, int fromIndex, int toIndex) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     int n = toIndex - fromIndex, p, g;
    //     if(n <= MIN_ARRAY_SORT_GRAN || (p = ForkJoinPool.getCommonPoolParallelism()) == 1)
    //         TimSort.sort(a, fromIndex, toIndex, NaturalOrder.INSTANCE, null, 0, 0);
    //     else
    //         new ArraysParallelSortHelpers.FJObject.Sorter<>(
    //             null, a,
    //             (T[])Array.newInstance(a.getClass().getComponentType(), n),
    //             fromIndex, n, 0, ((g = n / (p << 2)) <= MIN_ARRAY_SORT_GRAN) ?
    //             MIN_ARRAY_SORT_GRAN : g, NaturalOrder.INSTANCE
    //         ).invoke();
    // }

    // TODO
    // @SuppressWarnings("unchecked")
    // public static <T> void parallelSort(T[] a, Comparator<? super T> cmp) {
    //     if(cmp == null)
    //         cmp = NaturalOrder.INSTANCE;
    //     int n = a.length, p, g;
    //     if(n <= MIN_ARRAY_SORT_GRAN || (p = ForkJoinPool.getCommonPoolParallelism()) == 1)
    //         TimSort.sort(a, 0, n, cmp, null, 0, 0);
    //     else
    //         new ArraysParallelSortHelpers.FJObject.Sorter<>(
    //             null, a,
    //             (T[])Array.newInstance(a.getClass().getComponentType(), n),
    //             0, n, 0, ((g = n / (p << 2)) <= MIN_ARRAY_SORT_GRAN) ?
    //             MIN_ARRAY_SORT_GRAN : g, cmp
    //         ).invoke();
    // }

    // TODO
    // @SuppressWarnings("unchecked")
    // public static <T> void parallelSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> cmp) {
    //     rangeCheck(a.length, fromIndex, toIndex);
    //     if(cmp == null)
    //         cmp = NaturalOrder.INSTANCE;
    //     int n = toIndex - fromIndex, p, g;
    //     if(n <= MIN_ARRAY_SORT_GRAN || (p = ForkJoinPool.getCommonPoolParallelism()) == 1)
    //         TimSort.sort(a, fromIndex, toIndex, cmp, null, 0, 0);
    //     else
    //         new ArraysParallelSortHelpers.FJObject.Sorter<>(
    //             null, a,
    //             (T[])Array.newInstance(a.getClass().getComponentType(), n),
    //             fromIndex, n, 0, ((g = n / (p << 2)) <= MIN_ARRAY_SORT_GRAN) ?
    //             MIN_ARRAY_SORT_GRAN : g, cmp
    //         ).invoke();
    // }

    static final class LegacyMergeSort {
        @SuppressWarnings("removal")
        // TODO
        private static final boolean userRequested = false;
    }

    public static void sort(Object[] a) {
        if(LegacyMergeSort.userRequested)
            legacyMergeSort(a);
        else
            ComparableTimSort.sort(a, 0, a.length, null, 0, 0);
    }

    private static void legacyMergeSort(Object[] a) {
        Object[] aux = a.clone();
        mergeSort(aux, a, 0, a.length, 0);
    }

    public static void sort(Object[] a, int fromIndex, int toIndex) {
        rangeCheck(a.length, fromIndex, toIndex);
        if(LegacyMergeSort.userRequested)
            legacyMergeSort(a, fromIndex, toIndex);
        else
            ComparableTimSort.sort(a, fromIndex, toIndex, null, 0, 0);
    }

    private static void legacyMergeSort(Object[] a, int fromIndex, int toIndex) {
        Object[] aux = copyOfRange(a, fromIndex, toIndex);
        mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
    }

    private static final int INSERTIONSORT_THRESHOLD = 7;

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void mergeSort(Object[] src, Object[] dest, int low, int high, int off) {
        int length = high - low;
        if(length < INSERTIONSORT_THRESHOLD) {
            for(int i = low; i < high; i++)
                for(int j = i; j > low && ((Comparable) dest[j-1]).compareTo(dest[j])>0; j--)
                    swap(dest, j, j-1);
            return;
        }

        int destLow  = low;
        int destHigh = high;
        low  += off;
        high += off;
        int mid = (low + high) >>> 1;
        mergeSort(dest, src, low, mid, -off);
        mergeSort(dest, src, mid, high, -off);

        if(((Comparable)src[mid-1]).compareTo(src[mid]) <= 0) {
            System.arraycopy(src, low, dest, destLow, length);
            return;
        }

        for(int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if(q >= high || p < mid && ((Comparable)src[p]).compareTo(src[q])<=0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    private static void swap(Object[] x, int a, int b) {
        Object t = x[a];
        x[a] = x[b];
        x[b] = t;
    }

    public static <T> void sort(T[] a, Comparator<? super T> c) {
        if(c == null)
            sort(a);
        else {
            if(LegacyMergeSort.userRequested)
                legacyMergeSort(a, c);
            else
                TimSort.sort(a, 0, a.length, c, null, 0, 0);
        }
    }

    private static <T> void legacyMergeSort(T[] a, Comparator<? super T> c) {
        T[] aux = a.clone();
        if(c == null)
            mergeSort(aux, a, 0, a.length, 0);
        else
            mergeSort(aux, a, 0, a.length, 0, c);
    }

    public static <T> void sort(T[] a, int fromIndex, int toIndex, Comparator<? super T> c) {
        if(c == null)
            sort(a, fromIndex, toIndex);
        else {
            rangeCheck(a.length, fromIndex, toIndex);
            if(LegacyMergeSort.userRequested)
                legacyMergeSort(a, fromIndex, toIndex, c);
            else
                TimSort.sort(a, fromIndex, toIndex, c, null, 0, 0);
        }
    }

    private static <T> void legacyMergeSort(T[] a, int fromIndex, int toIndex, Comparator<? super T> c) {
        T[] aux = copyOfRange(a, fromIndex, toIndex);
        if(c == null)
            mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
        else
            mergeSort(aux, a, fromIndex, toIndex, -fromIndex, c);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void mergeSort(Object[] src, Object[] dest, int low, int high, int off, Comparator c) {
        int length = high - low;

        if(length < INSERTIONSORT_THRESHOLD) {
            for(int i = low; i < high; i++)
                for(int j = i; j > low && c.compare(dest[j-1], dest[j]) > 0; j--)
                    swap(dest, j, j-1);
            return;
        }

        int destLow  = low;
        int destHigh = high;
        low  += off;
        high += off;
        int mid = (low + high) >>> 1;
        mergeSort(dest, src, low, mid, -off, c);
        mergeSort(dest, src, mid, high, -off, c);

        if(c.compare(src[mid-1], src[mid]) <= 0) {
           System.arraycopy(src, low, dest, destLow, length);
           return;
        }

        for(int i = destLow, p = low, q = mid; i < destHigh; i++) {
            if(q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
                dest[i] = src[p++];
            else
                dest[i] = src[q++];
        }
    }

    // TODO
    // public static <T> void parallelPrefix(T[] array, BinaryOperator<T> op) {
    //     Objects.requireNonNull(op);
    //     if(array.length > 0)
    //         new ArrayPrefixHelpers.CumulateTask<>(null, op, array, 0, array.length).invoke();
    // }

    // TODO
    // public static <T> void parallelPrefix(T[] array, int fromIndex, int toIndex, BinaryOperator<T> op) {
    //     Objects.requireNonNull(op);
    //     rangeCheck(array.length, fromIndex, toIndex);
    //     if(fromIndex < toIndex)
    //         new ArrayPrefixHelpers.CumulateTask<>(null, op, array, fromIndex, toIndex).invoke();
    // }

    // TODO
    // public static void parallelPrefix(long[] array, LongBinaryOperator op) {
    //     Objects.requireNonNull(op);
    //     if(array.length > 0)
    //         new ArrayPrefixHelpers.LongCumulateTask(null, op, array, 0, array.length).invoke();
    // }

    // TODO
    // public static void parallelPrefix(long[] array, int fromIndex, int toIndex, LongBinaryOperator op) {
    //     Objects.requireNonNull(op);
    //     rangeCheck(array.length, fromIndex, toIndex);
    //     if(fromIndex < toIndex)
    //         new ArrayPrefixHelpers.LongCumulateTask(null, op, array, fromIndex, toIndex).invoke();
    // }

    // TODO
    // public static void parallelPrefix(double[] array, DoubleBinaryOperator op) {
    //     Objects.requireNonNull(op);
    //     if(array.length > 0)
    //         new ArrayPrefixHelpers.DoubleCumulateTask(null, op, array, 0, array.length).invoke();
    // }

    // TODO
    // public static void parallelPrefix(double[] array, int fromIndex, int toIndex, DoubleBinaryOperator op) {
    //     Objects.requireNonNull(op);
    //     rangeCheck(array.length, fromIndex, toIndex);
    //     if(fromIndex < toIndex)
    //         new ArrayPrefixHelpers.DoubleCumulateTask(null, op, array, fromIndex, toIndex).invoke();
    // }

    // TODO
    // public static void parallelPrefix(int[] array, IntBinaryOperator op) {
    //     Objects.requireNonNull(op);
    //     if(array.length > 0)
    //         new ArrayPrefixHelpers.IntCumulateTask
    //                 (null, op, array, 0, array.length).invoke();
    // }

    // TODO
    // public static void parallelPrefix(int[] array, int fromIndex, int toIndex, IntBinaryOperator op) {
    //     Objects.requireNonNull(op);
    //     rangeCheck(array.length, fromIndex, toIndex);
    //     if(fromIndex < toIndex)
    //         new ArrayPrefixHelpers.IntCumulateTask(null, op, array, fromIndex, toIndex).invoke();
    // }

    public static int binarySearch(long[] a, long key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(long[] a, int fromIndex, int toIndex, long key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(long[] a, int fromIndex, int toIndex, long key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            long midVal = a[mid];
            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static int binarySearch(int[] a, int key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(int[] a, int fromIndex, int toIndex, int key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(int[] a, int fromIndex, int toIndex, int key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            int midVal = a[mid];
            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static int binarySearch(short[] a, short key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(short[] a, int fromIndex, int toIndex, short key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(short[] a, int fromIndex, int toIndex, short key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            short midVal = a[mid];
            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static int binarySearch(char[] a, char key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(char[] a, int fromIndex, int toIndex, char key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(char[] a, int fromIndex, int toIndex, char key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            char midVal = a[mid];
            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static int binarySearch(byte[] a, byte key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(byte[] a, int fromIndex, int toIndex, byte key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(byte[] a, int fromIndex, int toIndex, byte key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            byte midVal = a[mid];
            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static int binarySearch(double[] a, double key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(double[] a, int fromIndex, int toIndex, double key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(double[] a, int fromIndex, int toIndex, double key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            double midVal = a[mid];
            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else {
                long midBits = Double.doubleToLongBits(midVal);
                long keyBits = Double.doubleToLongBits(key);
                if(midBits == keyBits)
                    return mid;
                else if(midBits < keyBits)
                    low = mid + 1;
                else
                    high = mid - 1;
            }
        }
        return -(low + 1);
    }

    public static int binarySearch(float[] a, float key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(float[] a, int fromIndex, int toIndex, float key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(float[] a, int fromIndex, int toIndex, float key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            float midVal = a[mid];

            if(midVal < key)
                low = mid + 1;
            else if(midVal > key)
                high = mid - 1;
            else {
                int midBits = Float.floatToIntBits(midVal);
                int keyBits = Float.floatToIntBits(key);
                if(midBits == keyBits)
                    return mid;
                else if(midBits < keyBits)
                    low = mid + 1;
                else
                    high = mid - 1;
            }
        }
        return -(low + 1);
    }

    public static int binarySearch(Object[] a, Object key) {
        return binarySearch0(a, 0, a.length, key);
    }

    public static int binarySearch(Object[] a, int fromIndex, int toIndex, Object key) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key);
    }

    private static int binarySearch0(Object[] a, int fromIndex, int toIndex, Object key) {
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("rawtypes")
            Comparable midVal = (Comparable)a[mid];
            @SuppressWarnings("unchecked")
            int cmp = midVal.compareTo(key);
            if(cmp < 0)
                low = mid + 1;
            else if(cmp > 0)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static <T> int binarySearch(T[] a, T key, Comparator<? super T> c) {
        return binarySearch0(a, 0, a.length, key, c);
    }

    public static <T> int binarySearch(T[] a, int fromIndex, int toIndex, T key, Comparator<? super T> c) {
        rangeCheck(a.length, fromIndex, toIndex);
        return binarySearch0(a, fromIndex, toIndex, key, c);
    }

    private static <T> int binarySearch0(T[] a, int fromIndex, int toIndex, T key, Comparator<? super T> c) {
        if(c == null)
            return binarySearch0(a, fromIndex, toIndex, key);
        int low = fromIndex;
        int high = toIndex - 1;
        while(low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = a[mid];
            int cmp = c.compare(midVal, key);
            if(cmp < 0)
                low = mid + 1;
            else if(cmp > 0)
                high = mid - 1;
            else
                return mid;
        }
        return -(low + 1);
    }

    public static boolean equals(long[] a, long[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(long[] a, int aFromIndex, int aToIndex, long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    public static boolean equals(int[] a, int[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(int[] a, int aFromIndex, int aToIndex, int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    public static boolean equals(short[] a, short a2[]) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(short[] a, int aFromIndex, int aToIndex, short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    @IntrinsicCandidate
    public static boolean equals(char[] a, char[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    @IntrinsicCandidate
    public static boolean equals(byte[] a, byte[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    public static boolean equals(boolean[] a, boolean[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(boolean[] a, int aFromIndex, int aToIndex, boolean[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    public static boolean equals(double[] a, double[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(double[] a, int aFromIndex, int aToIndex, double[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    public static boolean equals(float[] a, float[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        return ArraysSupport.mismatch(a, a2, length) < 0;
    }

    public static boolean equals(float[] a, int aFromIndex, int aToIndex, float[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        return ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, aLength) < 0;
    }

    public static boolean equals(Object[] a, Object[] a2) {
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        for(int i = 0; i < length; i++) {
            if(!Objects.equals(a[i], a2[i]))
                return false;
        }
        return true;
    }

    public static boolean equals(Object[] a, int aFromIndex, int aToIndex, Object[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        for(int i = 0; i < aLength; i++) {
            if(!Objects.equals(a[aFromIndex++], b[bFromIndex++]))
                return false;
        }
        return true;
    }

    public static <T> boolean equals(T[] a, T[] a2, Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        if(a == a2)
            return true;
        if(a == null || a2 == null)
            return false;
        int length = a.length;
        if(a2.length != length)
            return false;
        for(int i = 0; i < length; i++) {
            if(cmp.compare(a[i], a2[i]) != 0)
                return false;
        }
        return true;
    }

    public static <T> boolean equals(T[] a, int aFromIndex, int aToIndex, T[] b, int bFromIndex, int bToIndex, Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if(aLength != bLength)
            return false;
        for(int i = 0; i < aLength; i++) {
            if(cmp.compare(a[aFromIndex++], b[bFromIndex++]) != 0)
                return false;
        }
        return true;
    }

    public static void fill(long[] a, long val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(long[] a, int fromIndex, int toIndex, long val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(int[] a, int val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(int[] a, int fromIndex, int toIndex, int val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(short[] a, short val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(short[] a, int fromIndex, int toIndex, short val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(char[] a, char val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(char[] a, int fromIndex, int toIndex, char val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(byte[] a, byte val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(boolean[] a, boolean val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(boolean[] a, int fromIndex, int toIndex, boolean val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(double[] a, double val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(double[] a, int fromIndex, int toIndex,double val){
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(float[] a, float val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(float[] a, int fromIndex, int toIndex, float val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    public static void fill(Object[] a, Object val) {
        for(int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }

    public static void fill(Object[] a, int fromIndex, int toIndex, Object val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for(int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[])copyOf(original, newLength, original.getClass());
    }

    @IntrinsicCandidate
    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class) ? (T[]) new Object[newLength] : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static short[] copyOf(short[] original, int newLength) {
        short[] copy = new short[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static int[] copyOf(int[] original, int newLength) {
        int[] copy = new int[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static long[] copyOf(long[] original, int newLength) {
        long[] copy = new long[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static float[] copyOf(float[] original, int newLength) {
        float[] copy = new float[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static double[] copyOf(double[] original, int newLength) {
        double[] copy = new double[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    public static boolean[] copyOf(boolean[] original, int newLength) {
        boolean[] copy = new boolean[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return copyOfRange(original, from, to, (Class<? extends T[]>) original.getClass());
    }

    @IntrinsicCandidate
    public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static byte[] copyOfRange(byte[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static short[] copyOfRange(short[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        short[] copy = new short[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static int[] copyOfRange(int[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        int[] copy = new int[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static long[] copyOfRange(long[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        long[] copy = new long[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static char[] copyOfRange(char[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        char[] copy = new char[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static float[] copyOfRange(float[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        float[] copy = new float[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static double[] copyOfRange(double[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        double[] copy = new double[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    public static boolean[] copyOfRange(boolean[] original, int from, int to) {
        if(from == 0 || to == original.length)
            return original.clone();
        int newLength = to - from;
        if(newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        boolean[] copy = new boolean[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> asList(T... a) {
        return new ArrayList<>(a);
    }

    private static class ArrayList<E> extends AbstractList<E> implements RandomAccess {
        @SuppressWarnings("serial")
        private final E[] a;

        ArrayList(E[] array) {
            a = Objects.requireNonNull(array);
        }

        @Override
        public int size() {
            return a.length;
        }

        @Override
        public Object[] toArray() {
            return Arrays.copyOf(a, a.length, Object[].class);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            int size = size();
            if(a.length < size)
                return Arrays.copyOf(this.a, size, (Class<? extends T[]>) a.getClass());
            System.arraycopy(this.a, 0, a, 0, size);
            if(a.length > size)
                a[size] = null;
            return a;
        }

        @Override
        public E get(int index) {
            return a[index];
        }

        @Override
        public E set(int index, E element) {
            E oldValue = a[index];
            a[index] = element;
            return oldValue;
        }

        @Override
        public int indexOf(Object o) {
            E[] a = this.a;
            if(o == null) {
                for(int i = 0; i < a.length; i++) {
                    if(a[i] == null)
                        return i;
                }
            }
            else {
                for(int i = 0; i < a.length; i++) {
                    if(o.equals(a[i]))
                        return i;
                }
            }
            return -1;
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) >= 0;
        }

        @Override
        public Spliterator<E> spliterator() {
            return Spliterators.spliterator(a, Spliterator.ORDERED);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            for(E e : a)
                action.accept(e);
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            Objects.requireNonNull(operator);
            E[] a = this.a;
            for(int i = 0; i < a.length; i++)
                a[i] = operator.apply(a[i]);
        }

        @Override
        public void sort(Comparator<? super E> c) {
            Arrays.sort(a, c);
        }

        @Override
        public Iterator<E> iterator() {
            return new ArrayItr<>(a);
        }
    }

    private static class ArrayItr<E> implements Iterator<E> {
        private int cursor;
        private final E[] a;

        ArrayItr(E[] a) {
            this.a = a;
        }

        @Override
        public boolean hasNext() {
            return cursor < a.length;
        }

        @Override
        public E next() {
            int i = cursor;
            if(i >= a.length)
                throw new NoSuchElementException();
            cursor = i + 1;
            return a[i];
        }
    }

    public static int hashCode(long[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(long element : a) {
            int elementHash = (int)(element ^ (element >>> 32));
            result = 31 * result + elementHash;
        }
        return result;
    }

    public static int hashCode(int[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(int element : a)
            result = 31 * result + element;
        return result;
    }

    public static int hashCode(short[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(short element : a)
            result = 31 * result + element;
        return result;
    }

    public static int hashCode(char[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(char element : a)
            result = 31 * result + element;
        return result;
    }

    public static int hashCode(byte[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(byte element : a)
            result = 31 * result + element;
        return result;
    }

    public static int hashCode(boolean[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(boolean element : a)
            result = 31 * result + (element ? 1231 : 1237);
        return result;
    }

    public static int hashCode(float[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(float element : a)
            result = 31 * result + Float.floatToIntBits(element);
        return result;
    }

    public static int hashCode(double[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(double element : a) {
            long bits = Double.doubleToLongBits(element);
            result = 31 * result + (int)(bits ^ (bits >>> 32));
        }
        return result;
    }

    public static int hashCode(Object[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(Object element : a)
            result = 31 * result + (element == null ? 0 : element.hashCode());
        return result;
    }

    public static int deepHashCode(Object[] a) {
        if(a == null)
            return 0;
        int result = 1;
        for(Object element : a) {
            final int elementHash;
            final Class<?> cl;
            if(element == null)
                elementHash = 0;
            else if((cl = element.getClass().getComponentType()) == null)
                elementHash = element.hashCode();
            else if(element instanceof Object[])
                elementHash = deepHashCode((Object[]) element);
            else
                elementHash = primitiveArrayHashCode(element, cl);
            result = 31 * result + elementHash;
        }
        return result;
    }

    private static int primitiveArrayHashCode(Object a, Class<?> cl) {
        return
            (cl == byte.class) ? hashCode((byte[]) a) :
            (cl == int.class) ? hashCode((int[]) a) :
            (cl == long.class) ? hashCode((long[]) a) :
            (cl == char.class) ? hashCode((char[]) a) :
            (cl == short.class) ? hashCode((short[]) a) :
            (cl == boolean.class) ? hashCode((boolean[]) a) :
            (cl == double.class) ? hashCode((double[]) a) :
            hashCode((float[])a);
    }

    public static boolean deepEquals(Object[] a1, Object[] a2) {
        if(a1 == a2)
            return true;
        if(a1 == null || a2 == null)
            return false;
        int length = a1.length;
        if(a2.length != length)
            return false;
        for(int i = 0; i < length; i++) {
            Object e1 = a1[i];
            Object e2 = a2[i];
            if(e1 == e2)
                continue;
            if(e1 == null)
                return false;
            boolean eq = deepEquals0(e1, e2);
            if(!eq)
                return false;
        }
        return true;
    }

    static boolean deepEquals0(Object e1, Object e2) {
        if(e1 instanceof Object[] && e2 instanceof Object[])
            return deepEquals ((Object[])e1, (Object[])e2);
        else if(e1 instanceof byte[] && e2 instanceof byte[])
            return equals((byte[])e1, (byte[])e2);
        else if(e1 instanceof short[] && e2 instanceof short[])
            return equals((short[])e1, (short[])e2);
        else if(e1 instanceof int[] && e2 instanceof int[])
            return equals((int[])e1, (int[])e2);
        else if(e1 instanceof long[] && e2 instanceof long[])
            return equals((long[])e1, (long[])e2);
        else if(e1 instanceof char[] && e2 instanceof char[])
            return equals((char[])e1, (char[])e2);
        else if(e1 instanceof float[] && e2 instanceof float[])
            return equals((float[])e1, (float[])e2);
        else if(e1 instanceof double[] && e2 instanceof double[])
            return equals((double[])e1, (double[])e2);
        else if(e1 instanceof boolean[] && e2 instanceof boolean[])
            return equals((boolean[])e1, (boolean[])e2);
        else
            return e1.equals(e2);
    }

    public static String toString(long[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(int[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(short[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(char[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(byte[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(boolean[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(float[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(double[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(a[i]);
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    public static String toString(Object[] a) {
        if(a == null)
            return "null";
        int iMax = a.length - 1;
        if(iMax == -1)
            return "[]";
        StringBuilder b = new StringBuilder();
        b.append('[');
        for(int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if(i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    // public static String deepToString(Object[] a) {
    //     if(a == null)
    //         return "null";
    //     int bufLen = 20 * a.length;
    //     if(a.length != 0 && bufLen <= 0)
    //         bufLen = Integer.MAX_VALUE;
    //     StringBuilder buf = new StringBuilder(bufLen);
    //     deepToString(a, buf, new HashSet<>());
    //     return buf.toString();
    // }

    // private static void deepToString(Object[] a, StringBuilder buf, Set<Object[]> dejaVu) {
    //     if(a == null) {
    //         buf.append("null");
    //         return;
    //     }
    //     int iMax = a.length - 1;
    //     if(iMax == -1) {
    //         buf.append("[]");
    //         return;
    //     }
    //     dejaVu.add(a);
    //     buf.append('[');
    //     for(int i = 0; ; i++) {
    //         Object element = a[i];
    //         if(element == null)
    //             buf.append("null");
    //         else {
    //             Class<?> eClass = element.getClass();
    //             if(eClass.isArray()) {
    //                 if(eClass == byte[].class)
    //                     buf.append(toString((byte[]) element));
    //                 else if(eClass == short[].class)
    //                     buf.append(toString((short[]) element));
    //                 else if(eClass == int[].class)
    //                     buf.append(toString((int[]) element));
    //                 else if(eClass == long[].class)
    //                     buf.append(toString((long[]) element));
    //                 else if(eClass == char[].class)
    //                     buf.append(toString((char[]) element));
    //                 else if(eClass == float[].class)
    //                     buf.append(toString((float[]) element));
    //                 else if(eClass == double[].class)
    //                     buf.append(toString((double[]) element));
    //                 else if(eClass == boolean[].class)
    //                     buf.append(toString((boolean[]) element));
    //                 else {
    //                     if(dejaVu.contains(element))
    //                         buf.append("[...]");
    //                     else
    //                         deepToString((Object[])element, buf, dejaVu);
    //                 }
    //             }
    //             else
    //                 buf.append(element.toString());
    //         }
    //         if(i == iMax)
    //             break;
    //         buf.append(", ");
    //     }
    //     buf.append(']');
    //     dejaVu.remove(a);
    // }

    public static <T> void setAll(T[] array, IntFunction<? extends T> generator) {
        Objects.requireNonNull(generator);
        for(int i = 0; i < array.length; i++)
            array[i] = generator.apply(i);
    }

    // TODO
    // public static <T> void parallelSetAll(T[] array, IntFunction<? extends T> generator) {
    //     Objects.requireNonNull(generator);
    //     IntStream.range(0, array.length).parallel().forEach(i -> { array[i] = generator.apply(i); });
    // }

    public static void setAll(int[] array, IntUnaryOperator generator) {
        Objects.requireNonNull(generator);
        for(int i = 0; i < array.length; i++)
            array[i] = generator.applyAsInt(i);
    }

    // TODO
    // public static void parallelSetAll(int[] array, IntUnaryOperator generator) {
    //     Objects.requireNonNull(generator);
    //     IntStream.range(0, array.length).parallel().forEach(i -> { array[i] = generator.applyAsInt(i); });
    // }

    public static void setAll(long[] array, IntToLongFunction generator) {
        Objects.requireNonNull(generator);
        for(int i = 0; i < array.length; i++)
            array[i] = generator.applyAsLong(i);
    }

    // TODO
    // public static void parallelSetAll(long[] array, IntToLongFunction generator) {
    //     Objects.requireNonNull(generator);
    //     IntStream.range(0, array.length).parallel().forEach(i -> { array[i] = generator.applyAsLong(i); });
    // }

    public static void setAll(double[] array, IntToDoubleFunction generator) {
        Objects.requireNonNull(generator);
        for(int i = 0; i < array.length; i++)
            array[i] = generator.applyAsDouble(i);
    }

    // TODO
    // public static void parallelSetAll(double[] array, IntToDoubleFunction generator) {
    //     Objects.requireNonNull(generator);
    //     IntStream.range(0, array.length).parallel().forEach(i -> { array[i] = generator.applyAsDouble(i); });
    // }

    public static <T> Spliterator<T> spliterator(T[] array) {
        return Spliterators.spliterator(array, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static <T> Spliterator<T> spliterator(T[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static Spliterator.OfInt spliterator(int[] array) {
        return Spliterators.spliterator(array, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static Spliterator.OfInt spliterator(int[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static Spliterator.OfLong spliterator(long[] array) {
        return Spliterators.spliterator(array, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static Spliterator.OfLong spliterator(long[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static Spliterator.OfDouble spliterator(double[] array) {
        return Spliterators.spliterator(array, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    public static Spliterator.OfDouble spliterator(double[] array, int startInclusive, int endExclusive) {
        return Spliterators.spliterator(array, startInclusive, endExclusive, Spliterator.ORDERED | Spliterator.IMMUTABLE);
    }

    // TODO
    // public static <T> Stream<T> stream(T[] array) {
    //     return stream(array, 0, array.length);
    // }

    // TODO
    // public static <T> Stream<T> stream(T[] array, int startInclusive, int endExclusive) {
    //     return StreamSupport.stream(spliterator(array, startInclusive, endExclusive), false);
    // }

    // TODO
    // public static IntStream stream(int[] array) {
    //     return stream(array, 0, array.length);
    // }

    // TODO
    // public static IntStream stream(int[] array, int startInclusive, int endExclusive) {
    //     return StreamSupport.intStream(spliterator(array, startInclusive, endExclusive), false);
    // }

    // TODO
    // public static LongStream stream(long[] array) {
    //     return stream(array, 0, array.length);
    // }

    // TODO
    // public static LongStream stream(long[] array, int startInclusive, int endExclusive) {
    //     return StreamSupport.longStream(spliterator(array, startInclusive, endExclusive), false);
    // }

    // TODO
    // public static DoubleStream stream(double[] array) {
    //     return stream(array, 0, array.length);
    // }

    // TODO
    // public static DoubleStream stream(double[] array, int startInclusive, int endExclusive) {
    //     return StreamSupport.doubleStream(spliterator(array, startInclusive, endExclusive), false);
    // }

    public static int compare(boolean[] a, boolean[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Boolean.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(boolean[] a, int aFromIndex, int aToIndex, boolean[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Boolean.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(byte[] a, byte[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Byte.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Byte.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compareUnsigned(byte[] a, byte[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Byte.compareUnsigned(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compareUnsigned(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Byte.compareUnsigned(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(short[] a, short[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Short.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(short[] a, int aFromIndex, int aToIndex, short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Short.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compareUnsigned(short[] a, short[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Short.compareUnsigned(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compareUnsigned(short[] a, int aFromIndex, int aToIndex, short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Short.compareUnsigned(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(char[] a, char[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Character.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Character.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(int[] a, int[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Integer.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(int[] a, int aFromIndex, int aToIndex, int[] b, int bFromIndex, int bToIndex) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Integer.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compareUnsigned(int[] a, int[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Integer.compareUnsigned(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compareUnsigned(int[] a, int aFromIndex, int aToIndex, int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Integer.compareUnsigned(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(long[] a, long[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Long.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(long[] a, int aFromIndex, int aToIndex, long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Long.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compareUnsigned(long[] a, long[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Long.compareUnsigned(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compareUnsigned(long[] a, int aFromIndex, int aToIndex, long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Long.compareUnsigned(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(float[] a, float[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Float.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(float[] a, int aFromIndex, int aToIndex, float[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Float.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static int compare(double[] a, double[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int i = ArraysSupport.mismatch(a, b, Math.min(a.length, b.length));
        if(i >= 0)
            return Double.compare(a[i], b[i]);
        return a.length - b.length;
    }

    public static int compare(double[] a, int aFromIndex, int aToIndex, double[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, Math.min(aLength, bLength));
        if(i >= 0)
            return Double.compare(a[aFromIndex + i], b[bFromIndex + i]);
        return aLength - bLength;
    }

    public static <T extends Comparable<? super T>> int compare(T[] a, T[] b) {
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int length = Math.min(a.length, b.length);
        for(int i = 0; i < length; i++) {
            T oa = a[i];
            T ob = b[i];
            if(oa != ob) {
                if(oa == null || ob == null)
                    return oa == null ? -1 : 1;
                int v = oa.compareTo(ob);
                if(v != 0)
                    return v;
            }
        }
        return a.length - b.length;
    }

    public static <T extends Comparable<? super T>> int compare(T[] a, int aFromIndex, int aToIndex, T[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for(int i = 0; i < length; i++) {
            T oa = a[aFromIndex++];
            T ob = b[bFromIndex++];
            if(oa != ob) {
                if(oa == null || ob == null)
                    return oa == null ? -1 : 1;
                int v = oa.compareTo(ob);
                if(v != 0)
                    return v;
            }
        }
        return aLength - bLength;
    }

    public static <T> int compare(T[] a, T[] b, Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        if(a == b)
            return 0;
        if(a == null || b == null)
            return a == null ? -1 : 1;
        int length = Math.min(a.length, b.length);
        for(int i = 0; i < length; i++) {
            T oa = a[i];
            T ob = b[i];
            if(oa != ob) {
                int v = cmp.compare(oa, ob);
                if(v != 0)
                    return v;
            }
        }
        return a.length - b.length;
    }

    public static <T> int compare(T[] a, int aFromIndex, int aToIndex, T[] b, int bFromIndex, int bToIndex, Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for(int i = 0; i < length; i++) {
            T oa = a[aFromIndex++];
            T ob = b[bFromIndex++];
            if(oa != ob) {
                int v = cmp.compare(oa, ob);
                if(v != 0)
                    return v;
            }
        }
        return aLength - bLength;
    }

    public static int mismatch(boolean[] a, boolean[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(boolean[] a, int aFromIndex, int aToIndex, boolean[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(byte[] a, byte[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(byte[] a, int aFromIndex, int aToIndex, byte[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(char[] a, char[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(char[] a, int aFromIndex, int aToIndex, char[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(short[] a, short[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(short[] a, int aFromIndex, int aToIndex, short[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(int[] a, int[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(int[] a, int aFromIndex, int aToIndex, int[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(long[] a, long[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(long[] a, int aFromIndex, int aToIndex, long[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(float[] a, float[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(float[] a, int aFromIndex, int aToIndex, float[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(double[] a, double[] b) {
        int length = Math.min(a.length, b.length);
        if(a == b)
            return -1;
        int i = ArraysSupport.mismatch(a, b, length);
        return (i < 0 && a.length != b.length) ? length : i;
    }

    public static int mismatch(double[] a, int aFromIndex, int aToIndex, double[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        int i = ArraysSupport.mismatch(a, aFromIndex, b, bFromIndex, length);
        return (i < 0 && aLength != bLength) ? length : i;
    }

    public static int mismatch(Object[] a, Object[] b) {
        if(a == b)
            return -1;
        int length = Math.min(a.length, b.length);
        for(int i = 0; i < length; i++) {
            if(!Objects.equals(a[i], b[i]))
                return i;
        }
        return a.length != b.length ? length : -1;
    }

    public static int mismatch(Object[] a, int aFromIndex, int aToIndex, Object[] b, int bFromIndex, int bToIndex) {
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for(int i = 0; i < length; i++) {
            if(!Objects.equals(a[aFromIndex++], b[bFromIndex++]))
                return i;
        }
        return aLength != bLength ? length : -1;
    }

    public static <T> int mismatch(T[] a, T[] b, Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        if(a == b)
            return -1;
        int length = Math.min(a.length, b.length);
        for(int i = 0; i < length; i++) {
            T oa = a[i];
            T ob = b[i];
            if(oa != ob) {
                int v = cmp.compare(oa, ob);
                if(v != 0)
                    return i;
            }
        }
        return a.length != b.length ? length : -1;
    }

    public static <T> int mismatch(T[] a, int aFromIndex, int aToIndex, T[] b, int bFromIndex, int bToIndex, Comparator<? super T> cmp) {
        Objects.requireNonNull(cmp);
        rangeCheck(a.length, aFromIndex, aToIndex);
        rangeCheck(b.length, bFromIndex, bToIndex);
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        int length = Math.min(aLength, bLength);
        for(int i = 0; i < length; i++) {
            T oa = a[aFromIndex++];
            T ob = b[bFromIndex++];
            if(oa != ob) {
                int v = cmp.compare(oa, ob);
                if(v != 0)
                    return i;
            }
        }
        return aLength != bLength ? length : -1;
    }
}
