package java.util;

// import java.util.concurrent.CountedCompleter;
// import java.util.concurrent.RecursiveTask;

final class DualPivotQuicksort {
    private DualPivotQuicksort() {

    }

    private static final int MAX_MIXED_INSERTION_SORT_SIZE = 65;
    private static final int MAX_INSERTION_SORT_SIZE = 44;
    private static final int MIN_TRY_MERGE_SIZE = 4 << 10;
    private static final int MIN_FIRST_RUN_SIZE = 16;
    private static final int MIN_FIRST_RUNS_FACTOR = 7;
    private static final int MAX_RUN_CAPACITY = 5 << 10;
    private static final int MIN_BYTE_COUNTING_SORT_SIZE = 64;
    private static final int MIN_SHORT_OR_CHAR_COUNTING_SORT_SIZE = 1750;
    private static final int DELTA = 3 << 1;
    private static final int MAX_RECURSION_DEPTH = 64 * DELTA;

    private static final int NUM_SHORT_VALUES = 1 << 16;
    private static final int MAX_SHORT_INDEX = Short.MAX_VALUE + NUM_SHORT_VALUES + 1;

    private static final int NUM_CHAR_VALUES = 1 << 16;

    private static final int NUM_BYTE_VALUES = 1 << 8;
    private static final int MAX_BYTE_INDEX = Byte.MAX_VALUE + NUM_BYTE_VALUES + 1;

    static void sort(int[] a, int low, int high) {
        sort(a, 0, low, high);
    }

