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
    private boolean mutable;

    protected Image() {
        mutable = true;
    }

    public static Image create(int width, int height) {
        return Graphics.getGraphicsFactory().createImage(width, height);
    }

    public static Image create(byte[] imageData, int off, int len, boolean mutable) {
        Image img = Graphics.getGraphicsFactory().createImage(imageData, off, len);
        img.mutable = mutable;
        return img;
    }

    public static Image create(int[] rgb, int w, int h, boolean processAlpha, boolean mutable) {
        Image img =  Graphics.getGraphicsFactory().createImage(rgb, w, h, processAlpha);
        img.mutable = mutable;
        return img;
    }

    public static Image create(InputStream in) throws IOException {
        byte[] bytes = in.readAllBytes();
        return Graphics.getGraphicsFactory().createImage(bytes, 0, bytes.length);
    }

    public static Image create(InputStream in, boolean mutable) throws IOException {
        Image img = create(in);
        img.mutable = mutable;
        return img;
    }

    public static Image create(Image src) {
        Image img = Graphics.getGraphicsFactory().createImage(src.width, src.height);
        System.arraycopy(src.data, 0, img.data, 0, src.data.length);
        return img;
    }

    public static Image create(Image src, boolean mutable) {
        Image img = create(src);
        img.mutable = mutable;
        return img;
    }

    public static Image create(String fileName) throws IOException {
        if (fileName == null)
            throw new NullPointerException("fileName cannot be null");
        FileInputStream fi = new FileInputStream(fileName);
        Image img = create(fi);
        fi.close();
        return img;
    }

    public static Image create(String fileName, boolean mutable) throws IOException {
        Image img = create(fileName);
        img.mutable = mutable;
        return img;
    }

    public static Image create(File file) throws IOException {
        return create(file.getPath());
    }

    public static Image create(File file, boolean mutable) throws IOException {
        Image img = create(file.getPath());
        img.mutable = mutable;
        return img;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isMutable() {
        return mutable;
    }
}
