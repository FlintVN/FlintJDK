package flint.drawing;

public abstract class Image {
    protected int format;
    protected int width;
    protected int height;
    protected byte[] data;

    public Image() {

    }

    public Image(String filePath) {

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
