package flint.math;

public enum WindowType {
    HANN(0),
    HAMMING(1),
    BLACKMAN(2);

    final int value;

    private WindowType(int value) {
        this.value = value;
    }
}
