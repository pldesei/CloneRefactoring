This clone fragment is located in File: 
src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
The line range of this clone fragment is: 138-143
The content of this clone fragment is as follows:
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(address, port, localAddress, localPort);
        setSocket(sock);
        return wrapSocket(sock);
    }