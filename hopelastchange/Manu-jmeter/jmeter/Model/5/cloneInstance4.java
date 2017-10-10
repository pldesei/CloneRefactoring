(startLine=182 endLine=189 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/util/HttpSSLProtocolSocketFactory.java)
    public Socket createSocket(String host, int port, InetAddress inetAddress, int localPort)
            throws IOException, UnknownHostException {
        SSLSocketFactory sslfac = getSSLSocketFactory();
        Socket sock=sslfac.createSocket(host, port, inetAddress, localPort);
        setSocket(sock);
        return wrapSocket(sock);

    }
