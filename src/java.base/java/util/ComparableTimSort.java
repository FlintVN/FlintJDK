package java.util;

class ComparableTimSort {
    private static final int MIN_MERGE = 32;

    private final Object[] a;

    private static final int MIN_GALLOP = 7;

    private int minGallop = MIN_GALLOP;

    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;

    private Object[] tmp;
    private int tmpBase;
    private int tmpLen;

    private int stackSize = 0;
    private final int[] runBase;
    private final int[] runLen;

    private ComparableTimSort(Object[] a, Object[] work, int workBase, int workLen) {
        this.a = a;

        int len = a.length;
        int tlen = (len < 2 * INITIAL_TMP_STORAGE_LENGTH) ? len >>> 1 : INITIAL_TMP_STORAGE_LENGTH;
        if(work == null || workLen < tlen || workBase + tlen > work.length) {
            tmp = new Object[tlen];
            tmpBase = 0;
            tmpLen = tlen;
        }
        else {
            tmp = work;
            tmpBase = workBase;
            tmpLen = workLen;
        }

        int stackLen = (len < 120 ? 5 : len < 1542 ? 10 : len < 119151 ? 24 : 49);
        runBase = new int[stackLen];
        runLen = new int[stackLen];
    }

    static void sort(Object[] a, int lo, int hi, Object[] work, int workBase, int workLen) {
        int nRemaining = hi - lo;
        if(nRemaining < 2)
            return;

        if(nRemaining < MIN_MERGE) {
            int initRunLen = countRunAndMakeAscending(a, lo, hi);
            binarySort(a, lo, hi, lo + initRunLen);
            return;
        }

        ComparableTimSort ts = new ComparableTimSort(a, work, workBase, workLen);
        int minRun = minRunLength(nRemaining);
        do {
            int runLen = countRunAndMakeAscending(a, lo, hi);

            if(runLen < minRun) {
                int force = nRemaining <= minRun ? nRemaining : minRun;
                binarySort(a, lo, lo + force, lo + runLen);
                runLen = force;
            }

            ts.pushRun(lo, runLen);
            ts.mergeCollapse();

            lo += runLen;
            nRemaining -= runLen;
        } while(nRemaining != 0);

        ts.mergeForceCollapse();
    }

