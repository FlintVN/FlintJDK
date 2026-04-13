package flint.drawing;

public abstract class Image {
    int format;
    int width;
    int height;
    byte[] data;

    public Image(String filePath) {

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
