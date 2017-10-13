This clone method is located in File: 
solr/solrj/src/java/org/apache/solr/client/solrj/io/eval/DistanceEvaluator.java
The line range of this clone method is: 46-66
The content of this clone method is as follows:
  public Number evaluate(Tuple tuple) throws IOException {

    StreamEvaluator colEval1 = subEvaluators.get(0);
    StreamEvaluator colEval2 = subEvaluators.get(1);

    List<Number> numbers1 = (List<Number>)colEval1.evaluate(tuple);
    List<Number> numbers2 = (List<Number>)colEval2.evaluate(tuple);
    double[] column1 = new double[numbers1.size()];
    double[] column2 = new double[numbers2.size()];

    for(int i=0; i<numbers1.size(); i++) {
      column1[i] = numbers1.get(i).doubleValue();
    }

    for(int i=0; i<numbers2.size(); i++) {
      column2[i] = numbers2.get(i).doubleValue();
    }

    EuclideanDistance distance = new EuclideanDistance();
    return distance.compute(column1, column2);
  }
