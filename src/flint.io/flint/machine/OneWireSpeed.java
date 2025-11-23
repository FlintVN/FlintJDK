package flint.machine;

public enum OneWireSpeed {
    STANDARD(0),
    OVERDRIVE(1),
    OVERDRIVE_ONLY(2);

    final int value;

    private OneWireSpeed(int value) {
        this.value = value;
    }

    static OneWireSpeed fromValue(int value) {
        return switch(value) {
            case 0 -> STANDARD;
            case 1 -> OVERDRIVE;
            default -> OVERDRIVE_ONLY;
        };
    }
}
