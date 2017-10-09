This clone instance is located in File: 
src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java
The line range of this clone instance is: 174-179
The content of this clone instance is as follows:
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(host,port);
        setSocket(sock);
        return wrapSocket(sock);
    }
