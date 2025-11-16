package flint.machine;

public enum StopBits {
    ONE(0),
    ONE_POINT_FIVE(1),
    TWO(2);

    final int value;

    private StopBits(int value) {
        this.value = value;
    }

    public static StopBits fromValue(int value) {
        return switch(value) {
            case 0 -> ONE;
            case 1 -> ONE_POINT_FIVE;
            default -> TWO;
        };
    }
}
