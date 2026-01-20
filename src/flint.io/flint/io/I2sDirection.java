package flint.io;

public enum I2sDirection {
    TX(0),
    RX(1);

    final int value;

    private I2sDirection(int value) {
        this.value = value;
    }

    static I2sDirection fromValue(int value) {
        return switch(value) {
            case 0 -> TX;
            default -> RX;
        };
    }
}
