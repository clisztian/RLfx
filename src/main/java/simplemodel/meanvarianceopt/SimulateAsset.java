package simplemodel.meanvarianceopt;

import no.uib.cipr.matrix.DenseMatrix;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import timeseries.TimeSeries;
import timeseries.TimeSeriesEntry;

import java.util.ArrayList;
import java.util.Random;

/**
 * Simple model of simulation assets
 */
public class SimulateAsset {

    private float p_0; //initial price
    private float mu; //drift
    private float sigma; //volatility
    private float r; //risk-free rate
    private float M; //maturity

    private int n_days;
    private float delta;
    private int n_mc;

    private float gamma;
    private float lambda; //risk aversion

    private ArrayList<TimeSeries<Float>> series;
    private Random rng = new Random();


    public SimulateAsset(float p_0, float mu, float sigma, float r, float m, int n_days, float delta, int n_mc, float gamma, float lambda) {
        this.p_0 = p_0;
        this.mu = mu;
        this.sigma = sigma;
        this.r = r;
        M = m;
        this.n_days = n_days;
        this.delta = delta;
        this.n_mc = n_mc;
        this.gamma = gamma;
        this.lambda = lambda;
    }

    public SimulateAsset() {
        this.p_0 = 100f;
        this.mu = 0.03f;
        this.sigma = 0.20f;
        this.r = 0.01f;
        this.M = 1;
        this.n_days = 30;
        this.delta = (float)M/(float)n_days;

        this.n_mc = 2000;
        this.gamma = (float)Math.exp(-r * delta);
        this.lambda = 10f;
    }

    public ArrayList<TimeSeries<Float>> simulateSeries() {

        series = new ArrayList<TimeSeries<Float>>();
        for(int i = 0; i < n_mc; i++) {
            series.add(simulate());
        }
        return series;
    }

    private TimeSeries<Float> simulate() {

        DateTime now = DateTime.now();

        TimeSeries<Float> ts = new TimeSeries<>();
        ts.add(now.toString(), p_0);

        for(int t = 1; t < n_days; t++) {

            //S.loc[:, t] = S.loc[:, t-1] * np.exp((mu - 1/2 * sigma**2) * delta_t + sigma * np.sqrt(delta_t) * RN.loc[:, t])
            float prev = ts.get(t-1).getValue();
            float p = prev * (float)Math.exp((mu - .5f*sigma*sigma)*delta) + sigma * (float)Math.sqrt(delta)* (float)rng.nextGaussian();
            now = now.plusDays(1);

            TimeSeriesEntry<Float> entry = new TimeSeriesEntry<>(now.toString(), p);
            entry.setExtra(Math.log(p) - Math.log(prev));
            ts.add(entry);
        }

        return ts;
    }

    public Pair<DenseMatrix, DenseMatrix> getSeriesMatrix() {

        DenseMatrix S = new DenseMatrix(n_mc, n_days);
        DenseMatrix returns = new DenseMatrix(n_mc, n_days);

        for(int i = 0; i < n_mc; i++) {

            TimeSeries<Float> s = series.get(i);
            for(int t = 0; t < n_days; t++) {
                S.add(i, t, s.get(t).getValue());
                returns.add(i, t, s.get(t).getExtra());
            }
        }

        return Pair.of(S, returns);
    }

    public float getP_0() {
        return p_0;
    }

    public void setP_0(float p_0) {
        this.p_0 = p_0;
    }

    public float getMu() {
        return mu;
    }

    public void setMu(float mu) {
        this.mu = mu;
    }

    public float getSigma() {
        return sigma;
    }

    public void setSigma(float sigma) {
        this.sigma = sigma;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public float getM() {
        return M;
    }

    public void setM(float m) {
        M = m;
    }

    public int getN_days() {
        return n_days;
    }

    public void setN_days(int n_days) {
        this.n_days = n_days;
    }

    public float getDelta() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public int getN_mc() {
        return n_mc;
    }

    public void setN_mc(int n_mc) {
        this.n_mc = n_mc;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public float getLambda() {
        return lambda;
    }

    public void setLambda(float lambda) {
        this.lambda = lambda;
    }
}
