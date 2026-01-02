package java.net;

public interface InetAddressImpl {
    String getLocalHostName() throws UnknownHostException;
    InetAddress[] lookupAllHostAddr(String hostname) throws UnknownHostException;
    String getHostByAddr(byte[] addr) throws UnknownHostException;
    InetAddress anyLocalAddress();
}
