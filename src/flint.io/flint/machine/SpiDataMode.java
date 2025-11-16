package flint.machine;

public enum SpiDataMode {
    MSB_MODE0(0),
    MSB_MODE1(1),
    MSB_MODE2(2),
    MSB_MODE3(3),
    LSB_MODE0(4),
    LSB_MODE1(5),
    LSB_MODE2(6),
    LSB_MODE3(7);

    final int value;

    private SpiDataMode(int value) {
        this.value = value;
    }

    public static SpiDataMode fromValue(int value) {
        return switch(value) {
            case 0 -> MSB_MODE0;
            case 1 -> MSB_MODE1;
            case 2 -> MSB_MODE2;
            case 3 -> MSB_MODE3;
            case 4 -> LSB_MODE0;
            case 5 -> LSB_MODE1;
            case 6 -> LSB_MODE2;
            default -> LSB_MODE3;
        };
    }
}
