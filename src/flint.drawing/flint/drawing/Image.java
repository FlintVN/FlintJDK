package flint.drawing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

public abstract class Image {
    /** No transform. */
    public static final int TRANS_NONE = 0;

    /** Mirror horizontally, then rotate 180 degrees (equivalent to a vertical flip). */
    public static final int TRANS_MIRROR_ROT180 = 1;

    /** Mirror horizontally. */
    public static final int TRANS_MIRROR = 2;

    /** Rotate 180 degrees. */
    public static final int TRANS_ROT180 = 3;

    /** Mirror horizontally, then rotate 270 degrees clockwise (equivalent to transposing). */
    public static final int TRANS_MIRROR_ROT270 = 4;

    /** Rotate 90 degrees clockwise. */
    public static final int TRANS_ROT90 = 5;

    /** Rotate 270 degrees clockwise (90 degrees counter-clockwise). */
    public static final int TRANS_ROT270 = 6;

    /** Mirror horizontally, then rotate 90 degrees clockwise. */
    public static final int TRANS_MIRROR_ROT90 = 7;

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
        Image img = Graphics.getGraphicsFactory().createImage(rgb, w, h, processAlpha);
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
        if (src == null)
            throw new NullPointerException("Image src cannot be null");
        Image img = Graphics.getGraphicsFactory().createImage(src.width, src.height);
        System.arraycopy(src.data, 0, img.data, 0, src.data.length);
        img.hasAlpha = src.hasAlpha;
        return img;
    }

    public static Image create(Image src, boolean mutable) {
        Image img = create(src);
        img.mutable = mutable;
        return img;
    }

    public static Image create(Image src, int x, int y, int w, int h, int transform, boolean mutable) {
        Image img = Graphics.getGraphicsFactory().createImage(src, x, y, w, h, transform);
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

    public abstract void getRgb(int[] rgb, int offset, int scanlength, int x, int y, int w, int h);

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
