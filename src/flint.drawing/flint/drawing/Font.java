package flint.drawing;

import java.io.*;

public class Font {
    private byte[] fontData;

    public Font(String fontPath) throws IOException {
        try(FileInputStream inputStream = new FileInputStream(fontPath)) {
            initFontData(inputStream);
        }
        catch(IOException e) {
            throw e;
        }
    }

    public Font(InputStream inputStream) throws IOException {
        initFontData(inputStream);
    }

    private void initFontData(InputStream inputStream) throws IOException {
        fontData = inputStream.readAllBytes();
    }
}
