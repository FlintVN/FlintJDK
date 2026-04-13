package flint.drawing;

public abstract class Color {
    private static Color[] cache;

    protected Color() {

    }

    public static Color valueOf(int r, int g, int b) {
        return Graphics.getGraphicsFactory().createColor(1, r, g, b);
    }

    public static Color valueOf(int a, int r, int g, int b) {
        return Graphics.getGraphicsFactory().createColor(a, r, g, b);
    }

    public abstract int getValue();
    public abstract int getRed();
    public abstract int getGreen();
    public abstract int getBlue();
    public abstract float getAlpha();

    @Override
    public int hashCode() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Color)
            return getValue() == ((Color)obj).getValue();
        return false;
    }
}
