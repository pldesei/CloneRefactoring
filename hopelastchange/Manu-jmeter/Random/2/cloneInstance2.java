(startLine=358 endLine=368 srcPath=/home/ubuntu/OurTask/jmeter/1/jmeter/src/core/org/apache/jmeter/visualizers/RunningSample.java)
    public String toString() {
        StringBuilder mySB = new StringBuilder();

        mySB.append("Samples: " + this.getNumSamples() + "  ");
        mySB.append("Avg: " + this.getAverage() + "  ");
        mySB.append("Min: " + this.getMin() + "  ");
        mySB.append("Max: " + this.getMax() + "  ");
        mySB.append("Error Rate: " + this.getErrorPercentageString() + "  ");
        mySB.append("Sample Rate: " + this.getRateString());
        return mySB.toString();
    }
