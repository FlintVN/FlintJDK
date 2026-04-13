package flint.drawing;

public enum ImageFormat {
    BMP(0),
    PNG(1);

    final int value;

    private ImageFormat(int value) {
        this.value = value;
    }
}
