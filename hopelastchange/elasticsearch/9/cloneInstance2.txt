This clone instance is located in File: 
qa/smoke-test-http/src/test/java/org/elasticsearch/http/CorsRegexIT.java
The line range of this clone instance is: 103-116
The content of this clone instance is as follows:
    public void testThatPreFlightRequestReturnsNullOnNonMatch() throws IOException {
        try {
            getRestClient().performRequest("OPTIONS", "/", new BasicHeader("User-Agent", "Mozilla Bar"),
                    new BasicHeader("Origin", "http://evil-host:9200"),
                    new BasicHeader("Access-Control-Request-Method", "GET"));
            fail("request should have failed");
        } catch(ResponseException e) {
            Response response = e.getResponse();
            // a rejected origin gets a FORBIDDEN - 403
            assertThat(response.getStatusLine().getStatusCode(), is(403));
            assertThat(response.getHeader("Access-Control-Allow-Origin"), nullValue());
            assertThat(response.getHeader("Access-Control-Allow-Methods"), nullValue());
        }
    }
