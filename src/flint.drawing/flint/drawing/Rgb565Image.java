package flint.drawing;

public class Rgb565Image extends Image {
    Rgb565Image(int width, int height) {
        if (((width | height) >>> 16) != 0)
            throw new IllegalArgumentException("Image dimensions are out of range");
        int pixels = width * height;
        if ((pixels >>> 30) != 0)
            throw new IllegalArgumentException("Image data is too large");

        this.width = width;
        this.height = height;
        this.data = new byte[pixels << 1];
    }

    Rgb565Image(int width, int height, byte[] data) {
        if (data == null)
            throw new NullPointerException("data cannot be null");
        if (((width | height) >>> 16) != 0)
            throw new IllegalArgumentException("Image dimensions are out of range");
        int pixels = width * height;
        if ((pixels >>> 30) != 0)
            throw new IllegalArgumentException("Image data is too large");
        if (data.length < (pixels << 1))
            throw new IllegalArgumentException("The image buffer is too small");

        this.width = width;
        this.height = height;
        this.data = data;
    }
}
