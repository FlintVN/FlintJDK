package flint.io;

public enum I2sDataMode {
    MONO(0),
    STEREO(1);

    final int value;

    private I2sDataMode(int value) {
        this.value = value;
    }

    static I2sDataMode fromValue(int value) {
        return switch(value) {
            case 0 -> MONO;
            default -> STEREO;
        };
    }
}
