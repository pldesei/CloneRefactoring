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
