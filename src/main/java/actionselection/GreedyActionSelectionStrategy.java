package actionselection;

import models.QModel;
import util.IndexValue;

import java.util.Set;

public class GreedyActionSelectionStrategy extends AbstractActionSelectionStrategy {
    @Override
    public IndexValue selectAction(int stateId, QModel model, Set<Integer> actionsAtState) {
        return model.actionWithMaxQAtState(stateId, actionsAtState);
    }

    @Override
    public Object clone(){
        GreedyActionSelectionStrategy clone = new GreedyActionSelectionStrategy();
        return clone;
    }

    @Override
    public boolean equals(Object obj){
        return obj != null && obj instanceof GreedyActionSelectionStrategy;
    }
}
