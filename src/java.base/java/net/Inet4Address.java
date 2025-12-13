package java.net;

public final class Inet4Address extends InetAddress {
    private int address;

    Inet4Address(String hostName, byte[] addr) {
        super(hostName, IPv4);
        address = addr[3] & 0xFF;
        address |= ((addr[2] << 8) & 0xFF00);
        address |= ((addr[1] << 16) & 0xFF0000);
        address |= ((addr[0] << 24) & 0xFF000000);
    }

    @SuppressWarnings("fallthrough")
    static Inet4Address parseAddressString(String src) {
        byte[] res = new byte[4];

        long tmpValue = 0;
        int currByte = 0;
        boolean newOctet = true;

        int len = src.length();
        if(len == 0 || len > 15)
            return null;
        for(int i = 0; i < len; i++) {
            char c = src.charAt(i);
            if (c == '.') {
                if(newOctet || tmpValue < 0 || tmpValue > 0xff || currByte == 3)
                    return null;
                res[currByte++] = (byte) (tmpValue & 0xff);
                tmpValue = 0;
                newOctet = true;
            }
            else {
                int digit = ('0' <= c && c <= '9') ? (c - '0') : -1;
                if(digit < 0)
                    return null;
                tmpValue *= 10;
                tmpValue += digit;
                newOctet = false;
            }
        }
        if(newOctet || tmpValue < 0 || tmpValue >= (1L << ((4 - currByte) * 8)))
            return null;
        switch(currByte) {
            case 0:
                res[0] = (byte)((tmpValue >> 24) & 0xff);
            case 1:
                res[1] = (byte)((tmpValue >> 16) & 0xff);
            case 2:
                res[2] = (byte)((tmpValue >>  8) & 0xff);
            case 3:
                res[3] = (byte)((tmpValue >>  0) & 0xff);
        }

        return new Inet4Address(null, res);
    }

    @Override
    public boolean isMulticastAddress() {
        return ((address & 0xf0000000) == 0xe0000000);
    }

    @Override
    public byte[] getAddress() {
        byte[] addr = new byte[4];
        addr[0] = (byte) ((address >>> 24) & 0xFF);
        addr[1] = (byte) ((address >>> 16) & 0xFF);
        addr[2] = (byte) ((address >>> 8) & 0xFF);
        addr[3] = (byte) (address & 0xFF);
        return addr;
    }

    @Override
    public String getHostAddress() {
        return ((address >>> 24) & 0xFF) + "." + ((address >>> 16) & 0xFF) + "." + ((address >>>  8) & 0xFF) + "." + ((address >>>  0) & 0xFF);
    }

    @Override
    public int hashCode() {
        return address;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Inet4Address) && (((Inet4Address)obj).address == address);
    }
}
