    for (Map.Entry<String, Object> entry : fieldTypesNL) {
      Analysis analysis = new Analysis();
      NamedList fieldTypeNL = (NamedList) entry.getValue();
      NamedList<Object> queryNL = (NamedList<Object>) fieldTypeNL.get("query");
      List<AnalysisPhase> phases = (queryNL == null) ? null : buildPhases(queryNL);
      analysis.setQueryPhases(phases);
      NamedList<Object> indexNL = (NamedList<Object>) fieldTypeNL.get("index");
      phases = buildPhases(indexNL);
      analysis.setIndexPhases(phases);
      String fieldTypeName = entry.getKey();
      analysisByFieldTypeName.put(fieldTypeName, analysis);
    }
