package flint.drawing;

public interface GraphicsFactory {
    Graphics createGraphic(int width, int height, byte[] buff);
}
