package flint.drawing;

public interface GraphicsFactory {
    Graphics createGraphic(int width, int height, byte[] buff);

    Color createColor(int a, int r, int g, int b);
}
