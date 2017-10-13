This clone method is located in File: 
solr/solrj/src/java/org/apache/solr/client/solrj/io/eval/ConvolutionEvaluator.java
The line range of this clone method is: 45-70
The content of this clone method is as follows:
  public List<Number> evaluate(Tuple tuple) throws IOException {

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

    double[] conArray = MathArrays.convolve(column1, column2);
    List<Number> conList = new ArrayList();
    for(double d :conArray) {
      conList.add(d);
    }

    return conList;
  }
