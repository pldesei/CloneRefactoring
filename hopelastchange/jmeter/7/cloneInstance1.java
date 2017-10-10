This clone instance is located in File: 
src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java
The line range of this clone instance is: 260-270
The content of this clone instance is as follows:
    public String toString() {
        StringBuilder mySB = new StringBuilder();

        mySB.append("Samples: " + this.getCount() + "  ");
        mySB.append("Avg: " + this.getMean() + "  ");
        mySB.append("Min: " + this.getMin() + "  ");
        mySB.append("Max: " + this.getMax() + "  ");
        mySB.append("Error Rate: " + this.getErrorPercentage() + "  ");
        mySB.append("Sample Rate: " + this.getRate());
        return mySB.toString();
    }
