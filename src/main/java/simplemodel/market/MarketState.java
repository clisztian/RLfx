package simplemodel.market;


/**
 * q-value is only a function of the position and probability, and the action taken
 */
public class MarketState {

    private QLearnMarketMaker.Positions position;

    private int fill_probability_index;
    private float entry_price = 0f;
    private float best_bid = 0f;
    private float best_ask = 0f;

    private int timestamp = 0;



    public float[] getFill_probs() {
        return fill_probs;
    }

    private final float[] fill_probs = new float[] {0f , 0.11111111f, 0.22222222f, 0.33333333f, 0.44444444f,
            0.55555556f, 0.66666667f, 0.77777778f, 0.88888889f, 1f};
    public MarketState(QLearnMarketMaker.Positions position, int fill_probability_index, float entry_price, float best_bid, float best_ask) {
        this.position = position;
        this.fill_probability_index = fill_probability_index;
        this.entry_price = entry_price;
        this.best_bid = best_bid;
        this.best_ask = best_ask;
    }

    public MarketState(QLearnMarketMaker.Positions position, float best_ask, float best_ask_vol, float best_bid, float best_bid_vol) {
        this.position = position;
        float ratio = best_bid_vol / ( best_ask_vol + best_bid_vol);

        fill_probability_index = (int) Math.floor(ratio * 10f);
        this.best_bid = best_bid;
        this.best_ask = best_ask;
    }

    //initial state
    public MarketState(float best_ask, float best_ask_vol, float best_bid, float best_bid_vol) {

        this.position = QLearnMarketMaker.Positions.FLAT;
        float ratio = best_bid_vol / ( best_ask_vol + best_bid_vol);

        fill_probability_index = (int) Math.floor(ratio * 10);
        this.best_bid = best_bid;
        this.best_ask = best_ask;
        this.entry_price = -1f;
        timestamp = 0;
    }

    public QLearnMarketMaker.Positions getPosition() {
        return position;
    }

    public void setPosition(QLearnMarketMaker.Positions position) {
        this.position = position;
    }

    public int getFill_probability_index() {
        return fill_probability_index;
    }

    public void setFill_probability_index(int fill_probability_index) {
        this.fill_probability_index = fill_probability_index;
    }

    public float getEntry_price() {
        return entry_price;
    }

    public void setEntry_price(float entry_price) {
        this.entry_price = entry_price;
    }

    public float getBest_bid() {
        return best_bid;
    }

    public void setBest_bid(float best_bid) {
        this.best_bid = best_bid;
    }

    public float getBest_ask() {
        return best_ask;
    }

    public void setBest_ask(float best_ask) {
        this.best_ask = best_ask;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
