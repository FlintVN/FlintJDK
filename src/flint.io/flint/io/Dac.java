package flint.io;

import java.io.IOException;

public class Dac {
    private String name;
    private int dacId = -1;

    private native void initDac();

    public Dac(String name) {
        this.name = name;
        initDac();
    }

    public native void write(int value);
}
