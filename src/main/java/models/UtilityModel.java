package models;

import util.Vec;

import java.io.Serializable;

public class UtilityModel implements Serializable {
    private Vec U;
    private int stateCount;
    private int actionCount;

    public void setU(Vec U){
        this.U = U;
    }

    public Vec getU() {
        return U;
    }

    public double getU(int stateId){
        return U.get(stateId);
    }

    public int getStateCount() {
        return stateCount;
    }

    public int getActionCount() {
        return actionCount;
    }

    public UtilityModel(int stateCount, int actionCount, double initialU){
        this.stateCount = stateCount;
        this.actionCount = actionCount;
        U = new Vec(stateCount);
        U.setAll(initialU);
    }

    public UtilityModel(int stateCount, int actionCount){
        this(stateCount, actionCount, 0.1);
    }

    public UtilityModel(){

    }

    public void copy(UtilityModel rhs){
        U = rhs.U==null ? null : rhs.U.makeCopy();
        actionCount = rhs.actionCount;
        stateCount = rhs.stateCount;
    }

    public UtilityModel makeCopy(){
        UtilityModel clone = new UtilityModel();
        clone.copy(this);
        return clone;
    }

    @Override
    public boolean equals(Object rhs){
        if(rhs != null && rhs instanceof  UtilityModel){
            UtilityModel rhs2 = (UtilityModel)rhs;
            if(actionCount != rhs2.actionCount || stateCount != rhs2.stateCount) return false;

            if((U==null && rhs2.U!=null) && (U!=null && rhs2.U ==null)) return false;
            return !(U != null && !U.equals(rhs2.U));

        }
        return false;
    }

    public void reset(double initialU){
        U.setAll(initialU);
    }
}
