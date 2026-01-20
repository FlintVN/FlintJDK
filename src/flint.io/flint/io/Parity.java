package flint.io;

public enum Parity {
    NONE(0),
    ODD(1),
    EVEN(2),
    MARK(3),
    SPACE(4);

    final int value;

    private Parity(int value) {
        this.value = value;
    }

    static Parity fromValue(int value) {
        return switch(value) {
            case 0 -> NONE;
            case 1 -> ODD;
            case 2 -> EVEN;
            case 3 -> MARK;
            default -> SPACE;
        };
    }
}