    static void sort(int[] a, int bits, int low, int high) {
        while(true) {
            int end = high - 1, size = high - low;
            if(size < MAX_MIXED_INSERTION_SORT_SIZE + bits && (bits & 1) > 0) {
                mixedInsertionSort(a, low, high - 3 * ((size >> 5) << 3), high);
                return;
            }
            if(size < MAX_INSERTION_SORT_SIZE) {
                insertionSort(a, low, high);
                return;
            }
            if((bits == 0 || size > MIN_TRY_MERGE_SIZE && (bits & 1) > 0) && tryMergeRuns(a, low, size))
                return;
            if((bits += DELTA) > MAX_RECURSION_DEPTH) {
                heapSort(a, low, high);
                return;
            }
            int step = (size >> 3) * 3 + 3;
            int e1 = low + step;
            int e5 = end - step;
            int e3 = (e1 + e5) >>> 1;
            int e2 = (e1 + e3) >>> 1;
            int e4 = (e3 + e5) >>> 1;
            int a3 = a[e3];
            if(a[e5] < a[e2]) { int t = a[e5]; a[e5] = a[e2]; a[e2] = t; }
            if(a[e4] < a[e1]) { int t = a[e4]; a[e4] = a[e1]; a[e1] = t; }
            if(a[e5] < a[e4]) { int t = a[e5]; a[e5] = a[e4]; a[e4] = t; }
            if(a[e2] < a[e1]) { int t = a[e2]; a[e2] = a[e1]; a[e1] = t; }
            if(a[e4] < a[e2]) { int t = a[e4]; a[e4] = a[e2]; a[e2] = t; }
            if(a3 < a[e2]) {
                if(a3 < a[e1]) {
                    a[e3] = a[e2];
                    a[e2] = a[e1];
                    a[e1] = a3;
                }
                else {
                    a[e3] = a[e2];
                    a[e2] = a3;
                }
            }
            else if(a3 > a[e4]) {
                if(a3 > a[e5]) {
                    a[e3] = a[e4];
                    a[e4] = a[e5];
                    a[e5] = a3;
                }
                else {
                    a[e3] = a[e4];
                    a[e4] = a3;
                }
            }
            int lower = low;
            int upper = end;
            if(a[e1] < a[e2] && a[e2] < a[e3] && a[e3] < a[e4] && a[e4] < a[e5]) {
                int pivot1 = a[e1];
                int pivot2 = a[e5];
                a[e1] = a[lower];
                a[e5] = a[upper];
                while(a[++lower] < pivot1);
                while(a[--upper] > pivot2);
                for(int unused = --lower, k = ++upper; --k > lower;) {
                    int ak = a[k];
                    if(ak < pivot1) {
                        while(lower < k) {
                            if(a[++lower] >= pivot1) {
                                if(a[lower] > pivot2) {
                                    a[k] = a[--upper];
                                    a[upper] = a[lower];
                                }
                                else
                                    a[k] = a[lower];
                                a[lower] = ak;
                                break;
                            }
                        }
                    }
                    else if(ak > pivot2) {
                        a[k] = a[--upper];
                        a[upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot1;
                a[end] = a[upper];
                a[upper] = pivot2;
                sort(a, bits | 1, lower + 1, upper);
                sort(a, bits | 1, upper + 1, high);
            }
            else {
                int pivot = a[e3];
                a[e3] = a[lower];
                for(int k = ++upper; --k > lower;) {
                    int ak = a[k];
                    if(ak != pivot) {
                        a[k] = pivot;
                        if(ak < pivot) {
                            while(a[++lower] < pivot);
                            if(a[lower] > pivot)
                                a[--upper] = a[lower];
                            a[lower] = ak;
                        }
                        else
                            a[--upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot;
                sort(a, bits | 1, upper, high);
            }
            high = lower;
        }
    }

    private static void mixedInsertionSort(int[] a, int low, int end, int high) {
        if(end == high) {
            for(int i; ++low < end;) {
                int ai = a[i = low];
                while(ai < a[--i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
        else {
            int pin = a[end];
            for(int i, p = high; ++low < end;) {
                int ai = a[i = low];
                if(ai < a[i - 1]) {
                    a[i] = a[--i];
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
                else if(p > i && ai > pin) {
                    while(a[--p] > pin);
                    if(p > i) {
                        ai = a[p];
                        a[p] = a[i];
                    }
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
            }
            for(int i; low < high; ++low) {
                int a1 = a[i = low], a2 = a[++low];
                if(a1 > a2) {
                    while(a1 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a1;
                    while(a2 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a2;
                }
                else if(a1 < a[i - 1]) {
                    while(a2 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a2;
                    while(a1 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a1;
                }
            }
        }
    }

    private static void insertionSort(int[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            int ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void heapSort(int[] a, int low, int high) {
        for(int k = (low + high) >>> 1; k > low;)
            pushDown(a, --k, a[k], low, high);
        while(--high > low) {
            int max = a[low];
            pushDown(a, low, a[high], low, high);
            a[high] = max;
        }
    }

    private static void pushDown(int[] a, int p, int value, int low, int high) {
        for(int k;; a[p] = a[p = k]) {
            k = (p << 1) - low + 2;
            if(k > high)
                break;
            if(k == high || a[k] < a[k - 1])
                --k;
            if(a[k] <= value)
                break;
        }
        a[p] = value;
    }

    private static boolean tryMergeRuns(int[] a, int low, int size) {
        int[] run = null;
        int high = low + size;
        int count = 1, last = low;
        for(int k = low + 1; k < high;) {
            if(a[k - 1] < a[k])
                while(++k < high && a[k - 1] <= a[k]);
            else if(a[k - 1] > a[k]) {
                while(++k < high && a[k - 1] >= a[k]);
                for(int i = last - 1, j = k; ++i < --j && a[i] > a[j];) {
                    int ai = a[i];
                    a[i] = a[j];
                    a[j] = ai;
                }
            }
            else {
                for(int ak = a[k]; ++k < high && ak == a[k];);
                if(k < high)
                    continue;
            }
            if(run == null) {
                if(k == high)
                    return true;
                if(k - low < MIN_FIRST_RUN_SIZE)
                    return false;
                run = new int[((size >> 10) | 0x7F) & 0x3FF];
                run[0] = low;
            }
            else if(a[last - 1] > a[last]) {
                if(count > (k - low) >> MIN_FIRST_RUNS_FACTOR)
                    return false;
                if(++count == MAX_RUN_CAPACITY)
                    return false;
                if(count == run.length)
                    run = Arrays.copyOf(run, count << 1);
            }
            run[count] = (last = k);
        }
        if(count > 1) {
            int[] b = new int[size];
            mergeRuns(a, b, low, 1, run, 0, count);
        }
        return true;
    }

    private static int[] mergeRuns(int[] a, int[] b, int offset, int aim, int[] run, int lo, int hi) {
        if(hi - lo == 1) {
            if(aim >= 0)
                return a;
            for(int i = run[hi], j = i - offset, low = run[lo]; i > low; b[--j] = a[--i]);
            return b;
        }
        int mi = lo, rmi = (run[lo] + run[hi]) >>> 1;
        while(run[++mi + 1] <= rmi);
        int[] a1 = mergeRuns(a, b, offset, -aim, run, lo, mi);
        int[] a2 = mergeRuns(a, b, offset, 0, run, mi, hi);
        int[] dst = a1 == a ? b : a;
        int k = a1 == a ? run[lo] - offset : run[lo];
        int lo1 = a1 == b ? run[lo] - offset : run[lo];
        int hi1 = a1 == b ? run[mi] - offset : run[mi];
        int lo2 = a2 == b ? run[mi] - offset : run[mi];
        int hi2 = a2 == b ? run[hi] - offset : run[hi];
        mergeParts(dst, k, a1, lo1, hi1, a2, lo2, hi2);
        return dst;
    }

    private static void mergeParts(int[] dst, int k, int[] a1, int lo1, int hi1, int[] a2, int lo2, int hi2) {
        while(lo1 < hi1 && lo2 < hi2)
            dst[k++] = a1[lo1] < a2[lo2] ? a1[lo1++] : a2[lo2++];
        if(dst != a1 || k < lo1) {
            while(lo1 < hi1)
                dst[k++] = a1[lo1++];
        }
        if(dst != a2 || k < lo2) {
            while(lo2 < hi2)
                dst[k++] = a2[lo2++];
        }
    }

    static void sort(long[] a, int low, int high) {
        sort(a, 0, low, high);
    }

    static void sort(long[] a, int bits, int low, int high) {
        while(true) {
            int end = high - 1, size = high - low;
            if(size < MAX_MIXED_INSERTION_SORT_SIZE + bits && (bits & 1) > 0) {
                mixedInsertionSort(a, low, high - 3 * ((size >> 5) << 3), high);
                return;
            }
            if(size < MAX_INSERTION_SORT_SIZE) {
                insertionSort(a, low, high);
                return;
            }
            if((bits == 0 || size > MIN_TRY_MERGE_SIZE && (bits & 1) > 0) && tryMergeRuns(a, low, size))
                return;
            if((bits += DELTA) > MAX_RECURSION_DEPTH) {
                heapSort(a, low, high);
                return;
            }
            int step = (size >> 3) * 3 + 3;
            int e1 = low + step;
            int e5 = end - step;
            int e3 = (e1 + e5) >>> 1;
            int e2 = (e1 + e3) >>> 1;
            int e4 = (e3 + e5) >>> 1;
            long a3 = a[e3];
            if(a[e5] < a[e2]) { long t = a[e5]; a[e5] = a[e2]; a[e2] = t; }
            if(a[e4] < a[e1]) { long t = a[e4]; a[e4] = a[e1]; a[e1] = t; }
            if(a[e5] < a[e4]) { long t = a[e5]; a[e5] = a[e4]; a[e4] = t; }
            if(a[e2] < a[e1]) { long t = a[e2]; a[e2] = a[e1]; a[e1] = t; }
            if(a[e4] < a[e2]) { long t = a[e4]; a[e4] = a[e2]; a[e2] = t; }
            if(a3 < a[e2]) {
                if(a3 < a[e1]) {
                    a[e3] = a[e2];
                    a[e2] = a[e1];
                    a[e1] = a3;
                }
                else {
                    a[e3] = a[e2];
                    a[e2] = a3;
                }
            }
            else if(a3 > a[e4]) {
                if(a3 > a[e5]) {
                    a[e3] = a[e4];
                    a[e4] = a[e5];
                    a[e5] = a3;
                }
                else {
                    a[e3] = a[e4];
                    a[e4] = a3;
                }
            }
            int lower = low;
            int upper = end;
            if(a[e1] < a[e2] && a[e2] < a[e3] && a[e3] < a[e4] && a[e4] < a[e5]) {
                long pivot1 = a[e1];
                long pivot2 = a[e5];
                a[e1] = a[lower];
                a[e5] = a[upper];
                while(a[++lower] < pivot1);
                while(a[--upper] > pivot2);
                for(int unused = --lower, k = ++upper; --k > lower;) {
                    long ak = a[k];
                    if(ak < pivot1) {
                        while(lower < k) {
                            if(a[++lower] >= pivot1) {
                                if(a[lower] > pivot2) {
                                    a[k] = a[--upper];
                                    a[upper] = a[lower];
                                }
                                else
                                    a[k] = a[lower];
                                a[lower] = ak;
                                break;
                            }
                        }
                    }
                    else if(ak > pivot2) {
                        a[k] = a[--upper];
                        a[upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot1;
                a[end] = a[upper];
                a[upper] = pivot2;
                sort(a, bits | 1, lower + 1, upper);
                sort(a, bits | 1, upper + 1, high);
            }
            else {
                long pivot = a[e3];
                a[e3] = a[lower];
                for(int k = ++upper; --k > lower;) {
                    long ak = a[k];
                    if(ak != pivot) {
                        a[k] = pivot;
                        if(ak < pivot) {
                            while(a[++lower] < pivot);
                            if(a[lower] > pivot)
                                a[--upper] = a[lower];
                            a[lower] = ak;
                        }
                        else
                            a[--upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot;
                sort(a, bits | 1, upper, high);
            }
            high = lower;
        }
    }

    private static void mixedInsertionSort(long[] a, int low, int end, int high) {
        if(end == high) {
            for(int i; ++low < end;) {
                long ai = a[i = low];
                while(ai < a[--i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
        else {
            long pin = a[end];
            for(int i, p = high; ++low < end;) {
                long ai = a[i = low];
                if(ai < a[i - 1]) {
                    a[i] = a[--i];
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
                else if(p > i && ai > pin) {
                    while(a[--p] > pin);
                    if(p > i) {
                        ai = a[p];
                        a[p] = a[i];
                    }
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
            }
            for(int i; low < high; ++low) {
                long a1 = a[i = low], a2 = a[++low];
                if(a1 > a2) {
                    while(a1 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a1;
                    while(a2 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a2;
                }
                else if(a1 < a[i - 1]) {
                    while(a2 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a2;
                    while(a1 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a1;
                }
            }
        }
    }

    private static void insertionSort(long[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            long ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void heapSort(long[] a, int low, int high) {
        for(int k = (low + high) >>> 1; k > low;)
            pushDown(a, --k, a[k], low, high);
        while(--high > low) {
            long max = a[low];
            pushDown(a, low, a[high], low, high);
            a[high] = max;
        }
    }

    private static void pushDown(long[] a, int p, long value, int low, int high) {
        for(int k;; a[p] = a[p = k]) {
            k = (p << 1) - low + 2;
            if(k > high)
                break;
            if(k == high || a[k] < a[k - 1])
                --k;
            if(a[k] <= value)
                break;
        }
        a[p] = value;
    }

    private static boolean tryMergeRuns(long[] a, int low, int size) {
        int[] run = null;
        int high = low + size;
        int count = 1, last = low;
        for(int k = low + 1; k < high;) {
            if(a[k - 1] < a[k])
                while(++k < high && a[k - 1] <= a[k]);
            else if(a[k - 1] > a[k]) {
                while(++k < high && a[k - 1] >= a[k]);
                for(int i = last - 1, j = k; ++i < --j && a[i] > a[j];) {
                    long ai = a[i];
                    a[i] = a[j];
                    a[j] = ai;
                }
            }
            else {
                for(long ak = a[k]; ++k < high && ak == a[k];);
                if(k < high)
                    continue;
            }
            if(run == null) {
                if(k == high)
                    return true;
                if(k - low < MIN_FIRST_RUN_SIZE)
                    return false;
                run = new int[((size >> 10) | 0x7F) & 0x3FF];
                run[0] = low;
            }
            else if(a[last - 1] > a[last]) {
                if(count > (k - low) >> MIN_FIRST_RUNS_FACTOR)
                    return false;
                if(++count == MAX_RUN_CAPACITY)
                    return false;
                if(count == run.length)
                    run = Arrays.copyOf(run, count << 1);
            }
            run[count] = (last = k);
        }
        if(count > 1) {
            long[] b = new long[size];
            mergeRuns(a, b, low, 1, run, 0, count);
        }
        return true;
    }

    private static long[] mergeRuns(long[] a, long[] b, int offset, int aim, int[] run, int lo, int hi) {
        if(hi - lo == 1) {
            if(aim >= 0)
                return a;
            for(int i = run[hi], j = i - offset, low = run[lo]; i > low; b[--j] = a[--i]);
            return b;
        }
        int mi = lo, rmi = (run[lo] + run[hi]) >>> 1;
        while(run[++mi + 1] <= rmi);
        long[] a1 = mergeRuns(a, b, offset, -aim, run, lo, mi);
        long[] a2 = mergeRuns(a, b, offset, 0, run, mi, hi);
        long[] dst = a1 == a ? b : a;
        int k = a1 == a ? run[lo] - offset : run[lo];
        int lo1 = a1 == b ? run[lo] - offset : run[lo];
        int hi1 = a1 == b ? run[mi] - offset : run[mi];
        int lo2 = a2 == b ? run[mi] - offset : run[mi];
        int hi2 = a2 == b ? run[hi] - offset : run[hi];
        mergeParts(dst, k, a1, lo1, hi1, a2, lo2, hi2);
        return dst;
    }

    private static void mergeParts(long[] dst, int k, long[] a1, int lo1, int hi1, long[] a2, int lo2, int hi2) {
        while(lo1 < hi1 && lo2 < hi2)
            dst[k++] = a1[lo1] < a2[lo2] ? a1[lo1++] : a2[lo2++];
        if(dst != a1 || k < lo1) {
            while(lo1 < hi1)
                dst[k++] = a1[lo1++];
        }
        if(dst != a2 || k < lo2) {
            while(lo2 < hi2)
                dst[k++] = a2[lo2++];
        }
    }

    static void sort(short[] a, int low, int high) {
        if(high - low > MIN_SHORT_OR_CHAR_COUNTING_SORT_SIZE)
            countingSort(a, low, high);
        else
            sort(a, 0, low, high);
    }

    static void sort(short[] a, int bits, int low, int high) {
        while(true) {
            int end = high - 1, size = high - low;
            if(size < MAX_INSERTION_SORT_SIZE) {
                insertionSort(a, low, high);
                return;
            }
            if((bits += DELTA) > MAX_RECURSION_DEPTH) {
                countingSort(a, low, high);
                return;
            }
            int step = (size >> 3) * 3 + 3;
            int e1 = low + step;
            int e5 = end - step;
            int e3 = (e1 + e5) >>> 1;
            int e2 = (e1 + e3) >>> 1;
            int e4 = (e3 + e5) >>> 1;
            short a3 = a[e3];
            if(a[e5] < a[e2]) { short t = a[e5]; a[e5] = a[e2]; a[e2] = t; }
            if(a[e4] < a[e1]) { short t = a[e4]; a[e4] = a[e1]; a[e1] = t; }
            if(a[e5] < a[e4]) { short t = a[e5]; a[e5] = a[e4]; a[e4] = t; }
            if(a[e2] < a[e1]) { short t = a[e2]; a[e2] = a[e1]; a[e1] = t; }
            if(a[e4] < a[e2]) { short t = a[e4]; a[e4] = a[e2]; a[e2] = t; }
            if(a3 < a[e2]) {
                if(a3 < a[e1]) {
                    a[e3] = a[e2];
                    a[e2] = a[e1];
                    a[e1] = a3;
                }
                else {
                    a[e3] = a[e2];
                    a[e2] = a3;
                }
            }
            else if(a3 > a[e4]) {
                if(a3 > a[e5]) {
                    a[e3] = a[e4];
                    a[e4] = a[e5];
                    a[e5] = a3;
                }
                else {
                    a[e3] = a[e4];
                    a[e4] = a3;
                }
            }
            int lower = low;
            int upper = end;
            if(a[e1] < a[e2] && a[e2] < a[e3] && a[e3] < a[e4] && a[e4] < a[e5]) {
                short pivot1 = a[e1];
                short pivot2 = a[e5];
                a[e1] = a[lower];
                a[e5] = a[upper];
                while(a[++lower] < pivot1);
                while(a[--upper] > pivot2);
                for(int unused = --lower, k = ++upper; --k > lower;) {
                    short ak = a[k];

                    if(ak < pivot1) {
                        while(lower < k) {
                            if(a[++lower] >= pivot1) {
                                if(a[lower] > pivot2) {
                                    a[k] = a[--upper];
                                    a[upper] = a[lower];
                                }
                                else
                                    a[k] = a[lower];
                                a[lower] = ak;
                                break;
                            }
                        }
                    }
                    else if(ak > pivot2) {
                        a[k] = a[--upper];
                        a[upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot1;
                a[end] = a[upper];
                a[upper] = pivot2;
                sort(a, bits | 1, lower + 1, upper);
                sort(a, bits | 1, upper + 1, high);
            }
            else {
                short pivot = a[e3];
                a[e3] = a[lower];
                for(int k = ++upper; --k > lower;) {
                    short ak = a[k];
                    if(ak != pivot) {
                        a[k] = pivot;
                        if(ak < pivot) {
                            while(a[++lower] < pivot);
                            if(a[lower] > pivot)
                                a[--upper] = a[lower];
                            a[lower] = ak;
                        }
                        else
                            a[--upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot;
                sort(a, bits | 1, upper, high);
            }
            high = lower;
        }
    }

    private static void insertionSort(short[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            short ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void countingSort(short[] a, int low, int high) {
        int[] count = new int[NUM_SHORT_VALUES];
        for(int i = high; i > low; ++count[a[--i] & 0xFFFF]);
        if(high - low > NUM_SHORT_VALUES) {
            for(int i = MAX_SHORT_INDEX; --i > Short.MAX_VALUE;) {
                int value = i & 0xFFFF;
                for(low = high - count[value]; high > low; a[--high] = (short)value);
            }
        }
        else {
            for(int i = MAX_SHORT_INDEX; high > low;) {
                while(count[--i & 0xFFFF] == 0);
                int value = i & 0xFFFF;
                int c = count[value];
                do {
                    a[--high] = (short)value;
                } while(--c > 0);
            }
        }
    }

    static void sort(char[] a, int low, int high) {
        if(high - low > MIN_SHORT_OR_CHAR_COUNTING_SORT_SIZE)
            countingSort(a, low, high);
        else
            sort(a, 0, low, high);
    }

    static void sort(char[] a, int bits, int low, int high) {
        while(true) {
            int end = high - 1, size = high - low;
            if(size < MAX_INSERTION_SORT_SIZE) {
                insertionSort(a, low, high);
                return;
            }
            if((bits += DELTA) > MAX_RECURSION_DEPTH) {
                countingSort(a, low, high);
                return;
            }
            int step = (size >> 3) * 3 + 3;
            int e1 = low + step;
            int e5 = end - step;
            int e3 = (e1 + e5) >>> 1;
            int e2 = (e1 + e3) >>> 1;
            int e4 = (e3 + e5) >>> 1;
            char a3 = a[e3];
            if(a[e5] < a[e2]) { char t = a[e5]; a[e5] = a[e2]; a[e2] = t; }
            if(a[e4] < a[e1]) { char t = a[e4]; a[e4] = a[e1]; a[e1] = t; }
            if(a[e5] < a[e4]) { char t = a[e5]; a[e5] = a[e4]; a[e4] = t; }
            if(a[e2] < a[e1]) { char t = a[e2]; a[e2] = a[e1]; a[e1] = t; }
            if(a[e4] < a[e2]) { char t = a[e4]; a[e4] = a[e2]; a[e2] = t; }
            if(a3 < a[e2]) {
                if(a3 < a[e1]) {
                    a[e3] = a[e2];
                    a[e2] = a[e1];
                    a[e1] = a3;
                }
                else {
                    a[e3] = a[e2];
                    a[e2] = a3;
                }
            }
            else if(a3 > a[e4]) {
                if(a3 > a[e5]) {
                    a[e3] = a[e4];
                    a[e4] = a[e5];
                    a[e5] = a3;
                }
                else {
                    a[e3] = a[e4];
                    a[e4] = a3;
                }
            }
            int lower = low;
            int upper = end;
            if(a[e1] < a[e2] && a[e2] < a[e3] && a[e3] < a[e4] && a[e4] < a[e5]) {
                char pivot1 = a[e1];
                char pivot2 = a[e5];
                a[e1] = a[lower];
                a[e5] = a[upper];
                while(a[++lower] < pivot1);
                while(a[--upper] > pivot2);
                for(int unused = --lower, k = ++upper; --k > lower;) {
                    char ak = a[k];
                    if(ak < pivot1) {
                        while(lower < k) {
                            if(a[++lower] >= pivot1) {
                                if(a[lower] > pivot2) {
                                    a[k] = a[--upper];
                                    a[upper] = a[lower];
                                }
                                else
                                    a[k] = a[lower];
                                a[lower] = ak;
                                break;
                            }
                        }
                    }
                    else if(ak > pivot2) {
                        a[k] = a[--upper];
                        a[upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot1;
                a[end] = a[upper];
                a[upper] = pivot2;
                sort(a, bits | 1, lower + 1, upper);
                sort(a, bits | 1, upper + 1, high);
            }
            else {
                char pivot = a[e3];
                a[e3] = a[lower];
                for(int k = ++upper; --k > lower;) {
                    char ak = a[k];
                    if(ak != pivot) {
                        a[k] = pivot;
                        if(ak < pivot) {
                            while(a[++lower] < pivot);
                            if(a[lower] > pivot)
                                a[--upper] = a[lower];
                            a[lower] = ak;
                        }
                        else
                            a[--upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot;
                sort(a, bits | 1, upper, high);
            }
            high = lower;
        }
    }

    private static void insertionSort(char[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            char ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void countingSort(char[] a, int low, int high) {
        int[] count = new int[NUM_CHAR_VALUES];
        for(int i = high; i > low; ++count[a[--i]]);
        if(high - low > NUM_CHAR_VALUES) {
            for(int i = NUM_CHAR_VALUES; i > 0;)
                for(low = high - count[--i]; high > low; a[--high] = (char)i);
        }
        else {
            for(int i = NUM_CHAR_VALUES; high > low;) {
                while(count[--i] == 0);
                int c = count[i];
                do {
                    a[--high] = (char)i;
                } while(--c > 0);
            }
        }
    }

    static void sort(byte[] a, int low, int high) {
        if(high - low > MIN_BYTE_COUNTING_SORT_SIZE)
            countingSort(a, low, high);
        else
            insertionSort(a, low, high);
    }

    private static void insertionSort(byte[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            byte ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void countingSort(byte[] a, int low, int high) {
        int[] count = new int[NUM_BYTE_VALUES];
        for(int i = high; i > low; ++count[a[--i] & 0xFF]);
        if(high - low > NUM_BYTE_VALUES) {
            for(int i = MAX_BYTE_INDEX; --i > Byte.MAX_VALUE;) {
                int value = i & 0xFF;
                for(low = high - count[value]; high > low; a[--high] = (byte)value);
            }
        }
        else {
            for(int i = MAX_BYTE_INDEX; high > low;) {
                while(count[--i & 0xFF] == 0);

                int value = i & 0xFF;
                int c = count[value];

                do {
                    a[--high] = (byte)value;
                } while(--c > 0);
            }
        }
    }

    static void sort(float[] a, int low, int high) {
        int numNegativeZero = 0;
        for(int k = high; k > low;) {
            float ak = a[--k];
            if(ak == 0.0f && Float.floatToRawIntBits(ak) < 0) {
                numNegativeZero += 1;
                a[k] = 0.0f;
            }
            else if(ak != ak) {
                a[k] = a[--high];
                a[high] = ak;
            }
        }
        sort(a, 0, low, high);
        if(++numNegativeZero == 1)
            return;
        while(low <= high) {
            int middle = (low + high) >>> 1;
            if(a[middle] < 0)
                low = middle + 1;
            else
                high = middle - 1;
        }
        while(--numNegativeZero > 0)
            a[++high] = -0.0f;
    }

    static void sort(float[] a, int bits, int low, int high) {
        while(true) {
            int end = high - 1, size = high - low;
            if(size < MAX_MIXED_INSERTION_SORT_SIZE + bits && (bits & 1) > 0) {
                mixedInsertionSort(a, low, high - 3 * ((size >> 5) << 3), high);
                return;
            }
            if(size < MAX_INSERTION_SORT_SIZE) {
                insertionSort(a, low, high);
                return;
            }
            if((bits == 0 || size > MIN_TRY_MERGE_SIZE && (bits & 1) > 0) && tryMergeRuns(a, low, size))
                return;
            if((bits += DELTA) > MAX_RECURSION_DEPTH) {
                heapSort(a, low, high);
                return;
            }
            int step = (size >> 3) * 3 + 3;
            int e1 = low + step;
            int e5 = end - step;
            int e3 = (e1 + e5) >>> 1;
            int e2 = (e1 + e3) >>> 1;
            int e4 = (e3 + e5) >>> 1;
            float a3 = a[e3];
            if(a[e5] < a[e2]) { float t = a[e5]; a[e5] = a[e2]; a[e2] = t; }
            if(a[e4] < a[e1]) { float t = a[e4]; a[e4] = a[e1]; a[e1] = t; }
            if(a[e5] < a[e4]) { float t = a[e5]; a[e5] = a[e4]; a[e4] = t; }
            if(a[e2] < a[e1]) { float t = a[e2]; a[e2] = a[e1]; a[e1] = t; }
            if(a[e4] < a[e2]) { float t = a[e4]; a[e4] = a[e2]; a[e2] = t; }
            if(a3 < a[e2]) {
                if(a3 < a[e1]) {
                    a[e3] = a[e2];
                    a[e2] = a[e1];
                    a[e1] = a3;
                }
                else {
                    a[e3] = a[e2];
                    a[e2] = a3;
                }
            }
            else if(a3 > a[e4]) {
                if(a3 > a[e5]) {
                    a[e3] = a[e4];
                    a[e4] = a[e5];
                    a[e5] = a3;
                }
                else {
                    a[e3] = a[e4];
                    a[e4] = a3;
                }
            }
            int lower = low;
            int upper = end;
            if(a[e1] < a[e2] && a[e2] < a[e3] && a[e3] < a[e4] && a[e4] < a[e5]) {
                float pivot1 = a[e1];
                float pivot2 = a[e5];
                a[e1] = a[lower];
                a[e5] = a[upper];
                while(a[++lower] < pivot1);
                while(a[--upper] > pivot2);
                for(int unused = --lower, k = ++upper; --k > lower;) {
                    float ak = a[k];
                    if(ak < pivot1) {
                        while(lower < k) {
                            if(a[++lower] >= pivot1) {
                                if(a[lower] > pivot2) {
                                    a[k] = a[--upper];
                                    a[upper] = a[lower];
                                }
                                else
                                    a[k] = a[lower];
                                a[lower] = ak;
                                break;
                            }
                        }
                    }
                    else if(ak > pivot2) {
                        a[k] = a[--upper];
                        a[upper] = ak;
                    }
                }
                a[low] = a[lower]; a[lower] = pivot1;
                a[end] = a[upper]; a[upper] = pivot2;
                sort(a, bits | 1, lower + 1, upper);
                sort(a, bits | 1, upper + 1, high);
            }
            else {
                float pivot = a[e3];
                a[e3] = a[lower];
                for(int k = ++upper; --k > lower;) {
                    float ak = a[k];
                    if(ak != pivot) {
                        a[k] = pivot;
                        if(ak < pivot) {
                            while(a[++lower] < pivot);
                            if(a[lower] > pivot)
                                a[--upper] = a[lower];
                            a[lower] = ak;
                        }
                        else
                            a[--upper] = ak;
                    }
                }
                a[low] = a[lower]; a[lower] = pivot;
                sort(a, bits | 1, upper, high);
            }
            high = lower;
        }
    }

    private static void mixedInsertionSort(float[] a, int low, int end, int high) {
        if(end == high) {
            for(int i; ++low < end;) {
                float ai = a[i = low];
                while(ai < a[--i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
        else {
            float pin = a[end];
            for(int i, p = high; ++low < end;) {
                float ai = a[i = low];
                if(ai < a[i - 1]) {
                    a[i] = a[--i];
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
                else if(p > i && ai > pin) {
                    while(a[--p] > pin);
                    if(p > i) {
                        ai = a[p];
                        a[p] = a[i];
                    }
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
            }
            for(int i; low < high; ++low) {
                float a1 = a[i = low], a2 = a[++low];
                if(a1 > a2) {
                    while(a1 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a1;
                    while(a2 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a2;
                }
                else if(a1 < a[i - 1]) {
                    while(a2 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a2;
                    while(a1 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a1;
                }
            }
        }
    }

    private static void insertionSort(float[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            float ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void heapSort(float[] a, int low, int high) {
        for(int k = (low + high) >>> 1; k > low;)
            pushDown(a, --k, a[k], low, high);
        while(--high > low) {
            float max = a[low];
            pushDown(a, low, a[high], low, high);
            a[high] = max;
        }
    }

    private static void pushDown(float[] a, int p, float value, int low, int high) {
        for(int k ;; a[p] = a[p = k]) {
            k = (p << 1) - low + 2;
            if(k > high)
                break;
            if(k == high || a[k] < a[k - 1])
                --k;
            if(a[k] <= value)
                break;
        }
        a[p] = value;
    }

    private static boolean tryMergeRuns(float[] a, int low, int size) {
        int[] run = null;
        int high = low + size;
        int count = 1, last = low;
        for(int k = low + 1; k < high;) {
            if(a[k - 1] < a[k])
                while(++k < high && a[k - 1] <= a[k]);
            else if(a[k - 1] > a[k]) {
                while(++k < high && a[k - 1] >= a[k]);
                for(int i = last - 1, j = k; ++i < --j && a[i] > a[j];) {
                    float ai = a[i];
                    a[i] = a[j];
                    a[j] = ai;
                }
            }
            else {
                for(float ak = a[k]; ++k < high && ak == a[k];);
                if(k < high)
                    continue;
            }

            if(run == null) {
                if(k == high)
                    return true;
                if(k - low < MIN_FIRST_RUN_SIZE)
                    return false;
                run = new int[((size >> 10) | 0x7F) & 0x3FF];
                run[0] = low;
            }
            else if(a[last - 1] > a[last]) {
                if(count > (k - low) >> MIN_FIRST_RUNS_FACTOR)
                    return false;
                if(++count == MAX_RUN_CAPACITY)
                    return false;
                if(count == run.length)
                    run = Arrays.copyOf(run, count << 1);
            }
            run[count] = (last = k);
        }
        if(count > 1) {
            float[] b = new float[size];
            mergeRuns(a, b, low, 1, run, 0, count);
        }
        return true;
    }

    private static float[] mergeRuns(float[] a, float[] b, int offset, int aim, int[] run, int lo, int hi) {
        if(hi - lo == 1) {
            if(aim >= 0)
                return a;
            for(int i = run[hi], j = i - offset, low = run[lo]; i > low; b[--j] = a[--i]);
            return b;
        }
        int mi = lo, rmi = (run[lo] + run[hi]) >>> 1;
        while(run[++mi + 1] <= rmi);
        float[] a1 = mergeRuns(a, b, offset, -aim, run, lo, mi);
        float[] a2 = mergeRuns(a, b, offset, 0, run, mi, hi);
        float[] dst = a1 == a ? b : a;
        int k = a1 == a ? run[lo] - offset : run[lo];
        int lo1 = a1 == b ? run[lo] - offset : run[lo];
        int hi1 = a1 == b ? run[mi] - offset : run[mi];
        int lo2 = a2 == b ? run[mi] - offset : run[mi];
        int hi2 = a2 == b ? run[hi] - offset : run[hi];
        mergeParts(dst, k, a1, lo1, hi1, a2, lo2, hi2);
        return dst;
    }

    private static void mergeParts(float[] dst, int k, float[] a1, int lo1, int hi1, float[] a2, int lo2, int hi2) {
        while(lo1 < hi1 && lo2 < hi2)
            dst[k++] = a1[lo1] < a2[lo2] ? a1[lo1++] : a2[lo2++];
        if(dst != a1 || k < lo1) {
            while(lo1 < hi1)
                dst[k++] = a1[lo1++];
        }
        if(dst != a2 || k < lo2) {
            while(lo2 < hi2)
                dst[k++] = a2[lo2++];
        }
    }

    static void sort(double[] a, int low, int high) {
        int numNegativeZero = 0;
        for(int k = high; k > low; ) {
            double ak = a[--k];
            if(ak == 0.0d && Double.doubleToRawLongBits(ak) < 0) {
                numNegativeZero += 1;
                a[k] = 0.0d;
            }
            else if(ak != ak) {
                a[k] = a[--high];
                a[high] = ak;
            }
        }
        int size = high - low;
        sort(a, 0, low, high);
        if(++numNegativeZero == 1)
            return;
        while(low <= high) {
            int middle = (low + high) >>> 1;
            if(a[middle] < 0)
                low = middle + 1;
            else
                high = middle - 1;
        }
        while(--numNegativeZero > 0)
            a[++high] = -0.0d;
    }

    static void sort(double[] a, int bits, int low, int high) {
        while(true) {
            int end = high - 1, size = high - low;
            if(size < MAX_MIXED_INSERTION_SORT_SIZE + bits && (bits & 1) > 0) {
                mixedInsertionSort(a, low, high - 3 * ((size >> 5) << 3), high);
                return;
            }
            if(size < MAX_INSERTION_SORT_SIZE) {
                insertionSort(a, low, high);
                return;
            }
            if((bits == 0 || size > MIN_TRY_MERGE_SIZE && (bits & 1) > 0) && tryMergeRuns(a, low, size))
                return;
            if((bits += DELTA) > MAX_RECURSION_DEPTH) {
                heapSort(a, low, high);
                return;
            }
            int step = (size >> 3) * 3 + 3;
            int e1 = low + step;
            int e5 = end - step;
            int e3 = (e1 + e5) >>> 1;
            int e2 = (e1 + e3) >>> 1;
            int e4 = (e3 + e5) >>> 1;
            double a3 = a[e3];
            if(a[e5] < a[e2]) { double t = a[e5]; a[e5] = a[e2]; a[e2] = t; }
            if(a[e4] < a[e1]) { double t = a[e4]; a[e4] = a[e1]; a[e1] = t; }
            if(a[e5] < a[e4]) { double t = a[e5]; a[e5] = a[e4]; a[e4] = t; }
            if(a[e2] < a[e1]) { double t = a[e2]; a[e2] = a[e1]; a[e1] = t; }
            if(a[e4] < a[e2]) { double t = a[e4]; a[e4] = a[e2]; a[e2] = t; }

            if(a3 < a[e2]) {
                if(a3 < a[e1]) {
                    a[e3] = a[e2];
                    a[e2] = a[e1];
                    a[e1] = a3;
                }
                else {
                    a[e3] = a[e2];
                    a[e2] = a3;
                }
            }
            else if(a3 > a[e4]) {
                if(a3 > a[e5]) {
                    a[e3] = a[e4];
                    a[e4] = a[e5];
                    a[e5] = a3;
                }
                else {
                    a[e3] = a[e4];
                    a[e4] = a3;
                }
            }
            int lower = low; // The index of the last element of the left part
            int upper = end;
            if(a[e1] < a[e2] && a[e2] < a[e3] && a[e3] < a[e4] && a[e4] < a[e5]) {
                double pivot1 = a[e1];
                double pivot2 = a[e5];
                a[e1] = a[lower];
                a[e5] = a[upper];
                while(a[++lower] < pivot1);
                while(a[--upper] > pivot2);
                for(int unused = --lower, k = ++upper; --k > lower;) {
                    double ak = a[k];
                    if(ak < pivot1) {
                        while(lower < k) {
                            if(a[++lower] >= pivot1) {
                                if(a[lower] > pivot2) {
                                    a[k] = a[--upper];
                                    a[upper] = a[lower];
                                }
                                else
                                    a[k] = a[lower];
                                a[lower] = ak;
                                break;
                            }
                        }
                    }
                    else if(ak > pivot2) {
                        a[k] = a[--upper];
                        a[upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot1;
                a[end] = a[upper];
                a[upper] = pivot2;
                sort(a, bits | 1, lower + 1, upper);
                sort(a, bits | 1, upper + 1, high);
            }
            else {
                double pivot = a[e3];
                a[e3] = a[lower];
                for(int k = ++upper; --k > lower;) {
                    double ak = a[k];
                    if(ak != pivot) {
                        a[k] = pivot;
                        if(ak < pivot) {
                            while(a[++lower] < pivot);
                            if(a[lower] > pivot)
                                a[--upper] = a[lower];
                            a[lower] = ak;
                        }
                        else
                            a[--upper] = ak;
                    }
                }
                a[low] = a[lower];
                a[lower] = pivot;
                sort(a, bits | 1, upper, high);
            }
            high = lower;
        }
    }

    private static void mixedInsertionSort(double[] a, int low, int end, int high) {
        if(end == high) {
            for(int i; ++low < end;) {
                double ai = a[i = low];
                while(ai < a[--i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
        else {
            double pin = a[end];
            for(int i, p = high; ++low < end;) {
                double ai = a[i = low];
                if(ai < a[i - 1]) {
                    a[i] = a[--i];
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
                else if(p > i && ai > pin) {
                    while(a[--p] > pin);
                    if(p > i) {
                        ai = a[p];
                        a[p] = a[i];
                    }
                    while(ai < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = ai;
                }
            }
            for(int i; low < high; ++low) {
                double a1 = a[i = low], a2 = a[++low];
                if(a1 > a2) {
                    while(a1 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a1;
                    while(a2 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a2;
                }
                else if(a1 < a[i - 1]) {
                    while(a2 < a[--i])
                        a[i + 2] = a[i];
                    a[++i + 1] = a2;
                    while(a1 < a[--i])
                        a[i + 1] = a[i];
                    a[i + 1] = a1;
                }
            }
        }
    }

    private static void insertionSort(double[] a, int low, int high) {
        for(int i, k = low; ++k < high;) {
            double ai = a[i = k];
            if(ai < a[i - 1]) {
                while(--i >= low && ai < a[i])
                    a[i + 1] = a[i];
                a[i + 1] = ai;
            }
        }
    }

    private static void heapSort(double[] a, int low, int high) {
        for(int k = (low + high) >>> 1; k > low;)
            pushDown(a, --k, a[k], low, high);
        while(--high > low) {
            double max = a[low];
            pushDown(a, low, a[high], low, high);
            a[high] = max;
        }
    }

    private static void pushDown(double[] a, int p, double value, int low, int high) {
        for(int k;; a[p] = a[p = k]) {
            k = (p << 1) - low + 2;
            if(k > high)
                break;
            if(k == high || a[k] < a[k - 1])
                --k;
            if(a[k] <= value)
                break;
        }
        a[p] = value;
    }

    private static boolean tryMergeRuns(double[] a, int low, int size) {
        int[] run = null;
        int high = low + size;
        int count = 1, last = low;
        for(int k = low + 1; k < high;) {
            if(a[k - 1] < a[k])
                while(++k < high && a[k - 1] <= a[k]);
            else if(a[k - 1] > a[k]) {
                while(++k < high && a[k - 1] >= a[k]);
                for(int i = last - 1, j = k; ++i < --j && a[i] > a[j];) {
                    double ai = a[i];
                    a[i] = a[j];
                    a[j] = ai;
                }
            }
            else {
                for(double ak = a[k]; ++k < high && ak == a[k];);
                if(k < high)
                    continue;
            }
            if(run == null) {
                if(k == high)
                    return true;
                if(k - low < MIN_FIRST_RUN_SIZE)
                    return false;
                run = new int[((size >> 10) | 0x7F) & 0x3FF];
                run[0] = low;
            }
            else if(a[last - 1] > a[last]) {
                if(count > (k - low) >> MIN_FIRST_RUNS_FACTOR)
                    return false;
                if(++count == MAX_RUN_CAPACITY)
                    return false;
                if(count == run.length)
                    run = Arrays.copyOf(run, count << 1);
            }
            run[count] = (last = k);
        }

        if(count > 1) {
            double[] b = new double[size];
            mergeRuns(a, b, low, 1, run, 0, count);
        }
        return true;
    }

    private static double[] mergeRuns(double[] a, double[] b, int offset, int aim, int[] run, int lo, int hi) {
        if(hi - lo == 1) {
            if(aim >= 0)
                return a;
            for(int i = run[hi], j = i - offset, low = run[lo]; i > low; b[--j] = a[--i]);
            return b;
        }
        int mi = lo, rmi = (run[lo] + run[hi]) >>> 1;
        while(run[++mi + 1] <= rmi);
        double[] a1 = mergeRuns(a, b, offset, -aim, run, lo, mi);
        double[] a2 = mergeRuns(a, b, offset, 0, run, mi, hi);
        double[] dst = a1 == a ? b : a;
        int k = a1 == a ? run[lo] - offset : run[lo];
        int lo1 = a1 == b ? run[lo] - offset : run[lo];
        int hi1 = a1 == b ? run[mi] - offset : run[mi];
        int lo2 = a2 == b ? run[mi] - offset : run[mi];
        int hi2 = a2 == b ? run[hi] - offset : run[hi];
        mergeParts(dst, k, a1, lo1, hi1, a2, lo2, hi2);
        return dst;
    }

    private static void mergeParts(double[] dst, int k, double[] a1, int lo1, int hi1, double[] a2, int lo2, int hi2) {
        while(lo1 < hi1 && lo2 < hi2)
            dst[k++] = a1[lo1] < a2[lo2] ? a1[lo1++] : a2[lo2++];
        if(dst != a1 || k < lo1) {
            while(lo1 < hi1)
                dst[k++] = a1[lo1++];
        }
        if(dst != a2 || k < lo2) {
            while(lo2 < hi2)
                dst[k++] = a2[lo2++];
        }
    }
}