    @SuppressWarnings({"fallthrough", "rawtypes", "unchecked"})
    private static void binarySort(Object[] a, int lo, int hi, int start) {
        if(start == lo)
            start++;
        for(; start < hi; start++) {
            Comparable pivot = (Comparable)a[start];
            int left = lo;
            int right = start;
            while(left < right) {
                int mid = (left + right) >>> 1;
                if(pivot.compareTo(a[mid]) < 0)
                    right = mid;
                else
                    left = mid + 1;
            }

            int n = start - left;
            switch(n) {
                case 2:
                    a[left + 2] = a[left + 1];
                case 1:
                    a[left + 1] = a[left];
                    break;
                default: System.arraycopy(a, left, a, left + 1, n);
            }
            a[left] = pivot;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static int countRunAndMakeAscending(Object[] a, int lo, int hi) {
        int runHi = lo + 1;
        if(runHi == hi)
            return 1;

        if(((Comparable)a[runHi++]).compareTo(a[lo]) < 0) {
            while(runHi < hi && ((Comparable)a[runHi]).compareTo(a[runHi - 1]) < 0)
                runHi++;
            reverseRange(a, lo, runHi);
        }
        else {
            while(runHi < hi && ((Comparable)a[runHi]).compareTo(a[runHi - 1]) >= 0)
                runHi++;
        }

        return runHi - lo;
    }

    private static void reverseRange(Object[] a, int lo, int hi) {
        hi--;
        while(lo < hi) {
            Object t = a[lo];
            a[lo++] = a[hi];
            a[hi--] = t;
        }
    }

    private static int minRunLength(int n) {
        int r = 0;
        while(n >= MIN_MERGE) {
            r |= (n & 1);
            n >>= 1;
        }
        return n + r;
    }

    private void pushRun(int runBase, int runLen) {
        this.runBase[stackSize] = runBase;
        this.runLen[stackSize] = runLen;
        stackSize++;
    }

    private void mergeCollapse() {
        while(stackSize > 1) {
            int n = stackSize - 2;
            if(n > 0 && runLen[n-1] <= runLen[n] + runLen[n+1] || n > 1 && runLen[n-2] <= runLen[n] + runLen[n-1]) {
                if(runLen[n - 1] < runLen[n + 1])
                    n--;
            }
            else if(n < 0 || runLen[n] > runLen[n + 1])
                break;
            mergeAt(n);
        }
    }

    private void mergeForceCollapse() {
        while(stackSize > 1) {
            int n = stackSize - 2;
            if(n > 0 && runLen[n - 1] < runLen[n + 1])
                n--;
            mergeAt(n);
        }
    }

    @SuppressWarnings("unchecked")
    private void mergeAt(int i) {
        int base1 = runBase[i];
        int len1 = runLen[i];
        int base2 = runBase[i + 1];
        int len2 = runLen[i + 1];

        runLen[i] = len1 + len2;
        if(i == stackSize - 3) {
            runBase[i + 1] = runBase[i + 2];
            runLen[i + 1] = runLen[i + 2];
        }
        stackSize--;

        int k = gallopRight((Comparable<Object>)a[base2], a, base1, len1, 0);
        base1 += k;
        len1 -= k;
        if(len1 == 0)
            return;

        len2 = gallopLeft((Comparable<Object>)a[base1 + len1 - 1], a, base2, len2, len2 - 1);
        if(len2 == 0)
            return;

        if(len1 <= len2)
            mergeLo(base1, len1, base2, len2);
        else
            mergeHi(base1, len1, base2, len2);
    }

    private static int gallopLeft(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int lastOfs = 0;
        int ofs = 1;
        if(key.compareTo(a[base + hint]) > 0) {
            int maxOfs = len - hint;
            while(ofs < maxOfs && key.compareTo(a[base + hint + ofs]) > 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if(ofs <= 0)
                    ofs = maxOfs;
            }
            if(ofs > maxOfs)
                ofs = maxOfs;

            lastOfs += hint;
            ofs += hint;
        }
        else {
            final int maxOfs = hint + 1;
            while(ofs < maxOfs && key.compareTo(a[base + hint - ofs]) <= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if(ofs <= 0)
                    ofs = maxOfs;
            }
            if(ofs > maxOfs)
                ofs = maxOfs;

            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        }

        lastOfs++;
        while(lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);

            if(key.compareTo(a[base + m]) > 0)
                lastOfs = m + 1;
            else
                ofs = m;
        }
        return ofs;
    }

    private static int gallopRight(Comparable<Object> key, Object[] a, int base, int len, int hint) {
        int ofs = 1;
        int lastOfs = 0;
        if(key.compareTo(a[base + hint]) < 0) {
            int maxOfs = hint + 1;
            while(ofs < maxOfs && key.compareTo(a[base + hint - ofs]) < 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if(ofs <= 0)
                    ofs = maxOfs;
            }
            if(ofs > maxOfs)
                ofs = maxOfs;

            int tmp = lastOfs;
            lastOfs = hint - ofs;
            ofs = hint - tmp;
        }
        else {
            int maxOfs = len - hint;
            while(ofs < maxOfs && key.compareTo(a[base + hint + ofs]) >= 0) {
                lastOfs = ofs;
                ofs = (ofs << 1) + 1;
                if(ofs <= 0)
                    ofs = maxOfs;
            }
            if(ofs > maxOfs)
                ofs = maxOfs;
            lastOfs += hint;
            ofs += hint;
        }
        lastOfs++;
        while(lastOfs < ofs) {
            int m = lastOfs + ((ofs - lastOfs) >>> 1);

            if(key.compareTo(a[base + m]) < 0)
                ofs = m;
            else
                lastOfs = m + 1;
        }
        return ofs;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mergeLo(int base1, int len1, int base2, int len2) {
        Object[] a = this.a;
        Object[] tmp = ensureCapacity(len1);

        int cursor1 = tmpBase;
        int cursor2 = base2;
        int dest = base1;
        System.arraycopy(a, base1, tmp, cursor1, len1);

        a[dest++] = a[cursor2++];
        if(--len2 == 0) {
            System.arraycopy(tmp, cursor1, a, dest, len1);
            return;
        }
        if(len1 == 1) {
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
            return;
        }

        int minGallop = this.minGallop;
    outer:
        while(true) {
            int count1 = 0;
            int count2 = 0;

            do {
                if(((Comparable)a[cursor2]).compareTo(tmp[cursor1]) < 0) {
                    a[dest++] = a[cursor2++];
                    count2++;
                    count1 = 0;
                    if(--len2 == 0)
                        break outer;
                }
                else {
                    a[dest++] = tmp[cursor1++];
                    count1++;
                    count2 = 0;
                    if(--len1 == 1)
                        break outer;
                }
            } while((count1 | count2) < minGallop);

            do {
                count1 = gallopRight((Comparable)a[cursor2], tmp, cursor1, len1, 0);
                if(count1 != 0) {
                    System.arraycopy(tmp, cursor1, a, dest, count1);
                    dest += count1;
                    cursor1 += count1;
                    len1 -= count1;
                    if(len1 <= 1)
                        break outer;
                }
                a[dest++] = a[cursor2++];
                if(--len2 == 0)
                    break outer;

                count2 = gallopLeft((Comparable) tmp[cursor1], a, cursor2, len2, 0);
                if(count2 != 0) {
                    System.arraycopy(a, cursor2, a, dest, count2);
                    dest += count2;
                    cursor2 += count2;
                    len2 -= count2;
                    if(len2 == 0)
                        break outer;
                }
                a[dest++] = tmp[cursor1++];
                if(--len1 == 1)
                    break outer;
                minGallop--;
            } while(count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
            if(minGallop < 0)
                minGallop = 0;
            minGallop += 2;
        }
        this.minGallop = minGallop < 1 ? 1 : minGallop;

        if(len1 == 1) {
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
        }
        else if(len1 == 0)
            throw new IllegalArgumentException("Comparison method violates its general contract!");
        else
            System.arraycopy(tmp, cursor1, a, dest, len1);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mergeHi(int base1, int len1, int base2, int len2) {
        Object[] a = this.a;
        Object[] tmp = ensureCapacity(len2);
        int tmpBase = this.tmpBase;
        System.arraycopy(a, base2, tmp, tmpBase, len2);

        int cursor1 = base1 + len1 - 1;
        int cursor2 = tmpBase + len2 - 1;
        int dest = base2 + len2 - 1;

        a[dest--] = a[cursor1--];
        if(--len1 == 0) {
            System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
            return;
        }
        if(len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
            return;
        }

        int minGallop = this.minGallop;
    outer:
        while(true) {
            int count1 = 0;
            int count2 = 0;

            do {
                if(((Comparable) tmp[cursor2]).compareTo(a[cursor1]) < 0) {
                    a[dest--] = a[cursor1--];
                    count1++;
                    count2 = 0;
                    if(--len1 == 0)
                        break outer;
                }
                else {
                    a[dest--] = tmp[cursor2--];
                    count2++;
                    count1 = 0;
                    if(--len2 == 1)
                        break outer;
                }
            } while((count1 | count2) < minGallop);

            do {
                count1 = len1 - gallopRight((Comparable) tmp[cursor2], a, base1, len1, len1 - 1);
                if(count1 != 0) {
                    dest -= count1;
                    cursor1 -= count1;
                    len1 -= count1;
                    System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
                    if(len1 == 0)
                        break outer;
                }
                a[dest--] = tmp[cursor2--];
                if(--len2 == 1)
                    break outer;

                count2 = len2 - gallopLeft((Comparable)a[cursor1], tmp, tmpBase, len2, len2 - 1);
                if(count2 != 0) {
                    dest -= count2;
                    cursor2 -= count2;
                    len2 -= count2;
                    System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
                    if(len2 <= 1)
                        break outer;
                }
                a[dest--] = a[cursor1--];
                if(--len1 == 0)
                    break outer;
                minGallop--;
            } while(count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
            if(minGallop < 0)
                minGallop = 0;
            minGallop += 2;
        }
        this.minGallop = minGallop < 1 ? 1 : minGallop;

        if(len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
        }
        else if(len2 == 0)
            throw new IllegalArgumentException("Comparison method violates its general contract!");
        else
            System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
    }

    private Object[] ensureCapacity(int minCapacity) {
        if(tmpLen < minCapacity) {
            int newSize = -1 >>> Integer.numberOfLeadingZeros(minCapacity);
            newSize++;

            if(newSize < 0)
                newSize = minCapacity;
            else
                newSize = Math.min(newSize, a.length >>> 1);

            @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
            Object[] newArray = new Object[newSize];
            tmp = newArray;
            tmpLen = newSize;
            tmpBase = 0;
        }
        return tmp;
    }
}
