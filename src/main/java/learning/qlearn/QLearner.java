package learning.qlearn;

import actionselection.AbstractActionSelectionStrategy;
import actionselection.ActionSelectionStrategy;
import actionselection.EpsilonGreedyActionSelectionStrategy;
import actionselection.ActionSelectionStrategyFactory;
import models.QModel;
import util.IndexValue;

import java.io.Serializable;
import java.util.Set;

public class QLearner implements Serializable,Cloneable {
    protected QModel model;

    private ActionSelectionStrategy actionSelectionStrategy = new EpsilonGreedyActionSelectionStrategy();

    public QLearner makeCopy(){
        QLearner clone = new QLearner();
        clone.copy(this);
        return clone;
    }



    public void copy(QLearner rhs){
        model = rhs.model.makeCopy();
        actionSelectionStrategy = (ActionSelectionStrategy)((AbstractActionSelectionStrategy) rhs.actionSelectionStrategy).clone();
    }

    @Override
    public boolean equals(Object obj){
        if(obj !=null && obj instanceof QLearner){
            QLearner rhs = (QLearner)obj;
            if(!model.equals(rhs.model)) return false;
            return actionSelectionStrategy.equals(rhs.actionSelectionStrategy);
        }
        return false;
    }

    public QModel getModel() {
        return model;
    }

    public void setModel(QModel model) {
        this.model = model;
    }


    public String getActionSelection() {
        return ActionSelectionStrategyFactory.serialize(actionSelectionStrategy);
    }

    public void setActionSelection(String conf) {
        this.actionSelectionStrategy = ActionSelectionStrategyFactory.deserialize(conf);
    }

    public QLearner(){

    }

    public QLearner(int stateCount, int actionCount){
        this(stateCount, actionCount, 0.1, 0.7, 0.1);
    }

    public QLearner(QModel model, ActionSelectionStrategy actionSelectionStrategy){
        this.model = model;
        this.actionSelectionStrategy = actionSelectionStrategy;
    }

    public QLearner(int stateCount, int actionCount, double alpha, double gamma, double initialQ)
    {
        model = new QModel(stateCount, actionCount, initialQ);
        model.setAlpha(alpha);
        model.setGamma(gamma);
        actionSelectionStrategy = new EpsilonGreedyActionSelectionStrategy();
    }


    protected double maxQAtState(int stateId, Set<Integer> actionsAtState){
        IndexValue iv = model.actionWithMaxQAtState(stateId, actionsAtState);
        double maxQ = iv.getValue();
        return maxQ;
    }

    public IndexValue selectAction(int stateId, Set<Integer> actionsAtState){
        return actionSelectionStrategy.selectAction(stateId, model, actionsAtState);
    }

    public IndexValue selectAction(int stateId){
        return selectAction(stateId, null);
    }


    public void update(int stateId, int actionId, int nextStateId, double immediateReward){
        update(stateId, actionId, nextStateId, null, immediateReward);
    }

    public void update(int stateId, int actionId, int nextStateId, Set<Integer> actionsAtNextStateId, double immediateReward)
    {
        // old_value is $Q_t(s_t, a_t)$
        double oldQ = model.getQ(stateId, actionId);

        // learning_rate;
        double alpha = model.getAlpha(stateId, actionId);

        // discount_rate;
        double gamma = model.getGamma();

        // estimate_of_optimal_future_value is $max_a Q_t(s_{t+1}, a)$
        double maxQ = maxQAtState(nextStateId, actionsAtNextStateId);

        // learned_value = immediate_reward + gamma * estimate_of_optimal_future_value
        // old_value = oldQ
        // temporal_difference = learned_value - old_value
        // new_value = old_value + learning_rate * temporal_difference
        double newQ = oldQ + alpha * (immediateReward + gamma * maxQ - oldQ);

        // new_value is $Q_{t+1}(s_t, a_t)$
        model.setQ(stateId, actionId, newQ);
    }



}