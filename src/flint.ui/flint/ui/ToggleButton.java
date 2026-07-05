package flint.ui;

import flint.drawing.Image;
import flint.drawing.Color;
import flint.drawing.ClipMode;
import flint.drawing.Graphics;

public class ToggleButton extends View {
    private static final int DEFAULT_HEIGHT = 20;
    private static final int DEFAULT_RADIUS = 10;

    protected boolean checked;

    protected Color color;

    protected int borderThickness;
    protected Color borderColor;

    protected int cornerRadius;

    public ToggleButton() {
        color = Theme.defaultTheme.getColor();
        background = Theme.defaultTheme.getCardBackColor();
        borderThickness = 0;

        width = DEFAULT_HEIGHT * 2 - 4;
        height = DEFAULT_HEIGHT;

        cornerRadius = DEFAULT_RADIUS;
    }

    @Override
    protected void onDraw(Graphics g) {
        if(!isVisible(g)) return;

        int r = cornerRadius;
        Object bgColor = checked ? color : background;
        if(bgColor != null)
            g.fillRoundRect((Color)bgColor, this.x, this.y, actualWidth, actualHeight, r, r, r, r);

        if(borderThickness > 0 && borderColor != null && borderColor.getAlpha() > 0) {
            int w = actualWidth - borderThickness;
            int h = actualHeight - borderThickness;
            int tmp = borderThickness >>> 1;
            g.drawRoundRect(borderColor, borderThickness, this.x + tmp, this.y + tmp, w, h, r, r, r, r);
        }

        Color c = Theme.defaultTheme.getTextColor();
        if(c != null && c.getAlpha() > 0) {
            int h = actualHeight - (borderThickness << 1) - 6;
            int y = borderThickness + 3;
            int x = checked ? (actualWidth - y - h) : y;
            r = cornerRadius - borderThickness - 3;
            g.fillRoundRect(c, this.x + x, this.y + y, h, h, r, r, r, r);
        }
    }

    @Override
    protected void updateActualWidth(int availableW) {
        if((width == View.WRAP_CONTENT) || (width == View.MATCH_PARENT && availableW < 0))
            actualWidth = width >= 0 ? width : ((width == View.WRAP_CONTENT || availableW < 0) ? (DEFAULT_HEIGHT * 2 - 4) : availableW);
        else
            actualWidth = width >= 0 ? width : availableW;
    }

    @Override
    protected void updateActualHeight(int availableH) {
        if((height == View.WRAP_CONTENT) || (height == View.MATCH_PARENT && availableH < 0))
            actualHeight = height >= 0 ? height : ((height == View.WRAP_CONTENT || availableH < 0) ? DEFAULT_HEIGHT : availableH);
        else
            actualHeight = height >= 0 ? height : availableH;
    }

    @Override
    public void setBackground(Object bg) {
        if(bg == null)
            background = null;
        else if(bg instanceof Color)
            background = bg;
        else
            throw new IllegalArgumentException("background must be an instance of Color");
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getBorderThickness() {
        return borderThickness;
    }

    public void setBorderThickness(int thickness) {
        if(thickness < 0)
            throw new IllegalArgumentException("border thickness cannot be less than 0");
        borderThickness = thickness;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color color) {
        borderColor = color;
    }

    public int getColorRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(int radius) {
        cornerRadius = radius;
    }
}
