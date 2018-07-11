        for (int k = 0; k < docs; k++) {
          UpdateRequest req = new UpdateRequest();
          for (; numDocs < (k + 1) * 100; numDocs++) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "source_" + numDocs);
            doc.addField("xyz", numDocs);
            req.add(doc);
          }
          req.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
          log.info("Adding " + docs + " docs with commit=true, numDocs=" + numDocs);
          req.process(sourceSolrClient);
        }
