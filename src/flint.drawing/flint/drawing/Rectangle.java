package flint.drawing;

public class Rectangle {
    final int x;
    final int y;
    final int width;
    final int height;

    public Rectangle(int w, int h) {
        this(0, 0, w, h);
    }

    public Rectangle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
