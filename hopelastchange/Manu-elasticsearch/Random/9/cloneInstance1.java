(startLine=69 endLine=80 srcPath=/root/Projects/newestVersion/elasticsearch/00001/elasticsearch/qa/smoke-test-http/src/test/java/org/elasticsearch/http/CorsRegexIT.java)
    public void testThatRegularExpressionReturnsForbiddenOnNonMatch() throws IOException {
        try {
            getRestClient().performRequest("GET", "/", new BasicHeader("User-Agent", "Mozilla Bar"),
                    new BasicHeader("Origin", "http://evil-host:9200"));
            fail("request should have failed");
        } catch(ResponseException e) {
            Response response = e.getResponse();
            // a rejected origin gets a FORBIDDEN - 403
            assertThat(response.getStatusLine().getStatusCode(), is(403));
            assertThat(response.getHeader("Access-Control-Allow-Origin"), nullValue());
        }
    }
