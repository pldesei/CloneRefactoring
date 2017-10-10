This clone instance is located in File: 
src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
The line range of this clone instance is: 130-135
The content of this clone instance is as follows:
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(host,port);
        setSocket(sock);
        return wrapSocket(sock);
    }
