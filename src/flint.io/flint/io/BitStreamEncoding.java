package flint.io;

public enum BitStreamEncoding {
    HIGH_LOW(0),
    LOW_HIGH(1);

    final int value;

    private BitStreamEncoding(int value) {
        this.value = value;
    }

    static BitStreamEncoding fromValue(int value) {
        return switch(value) {
            case 0 -> HIGH_LOW;
            default -> LOW_HIGH;
        };
    }
}
