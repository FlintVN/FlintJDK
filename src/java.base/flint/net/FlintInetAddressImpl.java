package flint.net;

import java.net.*;

public class FlintInetAddressImpl implements InetAddressImpl {
    public FlintInetAddressImpl() { }
    public native String getLocalHostName() throws UnknownHostException;
    public native InetAddress[] lookupAllHostAddr(String hostname) throws UnknownHostException;
    public native String getHostByAddr(byte[] addr) throws UnknownHostException;
    public native InetAddress anyLocalAddress();
}
