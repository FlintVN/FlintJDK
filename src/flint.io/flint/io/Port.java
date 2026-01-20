package flint.io;

public class Port {
    private final byte[] pins;

    private static native void setMode(byte[] pins, int mode);

    public native int read();

    public native void write(int value);

    public native void reset();

    public Port(byte... pins) {
        if(pins == null || (pins.length < 1) || (pins.length > 32)) {
            if(pins == null)
                throw new NullPointerException("pins array cannot be null object");
            else
                throw new NullPointerException("The pin number must be from 1 to 32");
        }
        this.pins = pins.clone();
    }

    public Port(int... pins) {
        if(pins == null || (pins.length < 1) || (pins.length > 32)) {
            if(pins == null)
                throw new NullPointerException("pins array cannot be null object");
            else
                throw new NullPointerException("The pin number must be from 1 to 32");
        }
        byte[] tmp = new byte[pins.length];
        for(int i = 0; i < pins.length; i++)
            tmp[i] = (byte)pins[i];
        this.pins = tmp;
    }

    public Port(Pin... pins) {
        if(pins == null || (pins.length < 1) || (pins.length > 32)) {
            if(pins == null)
                throw new NullPointerException("pins array cannot be null object");
            else
                throw new NullPointerException("The pin number must be from 1 to 32");
        }
        byte[] pinArray = new byte[pins.length];
        for(int i = 0; i < pins.length; i++)
            pinArray[i] = (byte)pins[i].pin;
        this.pins = pinArray;
    }

    public Port setMode(PinMode mode) {
        Port.setMode(pins, mode.value);
        return this;
    }
}
