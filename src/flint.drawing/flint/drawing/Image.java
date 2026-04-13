package flint.drawing;

public abstract class Image {
    int width;
    int height;
    byte[] data;

    protected Image(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void save(String name) {
        save(name, ImageFormat.PNG);
    }

    public void save(String name, ImageFormat format) {
        // TODO
    }
}
