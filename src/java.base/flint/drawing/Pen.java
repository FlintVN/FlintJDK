package flint.drawing;

public class Pen {
    public final Color color;
    public final int width;

    public Pen(Color color) {
        this.color = color;
        this.width = 1;
    }

    public Pen(Color color, int width) {
        this.color = color;
        this.width = width;
    }
}
