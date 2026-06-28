package java.util;

public class Random {
    private long seed;
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    public Random() {
        this(System.currentTimeMillis());
    }

    @SuppressWarnings("this-escape")
    public Random(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & mask;
    }

    protected int next(int bits) {
        seed = (seed * multiplier + addend) & mask;
        return (int)(seed >>> (48 - bits));
    }

    public int nextInt() {
        return next(32);
    }

    public int nextInt(int bound) {
        if(bound <= 0)
            throw new IllegalArgumentException("bound must be positive");
        if((bound & -bound) == bound)
            return (int)((bound * (long)next(31)) >> 31);
        int bits, val;
        do {
            bits = next(31);
            val = bits % bound;
        }
        while(bits - val + (bound - 1) < 0);
        return val;
    }

    public long nextLong() {
        return ((long)(next(32)) << 32) + next(32);
    }

    public boolean nextBoolean() {
        return next(1) != 0;
    }

    public float nextFloat() {
        return next(24) / ((float)(1 << 24));
    }

    public double nextDouble() {
        return (((long)(next(26)) << 27) + next(27)) * 0x1.0p-53;
    }

    public void nextBytes(byte[] bytes) {
        for(int i = 0, len = bytes.length; i < len;) {
            int rnd = nextInt();
            for(int n = Math.min(len - i, 4); n-- > 0; rnd >>= 8)
                bytes[i++] = (byte)rnd;
        }
    }
}
