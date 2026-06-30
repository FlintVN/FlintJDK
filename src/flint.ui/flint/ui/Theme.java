package flint.ui;

import flint.drawing.Font;
import flint.drawing.Color;
import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class Theme {
    static Theme defaultTheme = new DarkTheme();

    protected Font defaultFont;

    public Theme() {

    }

    public static Theme getDefaultTheme() {
        return defaultTheme;
    }

    public static void setDefaultTheme(Theme theme) {
        if(theme == null)
            throw new NullPointerException("theme cannot be null");
        defaultTheme = theme;
    }

    public abstract Color getColor();
    public abstract Color getTextColor();
    public abstract Color getBackColor();
    public abstract Color getCardBackColor();
    public abstract Color getBorderBackColor();

    public Font getFont() {
        if(defaultFont == null) {
            try {
                defaultFont = new Font("/sys/fonts/default.font");
            }
            catch(IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return defaultFont;
    }

    public int getBorderThickness() {
        return 1;
    }

    public int getCornerRadius() {
        return 4;
    }
}
