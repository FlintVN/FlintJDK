package flint.drawing;

public final class Color {
    private final int value;

    public Color(int value) {
        this.value = value;
    }

    public Color(int r, int g, int b) {
        this(255, r, g, b);
    }

    public Color(int a, int r, int g, int b) {
        if(a < 0 || a > 255) throw new IllegalArgumentException("Color parameter outside of expected range: Alpha");
        if(r < 0 || r > 255) throw new IllegalArgumentException("Color parameter outside of expected range: Red");
        if(g < 0 || g > 255) throw new IllegalArgumentException("Color parameter outside of expected range: Green");
        if(b < 0 || b > 255) throw new IllegalArgumentException("Color parameter outside of expected range: Blue");
        value = (a << 24) | (r << 16) | (g << 8) | b;
    }

    public int getAlpha() {
        return value >>> 24;
    }

    public int getValue() {
        return value;
    }

    public int getRed() {
        return (value >>> 16) & 0xFF;
    }

    public int getGreen() {
        return (value >>> 8) & 0xFF;
    }

    public int getBlue() {
        return value & 0xFF;
    }
}
