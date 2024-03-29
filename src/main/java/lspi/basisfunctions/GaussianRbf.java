package lspi.basisfunctions;

import lspi.types.BasisFunctions;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import util.Vec;

import java.io.Serializable;

public class GaussianRbf implements BasisFunctions, Serializable {

    private int numActions;
    private int numBasis;

    /**
     * Constructs a new 2D Gaussian RBF with the specified number of centers in the x and y
     * directions.
     *
     * <p>
     * This should probably be generalized to any dimension.
     *
     * @param numFx      Number of centers in first dimension
     * @param numFy      Number of centers in second dimension
     * @param numActions Number of actions for the domain
     */
    public GaussianRbf(int numFx, int numFy, int numActions) {
        this.numActions = numActions;
        this.numBasis = (numFx * numFy + 1) * numActions;
    }

    @Override
    public Vector evaluate(Vector state, int action) {
        Vector phi = new DenseVector(numBasis);

        if (action >= numActions || action < 0) {
            return phi; // return 0's if action number is invalid
        }

        if (Math.abs(state.get(0)) > Math.PI / 2.0) {
            return phi;
        }

        int base = (numBasis / numActions) * action;

        phi.set(base++, 1.0);

        double sigma2 = 1.0;
        for (double x = -Math.PI / 4.0; x <= Math.PI / 4.0; x += Math.PI / 4.0) {
            for (double y = -1.0; y <= 1.0; y += 1.0) {
                double dist = Math.pow(state.get(0) - x, 2) + Math.pow(state.get(1) - y, 2);
                phi.set(base++, Math.exp(-dist / (2 * sigma2)));
            }
        }

        return phi;
    }

    @Override
    public int size() {
        return numBasis;
    }

    public static void main(String[] args) {

        GaussianRbf rbf = new GaussianRbf(20, 1, 10);

        Vector state = new DenseVector(2);
        state.set(0,Math.PI/3.0);
        Vector v = rbf.evaluate(state, 3);

        System.out.println(v.toString());


    }

}

