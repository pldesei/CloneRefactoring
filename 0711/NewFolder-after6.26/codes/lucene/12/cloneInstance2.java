    for (Map.Entry<String, Object> entry : fieldNamesNL) {
      Analysis analysis = new Analysis();
      NamedList fieldNameNL = (NamedList) entry.getValue();
      NamedList<Object> queryNL = (NamedList<Object>) fieldNameNL.get("query");
      List<AnalysisPhase> phases = (queryNL == null) ? null : buildPhases(queryNL);
      analysis.setQueryPhases(phases);
      NamedList<Object> indexNL = (NamedList<Object>) fieldNameNL.get("index");
      phases = buildPhases(indexNL);
      analysis.setIndexPhases(phases);
      String fieldName = entry.getKey();
      analysisByFieldName.put(fieldName, analysis);
    }
