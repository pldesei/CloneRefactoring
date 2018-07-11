package edu.pku.sei.codeclone.predictor;

public class CloneVersion implements Comparable<CloneVersion>{
    int version;
    String flag;
    public CloneVersion(int version, String flag){
        this.version = version;
        this.flag = flag;
    }
    @Override
    public int compareTo(CloneVersion arg0) {
        // TODO Auto-generated method stub
        if(this.version > arg0.version){
            return 1;
        }else if(this.version < arg0.version){
            return -1;
        }else{
            return 0;
        }
    }
    

}
