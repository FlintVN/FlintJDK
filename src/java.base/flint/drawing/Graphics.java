package flint.drawing;

public class Graphics {
    private final int colorMode;
    private final byte[] colorBuffer;
    private int x;
    private int y;
    private int width;
    private int clipX;
    private int clipY;
    private int clipWidth;
    private int clipHeight;

    public Graphics(int x, int y, int width, int height) {
        this(x, y, width, height, ColorMode.RGB565);
    }

    public Graphics(int x, int y, int width, int height, ColorMode colorMode) {
        this.colorMode = switch(colorMode) {
            case RGB444 -> 0;
            case RGB555 -> 1;
            case RGB565 -> 2;
            case RGB888 -> 3;
        };
        this.colorBuffer = new byte[width * height * getPixelSize()];
        this.x = x;
        this.y = y;
        this.width = width;
        this.clipX = x;
        this.clipY = y;
        this.clipWidth = width;
        this.clipHeight = height;
    }

    private int getPixelSize() {
        return (this.colorMode == 3) ? 3 : 2;
    }

    public void translate(int x, int y) {
        this.x += x;
        this.y += y;
        this.clipX += x;
        this.clipY += y;
    }

    public void clipRect(int x, int y, int width, int height) {
        int xEnd1 = this.clipWidth + this.clipX;
        int yEnd1 = this.clipHeight + this.clipY;
        int xEnd2 = width + x;
        int yEnd2 = height + y;
        this.clipX = x > this.clipX ? x : this.clipX;
        this.clipY = y > this.clipY ? y : this.clipY;
        this.clipWidth = ((xEnd2 < xEnd1) ? xEnd2 : xEnd1) - this.clipX;
        this.clipHeight = ((yEnd2 < yEnd1) ? yEnd2 : yEnd1) - this.clipY;
    }

    public void setClip(int x, int y, int width, int height) {
        int xEnd1 = this.width + this.x;
        int yEnd1 = colorBuffer.length / this.width / getPixelSize() + this.y;
        int xEnd2 = width + x;
        int yEnd2 = height + y;
        this.clipX = x > this.x ? x : this.x;
        this.clipY = y > this.y ? y : this.y;
        this.clipWidth = ((xEnd2 < xEnd1) ? xEnd2 : xEnd1) - this.clipX;
        this.clipHeight = ((yEnd2 < yEnd1) ? yEnd2 : yEnd1) - this.clipY;
    }

    public native void drawLine(Pen pen, int x1, int y1, int x2, int y2);

    public void drawLine(Pen pen, Point p1, Point p2) {
        drawLine(pen, p1.x, p1.y, p2.x, p2.y);
    }

    public native void drawRect(Pen pen, int x, int y, int width, int height);

    public void drawRect(Pen pen, Rectangle rect) {
        drawRect(pen, rect.x, rect.y, rect.width, rect.height);
    }

    public native void fillRect(Color color, int x, int y, int width, int height);

    public void fillRect(Color color, Rectangle rect) {
        fillRect(color, rect.x, rect.y, rect.width, rect.height);
    }

    public native void drawRoundRect(Pen pen, int x, int y, int width, int height, int arcWidth, int arcHeight);

    public native void fillRoundRect(Color color, int x, int y, int width, int height, int arcWidth, int arcHeight);

    public native void drawEllipse(Pen pen, int x, int y, int width, int height);

    public void drawEllipse(Pen pen, Rectangle rect) {
        drawEllipse(pen, rect.x, rect.y, rect.width, rect.height);
    }

    public native void fillEllipse(Color color, int x, int y, int width, int height);

    public void fillEllipse(Color color, Rectangle rect) {
        fillEllipse(color, rect.x, rect.y, rect.width, rect.height);
    }

    public native void drawArc(Pen pen, int x, int y, int width, int height, int startAngle, int arcAngle);

    public void drawArc(Pen pen, Rectangle rect, int startAngle, int arcAngle) {
        drawArc(pen, rect.x, rect.y, rect.width, rect.height, startAngle, arcAngle);
    }

    public native void fillArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle);

    public void fillArc(Color color, Rectangle rect, int startAngle, int arcAngle) {
        fillArc(color, rect.x, rect.y, rect.width, rect.height, startAngle, arcAngle);
    }

    public native void drawPolyline(Pen pen, int[] xPoints, int[] yPoints, int nPoints);

    public native void drawPolygon(Pen pen, int[] xPoints, int[] yPoints, int nPoints);

    public void drawPolygon(Pen pen, Polygon polygon) {
        drawPolygon(pen, polygon.xpoints, polygon.ypoints, polygon.npoints);
    }

    public native void fillPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints);

    public void fillPolygon(Color color, Polygon p) {
        fillPolygon(color, p.xpoints, p.ypoints, p.npoints);
    }

    public native void drawString(String str, Font font, Color color, int x, int y);

    public native void drawImage(Image img, int x, int y);

    public native void drawImage(Image img, int x, int y, int width, int height);

    public void drawImage(Image img, Point p) {
        drawImage(img, p.x, p.y);
    }

    public void drawImage(Image img, Rectangle rect) {
        drawImage(img, rect.x, rect.y, rect.width, rect.height);
    }
}
