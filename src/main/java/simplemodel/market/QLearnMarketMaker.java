package simplemodel.market;

import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class QLearnMarketMaker {

    Random rng = new Random();

    public enum Actions {
        SELL,
        HOLD,
        BUY
    }

    public enum Positions {
        FLAT,
        LONG,
        SHORT
    }

    //private final float eps = 0.5f;
    private final float alpha = 0.05f; // Learning rate
    private final float gamma = 1f; // Eagerness - 0 looks in the near future, 1 looks in the distant future


    private final int num_prob_steps = 10; //number discrete probs

    private float[][][] Qvalues;

    private final float[] fill_probs = new float[] {0f , 0.11f, 0.22f, 0.33f, 0.44f, 0.55f, 0.66f, 0.77f, 0.88f, 1f};

    private final float trans_cost = 0;

    private DecimalFormat df = new DecimalFormat("#.0000");

    private ArrayList<float[]> ebbo = new ArrayList<>();


    public void initQvalues() {

        Qvalues = new float[Positions.values().length][num_prob_steps][Actions.values().length];

    }

    public void readData() throws IOException {


        ebbo = new ArrayList<>();

        ClassLoader classLoader = QLearnMarketMaker.class.getClassLoader();
        File file = new File(classLoader.getResource("data/lob/AMZN_L1.csv").getFile());
        BufferedReader fis = new BufferedReader(new FileReader(file));

        String content;

        while ((content = fis.readLine()) != null) {


            String[] bbo = content.split("[,]+");

            float best_ask = Float.parseFloat(bbo[0]);
            float best_bid = Float.parseFloat(bbo[2]);

            float best_ask_vol = Float.parseFloat(bbo[1]);
            float best_bid_vol = Float.parseFloat(bbo[3]);

            ebbo.add(new float[] {best_ask/1000f, best_ask_vol, best_bid/1000f, best_bid_vol});
        }

        for(float[] o : ebbo) {
            System.out.println(df.format(o[0]) + " " + o[1] + " " + df.format(o[2]) + " " + o[3]);
        }
    }


    public Pair<MarketState, Float> step(MarketState current_state, Actions actions) {

        float reward = 0;
        float instant_pnl = 0;

        Positions next_pos = current_state.getPosition();
        float entry_price = current_state.getEntry_price();
        float exit_price = -100000f;

        boolean fill_bid = fill_probs[current_state.getFill_probability_index()] < rng.nextFloat();
        boolean fill_ask = !fill_bid;



        //compute the action given the current state
        if(actions == Actions.BUY && fill_bid) {

            if(current_state.getPosition().equals(Positions.FLAT)) { //buy since fill available

                next_pos = Positions.LONG;
                entry_price = current_state.getBest_bid();
                reward = -trans_cost;
            }
            else if(current_state.getPosition().equals(Positions.SHORT)) {// close position

                next_pos = Positions.FLAT;
                exit_price = current_state.getBest_bid();
                instant_pnl = entry_price - exit_price;
                reward = -trans_cost;
                entry_price = -100000f;
            }

        }
        else if(actions.equals(Actions.SELL) && fill_ask) {

            if(current_state.getPosition().equals(Positions.FLAT)) { //sell since fill available

                next_pos = Positions.SHORT;
                entry_price = current_state.getBest_ask();
                reward = -trans_cost;
            }
            else if(current_state.getPosition().equals(Positions.LONG)) {// close position

                next_pos = Positions.FLAT;
                exit_price = current_state.getBest_ask();
                instant_pnl = exit_price - entry_price;
                reward = -trans_cost;
                entry_price = -100000f;
            }

        }

        //compute the total reward
        reward += instant_pnl;


        if(current_state.getTimestamp() < ebbo.size()-1) {

            float[] bbo = ebbo.get(current_state.getTimestamp()+1);

            MarketState next_state = new MarketState(next_pos, bbo[0], bbo[1], bbo[2], bbo[3]);
            next_state.setEntry_price(entry_price);
            next_state.setTimestamp(current_state.getTimestamp()+1);

            //System.out.println(current_state.getTimestamp()+1 + " " + next_pos.name() + " " + next_state.getFill_probability_index() + " " + next_state.getEntry_price() + " " + exit_price + " " + reward);

            return Pair.of(next_state, reward);
        }
        else return null; //end of file


    }

    /**
     * Choose action based on current state, either random exploration or exploitation
     * @param current_state
     * @param eps governs exploration or exploitation
     * @return
     */
    public Actions chooseAction(MarketState current_state, float eps) {

        Actions next_action;
        //using eps, engage exploration with probability eps we choose randomly among allowed actions
        if(eps > rng.nextFloat()) {

            if(current_state.getPosition().equals(Positions.LONG)) {
                next_action = rng.nextFloat() < .5f ? Actions.SELL : Actions.HOLD;
            }
            else if(current_state.getPosition().equals(Positions.SHORT)) {
                next_action = rng.nextFloat() < .5f ? Actions.BUY : Actions.HOLD;
            }
            else {
                float coin = rng.nextFloat();
                next_action = coin < .33f ? Actions.BUY : (coin < .66f ? Actions.SELL : Actions.HOLD);
            }
        }
        else { //exploitation instead

            float[] policy_vals;
            if(current_state.getPosition().equals(Positions.LONG)) {

                policy_vals = Qvalues[Positions.LONG.ordinal()][current_state.getFill_probability_index()];
                next_action =  policy_vals[0] >= policy_vals[1] ? Actions.SELL : Actions.HOLD;

            }
            else if(current_state.getPosition().equals(Positions.SHORT)) {

                policy_vals = Qvalues[Positions.SHORT.ordinal()][current_state.getFill_probability_index()];
                next_action = policy_vals[1] >= policy_vals[2] ? Actions.HOLD : Actions.BUY;
            }
            else {

                policy_vals = Qvalues[Positions.FLAT.ordinal()][current_state.getFill_probability_index()];
                int max_val = policy_vals[0] < policy_vals[1] ? 1 : 0;
                max_val = policy_vals[2] < policy_vals[max_val] ? max_val : 2;

                if(max_val == 0) {
                    next_action = Actions.SELL;
                }
                else if(max_val == 1) {
                    next_action = Actions.HOLD;
                }
                else {
                    next_action = Actions.BUY;
                }
            }
            //System.out.println(policy_vals[0] + " " + policy_vals[1] + " " + policy_vals[2] + " " + next_action);

        }

        return next_action;

    }


    public float Q_learn(float eps) {

        float cumulative_reward = 0;
        MarketState current_state = getInitialState();


        int n_trades = 0;
        //iterations over the entire history of ebbo
        for(int iter = 0; iter < ebbo.size(); iter++) {

            //first choose the action given the current state;
            Actions action = chooseAction(current_state, eps);

            Pair<MarketState, Float> next = step(current_state, action);

            if(next == null) {
                System.out.println(n_trades + " " + cumulative_reward + ", avg ret: " + (cumulative_reward/(float)n_trades));
                return cumulative_reward;
            }

            MarketState next_state = next.getKey();

            float reward = next.getValue();
            cumulative_reward += reward;

            //update Q value

            float qval = Qvalues[current_state.getPosition().ordinal()][current_state.getFill_probability_index()][action.ordinal()];
            float[] policy = Qvalues[next_state.getPosition().ordinal()][next_state.getFill_probability_index()];

            qval += alpha*(reward +  gamma * max(policy) - qval);

            Qvalues[current_state.getPosition().ordinal()][current_state.getFill_probability_index()][action.ordinal()] = qval;

            current_state = next_state;

            if(reward > 0) n_trades++;
        }



        return cumulative_reward;
    }


    public MarketState getInitialState() {

        float[] bbo = ebbo.get(0);
        return new MarketState( bbo[0], bbo[1], bbo[2], bbo[3]);
    }

    public static float max(float[] v) {

        float _max = Float.MIN_VALUE;

        for(int i = 0; i < v.length; i++) {
            if(v[i] > _max) {
                _max = v[i];
            }
        }
        return _max;
    }


    public void Q_learning(int n_episodes) {

        //instantiate the rewards per episode and init q_vals
        float[] rewards = new float[n_episodes];
        initQvalues();

        float EPS = .5f;
        int epoch_length = 15;

        for(int episode = 0; episode < 150; episode++) {

            float eps = EPS * (float)Math.pow((1f - EPS), (float)episode/epoch_length);

            float reward = Q_learn(eps);

            rewards[episode] = reward;

            System.out.println(episode + " " + eps + " " + reward);
        }
    }


    public static void main(String[] args) {

        QLearnMarketMaker mm = new QLearnMarketMaker();
        try {
            mm.readData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        mm.Q_learning(150);

        System.out.println("Finished");
    }


}
