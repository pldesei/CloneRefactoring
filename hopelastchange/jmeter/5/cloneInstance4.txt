This clone instance is located in File: 
src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
The line range of this clone instance is: 182-189
The content of this clone instance is as follows:
    public Socket createSocket(String host, int port, InetAddress inetAddress, int localPort)
            throws IOException, UnknownHostException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(host, port, inetAddress, localPort);
        setSocket(sock);
        return wrapSocket(sock);

    }
