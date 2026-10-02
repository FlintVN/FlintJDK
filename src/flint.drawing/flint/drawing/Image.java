package flint.drawing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

public abstract class Image {
    protected boolean hasAlpha;
    protected int width;
    protected int height;
    protected byte[] data;

    protected Image() {

    }

    public static Image create(int width, int height) {
        return Graphics.getGraphicsFactory().createImage(width, height);
    }

    public static Image create(byte[] imageData, int off, int len) {
        return Graphics.getGraphicsFactory().createImage(imageData, off, len);
    }

    public static Image create(int[] rgb, int w, int h, boolean processAlpha) {
        return Graphics.getGraphicsFactory().createImage(rgb, w, h, processAlpha);
    }

    public static Image create(InputStream in) throws IOException {
        byte[] bytes = in.readAllBytes();
        return Graphics.getGraphicsFactory().createImage(bytes, 0, bytes.length);
    }

    public static Image create(Image src) {
        Image img = Graphics.getGraphicsFactory().createImage(src.width, src.height);
        System.arraycopy(src.data, 0, img.data, 0, src.data.length);
        return img;
    }

    public static Image create(String path) throws IOException {
        return create(new FileInputStream(path));
    }

    public static Image create(File file) throws IOException {
        return create(new FileInputStream(file));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
