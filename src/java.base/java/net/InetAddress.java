package java.net;

import java.lang.reflect.*;
import java.util.Hashtable;

public sealed abstract class InetAddress permits Inet4Address, Inet6Address {
    protected static final int IPv4 = 1;
    protected static final int IPv6 = 2;

    static Hashtable<String, InetAddress[]> cache = new Hashtable<>();
    static InetAddressImpl impl;
    static InetAddress localHost;
    private static InetAddress loopbackHost;

    private String hostName;
    private int family;

    static {
        try {
            impl = (InetAddressImpl)Class.forName("flint.net.FlintInetAddressImpl").getConstructor().newInstance();
        }
        catch(ClassNotFoundException e) {
            System.out.println("Class not found: flint.net.FlintInetAddressImpl");
        }
        catch(NoSuchMethodException e) {
            System.out.println("Method not found: flint.net.FlintInetAddressImpl.<init>()");
        }
        catch(InstantiationException | IllegalArgumentException | InvocationTargetException e) {
            System.out.println("Could not instantiate: flint.net.FlintInetAddressImpl");
        }
        catch(IllegalAccessException e) {
            System.out.println("Cannot access class: flint.net.FlintInetAddressImpl");
        }
    }

    protected InetAddress(String hostName, int family) {
        this.hostName = hostName;
        this.family = family;
    }

    @SuppressWarnings("unchecked")
    public String getHostName() {
        if(hostName == null) {
            try {
                hostName = impl.getHostByAddr(getAddress());
                InetAddress[] arr = cache.get(hostName);
                if(arr != null) {
                    for(int i = 0; i < arr.length; i++) {
                        if(hostName.equalsIgnoreCase(arr[i].hostName) && !equals(arr[i])) {
                            hostName = getHostAddress();
                            break;
                        }
                    }
                }
                else
                    cache.put(hostName, new InetAddress[] {this});
            }
            catch(UnknownHostException e) {
                hostName = getHostAddress();
            }
        }
        return hostName;
    }

    public abstract byte[] getAddress();

    public abstract String getHostAddress();

    public abstract boolean isMulticastAddress();

    public int hashCode() {
        return -1;
    }

    public boolean equals(Object obj) {
        return false;
    }

    public String toString() {
        return getHostName() + "/" + getHostAddress();
    }

    public static InetAddress getByAddress(byte[] addr) throws UnknownHostException {
        return getByAddress(null, addr);
    }

    public static InetAddress getByAddress(String host, byte[] addr) throws UnknownHostException {
        if(host != null && !host.isEmpty() && host.charAt(0) == '[') {
            if(host.charAt(host.length()-1) == ']')
                host = host.substring(1, host.length() -1);
        }
        if(addr != null) {
            if(addr.length == 4)
                return new Inet4Address(host, addr);
            else if(addr.length == 6) {
                byte[] newAddr = convertFromIPv4MappedAddress(addr);
                if(newAddr != null)
                    return new Inet4Address(host, newAddr);
                else
                    return new Inet6Address(host, addr);
            }
        }
        throw new UnknownHostException("addr is of illegal length");
    }

    private static byte[] convertFromIPv4MappedAddress(byte[] addr) {
        if(isIPv4MappedAddress(addr)) {
            byte[] newAddr = new byte[4];
            newAddr[0] = addr[12];
            newAddr[1] = addr[13];
            newAddr[2] = addr[14];
            newAddr[3] = addr[15];
            return newAddr;
        }
        return null;
    }

    private static boolean isIPv4MappedAddress(byte[] addr) {
        if(addr.length < 16)
            return false;
        if(
            (addr[0] == 0x00) && (addr[1] == 0x00) &&
            (addr[2] == 0x00) && (addr[3] == 0x00) &&
            (addr[4] == 0x00) && (addr[5] == 0x00) &&
            (addr[6] == 0x00) && (addr[7] == 0x00) &&
            (addr[8] == 0x00) && (addr[9] == 0x00) &&
            (addr[10] == (byte)0xff) && (addr[11] == (byte)0xff)
        ) {
            return true;
        }
        return false;
    }

    public static InetAddress getByName(String host) throws UnknownHostException {
        return InetAddress.getAllByName(host)[0];
    }

    public static InetAddress[] getAllByName(String host) throws UnknownHostException {
        if(host == null || host.length() == 0) {
            InetAddress[] ret = new InetAddress[1];
            ret[0] = getLoopbackHost();
            return ret;
        }

        boolean ipv6Expected = false;
        if(host.charAt(0) == '[') {
            if(host.length() > 2 && host.charAt(host.length() - 1) == ']') {
                host = host.substring(1, host.length() -1);
                ipv6Expected = true;
            }
            else
                throw new UnknownHostException("invalid IPv6 address");
        }

        InetAddress inetAddress = null;
        if(!ipv6Expected)
            inetAddress = Inet4Address.parseAddressString(host);
        if(inetAddress == null)
            inetAddress = Inet4Address.parseAddressString(host);
        if(inetAddress != null)
            return new InetAddress[] {inetAddress};

        return getAllByName0(host);
    }

    private static InetAddress[] getAllByName0(String host) throws UnknownHostException {
        InetAddress[] obj = null;
        InetAddress[] objcopy = null;

        synchronized(cache) {
            obj = cache.get(host);
            if(obj == null) {
                try {
                    obj = impl.lookupAllHostAddr(host);
                }
                catch(UnknownHostException e) {

                }
                if(obj != null)
                    cache.put(host, obj);
            }
        }
        if(obj == null)
            throw new UnknownHostException(host);
        return obj.clone();
    }

    public static InetAddress getLocalHost() throws UnknownHostException {
        if(localHost == null) {
            try {
                localHost = getAllByName(impl.getLocalHostName())[0];
            }
            catch(Exception ex) {

            }
        }
        if(localHost == null)
            throw new UnknownHostException();
        return localHost;
    }

    static InetAddress anyLocalAddress() {
        return impl.anyLocalAddress();
    }

    private static InetAddress getLoopbackHost() {
        if(loopbackHost == null)
            loopbackHost = new Inet4Address("localhost", new byte[] {0x7F, 0x00, 0x00, 0x01});
        return loopbackHost;
    }
}
