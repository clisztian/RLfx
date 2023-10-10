package util;

public class IndexValue {
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    private double value;

    public IndexValue(){

    }

    public IndexValue(int index, double value){
        this.index = index;
        this.value = value;
    }

    public IndexValue makeCopy(){
        IndexValue clone = new IndexValue();
        clone.setValue(value);
        clone.setIndex(index);
        return clone;
    }

    @Override
    public boolean equals(Object rhs){
        if(rhs != null && rhs instanceof IndexValue){
            IndexValue rhs2 = (IndexValue)rhs;
            return index == rhs2.index && value == rhs2.value;
        }
        return false;
    }

    public boolean isValid(){
        return index != -1;
    }

}
