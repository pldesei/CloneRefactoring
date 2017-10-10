(startLine=260 endLine=270 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/visualizers/SamplingStatCalculator.java)
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
