package flint.drawing;

public class Rectangle {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Point getLocation() {
        return new Point(x, y);
    }
}
