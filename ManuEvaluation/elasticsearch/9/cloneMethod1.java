This clone method is located in File: 
qa/smoke-test-http/src/test/java/org/elasticsearch/http/CorsRegexIT.java
The line range of this clone method is: 69-80
The content of this clone method is as follows:
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
