  public static void assertJPut(String request, String content, double delta, String... tests) throws Exception {
    int queryStartPos = request.indexOf('?');
    String query;
    String path;
    if (-1 == queryStartPos) {
      query = "";
      path = request;
    } else {
      query = request.substring(queryStartPos + 1);
      path = request.substring(0, queryStartPos);
    }
    query = setParam(query, "wt", "json");
    request = path + '?' + setParam(query, "indent", "on");

    String response;
    boolean failed = true;
    try {
      response = restTestHarness.put(request, content);
      failed = false;
    } finally {
      if (failed) {
        log.error("REQUEST FAILED: " + request);
      }
    }

    for (String test : tests) {
      if (null == test || 0 == test.length()) continue;
      String testJSON = test.replaceAll("(?<!\\\\)\'", "\"");
      testJSON = testJSON.replaceAll("\\\\\'", "'");

      try {
        failed = true;
        String err = JSONTestUtil.match(response, testJSON, delta);
        failed = false;
        if (err != null) {
          log.error("query failed JSON validation. error=" + err +
              "\n expected =" + testJSON +
              "\n response = " + response +
              "\n request = " + request + "\n"
          );
          throw new RuntimeException(err);
        }
      } finally {
        if (failed) {
          log.error("JSON query validation threw an exception." +
              "\n expected =" + testJSON +
              "\n response = " + response +
              "\n request = " + request + "\n"
          );
        }
      }
    }
  }
