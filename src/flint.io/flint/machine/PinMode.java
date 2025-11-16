package flint.machine;

public enum PinMode {
    INPUT(0),
    OUTPUT(1),
    INPUT_PULL_UP(2),
    INPUT_PULL_DOWN(3),
    OUTPUT_OPEN_DRAIN(4);

    final int value;

    private PinMode(int value) {
        this.value = value;
    }
}
