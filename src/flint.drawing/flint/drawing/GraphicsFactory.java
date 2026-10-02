package flint.drawing;

import java.io.InputStream;

public interface GraphicsFactory {
    Graphics createGraphic(int width, int height, byte[] buff);

    Image createImage(int width, int height);

    Image createImage(byte[] imageData, int off, int len);

    Image createImage(int[] rgb, int w, int h, boolean processAlpha);
}
