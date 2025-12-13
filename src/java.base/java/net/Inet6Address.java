package java.net;

public final class Inet6Address extends InetAddress {
    private byte[] address;
    private int scopeId;
    private boolean scopeIdSet;

    Inet6Address(String hostName, byte[] addr) {
        super(hostName, IPv6);
        address = addr;
        this.scopeId = 0;
        this.scopeIdSet = false;
    }

    Inet6Address(String hostName, byte[] addr, int scopeId) {
        super(hostName, IPv6);
        address = addr;
        this.scopeId = scopeId;
        this.scopeIdSet = true;
    }

    Inet6Address(String hostName, byte[] addr, int scopeId, boolean scopeIdSet) {
        super(hostName, IPv6);
        address = addr;
        this.scopeId = scopeId;
        this.scopeIdSet = scopeIdSet;
    }

    static Inet6Address parseAddressString(String src) {
        // TODO
        return null;
    }

    @Override
    public boolean isMulticastAddress() {
        return ((address[0] & 0xff) == 0xff);
    }

    @Override
    public byte[] getAddress() {
        return address.clone();
    }

    @Override
    public String getHostAddress() {
        StringBuilder sb = new StringBuilder(scopeId >= 0 ? 39 : 50);
        byte[] address = this.address;
        int len = 8;
        for(int i = 0; i < len; i++) {
            sb.append(Integer.toHexString(((address[i << 1] << 8) & 0xff00) | (address[(i << 1) + 1] & 0xff)));
            if(i < len - 1)
                sb.append(":");
        }
        if(scopeIdSet) {
            sb.append("%");
            sb.append(scopeId);
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        int i = 0;
        byte[] address = this.address;
        while(i < address.length) {
            int j = 0;
            int component = 0;
            while(j < 4 && i < address.length) {
                component = (component << 8) + address[i];
                j++;
                i++;
            }
            hash += component;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Inet6Address inetAddr) {
            byte[] addr1 = this.address;
            byte[] addr2 = inetAddr.address;
            for(int i = 0; i < address.length; i++) {
                if(addr1[i] != addr2[i])
                    return false;
            }
            return true;
        }
        return false;
    }
}
