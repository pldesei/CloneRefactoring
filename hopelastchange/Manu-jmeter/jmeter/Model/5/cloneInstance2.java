(startLine=138 endLine=143 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java)
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(address, port, localAddress, localPort);
        setSocket(sock);
        return wrapSocket(sock);
    }
