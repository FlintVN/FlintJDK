package flint.drawing;

public class Rgb565Image extends Image {
    Rgb565Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new byte[width * height];
    }

    Rgb565Image(int width, int height, byte[] data) {
        this.width = width;
        this.height = height;
        this.data = data;
    }
}
