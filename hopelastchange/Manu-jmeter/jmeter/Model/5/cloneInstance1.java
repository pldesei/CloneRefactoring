(startLine=130 endLine=135 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java)
    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(host,port);
        setSocket(sock);
        return wrapSocket(sock);
    }
