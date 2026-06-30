package flint.ui;

import flint.drawing.Color;

public class DarkTheme extends Theme {
    private Color color = new Color(0xFF198037);
    private Color textColor = new Color(0xFFA0A0A0);
    private Color backColor = new Color(0xFF101010);
    private Color cardBackColor = new Color(0xFF202020);
    private Color borderColor = new Color(0xFF242424);

    public DarkTheme() {

    }

    public Color getColor() {
        return color;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getBackColor() {
        return backColor;
    }

    public Color getCardBackColor() {
        return cardBackColor;
    }

    public Color getBorderBackColor() {
        return borderColor;
    }
}
