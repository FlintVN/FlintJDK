package java.util.zip;

class ZStreamRef {
    private volatile int address;

    ZStreamRef(int address) {
        this.address = address;
    }

    int address() {
        return address;
    }

    void clear() {
        address = 0;
    }
}
