package java.net;

import java.lang.reflect.*;
import java.util.Hashtable;

public final class InetAddress {
    static Hashtable<String, InetAddress[]> addressCache = new Hashtable<>();
    static InetAddress unknownAddress;
    static InetAddress anyLocalAddress;
    static InetAddress localHost;
    static InetAddress[] unknown_array;
    static InetAddressImpl impl;
    private static InetAddress loopbackHost;

    String hostName;
    int address;
    int family;

    static {
        try {
            impl = null;
            impl = (InetAddressImpl)Class.forName("java.net.FlintInetAddressImpl").getConstructor().newInstance();
        }
        catch(ClassNotFoundException e) {
            System.out.println("Class not found: java.net.FlintInetAddressImpl");
        }
        catch(NoSuchMethodException e) {
            System.out.println("Method not found: java.net.FlintInetAddressImpl.<init>()");
        }
        catch(InstantiationException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println("Could not instantiate: java.net.FlintInetAddressImpl");
        }
        catch(IllegalAccessException e) {
            System.out.println("Cannot access class: java.net.FlintInetAddressImpl");
        }

        if(impl == null) {
            try {
                impl = new InetAddressImpl();
            }
            catch(Exception e) {
                throw new Error("System property impl.prefix incorrect");
            }
        }

        unknownAddress = new InetAddress();
        anyLocalAddress = new InetAddress();
        impl.makeAnyLocalAddress(anyLocalAddress);
        byte[] IP = new byte[4];
        IP[0] = 0x7F;
        IP[1] = 0x00;
        IP[2] = 0x00;
        IP[3] = 0x01;
        loopbackHost = new InetAddress("localhost", IP);

        try {
            localHost = new InetAddress();
            localHost.hostName = impl.getLocalHostName();
            localHost.address = -1;
        }
        catch(Exception ex) {
            localHost = unknownAddress;
        }

        String unknownByAddr = new String("0.0.0.0");
        unknown_array = new InetAddress[1];
        unknown_array[0] = new InetAddress(unknownByAddr, unknownAddress.getAddress());
        addressCache.put(unknownByAddr, unknown_array);
    }

    InetAddress() {
        family = impl.getInetFamily();
    }

    InetAddress(String hostName, byte addr[]) {
        this.hostName = new String(hostName);
        this.family = impl.getInetFamily();

        address  = addr[3] & 0xFF;
        address |= ((addr[2] << 8) & 0xFF00);
        address |= ((addr[1] << 16) & 0xFF0000);
        address |= ((addr[0] << 24) & 0xFF000000);
    }

    public boolean isMulticastAddress() {
        return ((address & 0xf0000000) == 0xe0000000);
    }

    @SuppressWarnings("unchecked")
    public String getHostName() {
        if(hostName == null) {
            try {
                hostName = new String(impl.getHostByAddr(address));
                InetAddress[] arr = addressCache.get(hostName);
                if(arr != null) {
                    for(int i = 0; i < arr.length; i++) {
                        if(hostName.equalsIgnoreCase(arr[i].hostName) && address != arr[i].address) {
                            hostName = getHostAddress();
                            break;
                        }
                    }
                }
                else {
                    arr = new InetAddress[1];
                    arr[0] = this;
                    addressCache.put(hostName, arr);
                }
            }
            catch(UnknownHostException e) {
                hostName = getHostAddress();
            }
        }
        return hostName;
    }

    public byte[] getAddress() {
        byte[] addr = new byte[4];

        addr[0] = (byte) ((address >>> 24) & 0xFF);
        addr[1] = (byte) ((address >>> 16) & 0xFF);
        addr[2] = (byte) ((address >>> 8) & 0xFF);
        addr[3] = (byte) (address & 0xFF);
        return addr;
    }

    public String getHostAddress() {
        return ((address >>> 24) & 0xFF) + "." + ((address >>> 16) & 0xFF) + "." + ((address >>>  8) & 0xFF) + "." + ((address >>>  0) & 0xFF);
    }

    public int hashCode() {
        return address;
    }

    public boolean equals(Object obj) {
        return (obj != null) && (obj instanceof InetAddress) && (((InetAddress)obj).address == address);
    }

    public String toString() {
        return getHostName() + "/" + getHostAddress();
    }

    public static InetAddress getByName(String host)
        throws UnknownHostException {
        Object obj = null;
        if(host == null || host.length() == 0)
            return loopbackHost;

        if(!Character.isDigit(host.charAt(0)))
            return getAllByName0(host)[0];
        else {
            int IP = 0x00;
            int hitDots = 0;
            char[] data = host.toCharArray();

            for(int i = 0; i < data.length; i++) {
                char c = data[i];
                if(c < 48 || c > 57)
                    return getAllByName0(host)[0];
                int b = 0x00;
                while(c != '.') {
                    if(c < 48 || c > 57)
                        return getAllByName0(host)[0];
                    b = b * 10 + c - '0';

                    if(++i >= data.length)
                        break;
                    c = data[i];
                }
                if(b > 0xFF)
                    return getAllByName0(host)[0];
                IP = (IP << 8) + b;
                hitDots++;
            }

            if(hitDots != 4 || host.endsWith("."))
                return getAllByName0(host)[0];

            InetAddress in = new InetAddress();
            in.address = IP;
            in.hostName = null;
            return in;
        }
    }

    public static InetAddress getAllByName(String host)[]
        throws UnknownHostException {

        if(host == null || host.length() == 0)
            throw new UnknownHostException("empty string");

        if(Character.isDigit(host.charAt(0))) {
            InetAddress[] ret = new InetAddress[1];
            ret[0] = getByName(host);
            return ret;
        }
        else
            return getAllByName0(host);
    }

    private static InetAddress[] getAllByName0(String host) throws UnknownHostException  {
        InetAddress[] obj = null;
        InetAddress[] objcopy = null;

        synchronized(addressCache) {
            obj = addressCache.get(host);
            if(obj == null) {
                try {
                    byte[][] byte_array = impl.lookupAllHostAddr(host);
                    InetAddress[] addr_array = new InetAddress[byte_array.length];

                    for(int i = 0; i < byte_array.length; i++) {
                        byte addr[] = byte_array[i];
                        addr_array[i] = new InetAddress(host, addr);
                    }
                    obj = addr_array;
                }
                catch(UnknownHostException e) {
                    obj  = unknown_array;
                }
                if(obj != unknown_array)
                    addressCache.put(host, obj);
            }
        }
        if(obj == unknown_array)
            throw new UnknownHostException(host);
        return obj.clone();
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        if(localHost.equals(unknownAddress))
            throw new UnknownHostException();

        if(localHost.address == -1)
            localHost = getAllByName(localHost.hostName)[0];
        return localHost;
    }
}

class InetAddressImpl {
    native String getLocalHostName() throws UnknownHostException;
    native void makeAnyLocalAddress(InetAddress addr);
    native byte[][] lookupAllHostAddr(String hostname) throws UnknownHostException;
    native String getHostByAddr(int addr) throws UnknownHostException;
    native int getInetFamily();
}
