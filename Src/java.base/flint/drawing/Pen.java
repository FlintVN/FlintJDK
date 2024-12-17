package flint.drawing;

public class Pen {
    final Color color;
    final int width;

    public Pen(Color color) {
        this.color = color;
        this.width = 1;
    }

    public Pen(Color color, int width) {
        this.color = color;
        this.width = width;
    }

    public Color getColor() {
        return color;
    }

    public int getWidth() {
        return width;
    }
}
