package flint.net;

import java.net.*;

/**
 * FlintJVM can access non-public classes via Class.forName.
 * This could be because the access validation feature has not been implemented at the VM layer.
 * Which could be considered a bug, and FlintInetAddressImpl is exploiting this bug.
 */
/* public */ class FlintInetAddressImpl implements InetAddressImpl {
    public FlintInetAddressImpl() { }
    public native String getLocalHostName() throws UnknownHostException;
    public native InetAddress[] lookupAllHostAddr(String hostname) throws UnknownHostException;
    public native String getHostByAddr(byte[] addr) throws UnknownHostException;
    public native InetAddress anyLocalAddress();
}
