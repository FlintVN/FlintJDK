package flint.machine;

import java.io.IOException;

public class Adc {
    private String name;
    private int adcId = -1;
    private int channel;

    private native void initAdc();

    public Adc(String name) {
        this(name, -1);
    }

    public Adc(String name, int channel) {
        this.name = name;
        this.channel = channel;
        initAdc();
    }

    public native int read();
}
