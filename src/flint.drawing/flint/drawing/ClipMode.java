package flint.drawing;

public enum ClipMode {
    REPLACE(0),
    INTERSECT(1);

    final int value;

    private ClipMode(int value) {
        this.value = value;
    }
}
