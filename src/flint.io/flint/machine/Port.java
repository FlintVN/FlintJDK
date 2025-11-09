package flint.machine;

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
        int m = switch(mode) {
            case INPUT -> 0;
            case OUTPUT -> 1;
            case INPUT_PULL_UP -> 2;
            case INPUT_PULL_DOWN -> 3;
            default -> 4;
        };
        Port.setMode(pins, m);
        return this;
    }
}
