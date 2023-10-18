package simplemodel.meanvarianceopt;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import org.apache.commons.lang3.tuple.Pair;

public class PortfolioQLSLearning {
    //data_mat_t[t, :, :] = np.array([np.kron(basis_x(x[i]), basis_a(a[i])) .reshape(num_basis, 1) for i in range(N_MC)])[:, :, 0]

    private SimulateAsset assets;

    public PortfolioQLSLearning() {
        assets = new SimulateAsset();
    }

    public static Vector negativeReward(float mu, float var, Vector a, float rf, float lmbda) {
        Vector C = new DenseVector(a.size());

        for(int i = 0; i < a.size(); i++) {
            C.set(i, -(1.0 - a.get(i)) * rf - a.get(i)*mu + lmbda * a.get(i)*a.get(i)*var);
        }

        return C;
    }

    /**
     * create grid for the state and action space
     * @param X_min
     * @param X_max
     * @param Xres
     * @param a_min
     * @param a_max
     * @param A_res
     */

    public Pair<double[], double[]> createGrids(double X_min, double X_max, int Xres, double a_min, double a_max, int A_res) {

        double[] grid_x = new double[Xres];
        double[] grid_a = new double[A_res];

        double delta_x = ((X_max + 10.0) - (X_min-10.0))/((double)Xres -  1.0);
        double delta_a = ((a_max+0.1) - (a_min-0.1))/((double)A_res - 1.0);

        grid_x[0] = X_min-10.0;
        for(int i = 1; i < Xres; i++) {
            grid_x[i] = grid_x[i-1] + delta_x;
        }
        grid_a[0] = a_min-0.1;
        for(int i = 1; i < A_res; i++) {
            grid_a[i] = grid_a[i-1] + delta_a;
        }


        return Pair.of(grid_x, grid_a);
    }


    public DenseMatrix getPhi(double X_min, double X_max, int Xres, double a_min, double a_max, int A_res) {


        Pair<double[], double[]> grids = createGrids(X_min, X_max, Xres, a_min, a_max, A_res);

        Pair<double[], double[]> nodes = createGrids(X_min, X_max, 10, a_min, a_max, 3);

        double[] xgrid = grids.getKey();
        double[] agrid = grids.getValue();

        double[] xnodes = nodes.getKey();
        double[] anodes = nodes.getValue();

        DenseMatrix phi = new DenseMatrix(xgrid.length * agrid.length, xnodes.length * anodes.length);

        for(int m = 0; m < xnodes.length; m++) {
            for(int n = 0; n < anodes.length; n++) {
                for(int i = 0; i < xgrid.length; i++) {
                    for(int j = 0; j < agrid.length; j++) {

                        double b = RadialFunction.phi3(xgrid[i], xnodes[m], 20.0) * RadialFunction.phi3(agrid[j], anodes[n], 1.0);
                        System.out.println(RadialFunction.phi3(xgrid[i], xnodes[m], 20.0) + " " + RadialFunction.phi3(agrid[j], anodes[n], 1.0));
                        phi.add(i * agrid.length + j, m * anodes.length + n, b);
                    }
                }
            }
        }

        return phi;
    }


    public DenseMatrix getS(DenseMatrix phi) {

        DenseMatrix S = new DenseMatrix(phi.numColumns(), phi.numColumns());
        phi.transAmult(phi,S);

        return S;
    }


    public DenseMatrix getM(DenseMatrix phi, Vector R, double gamma, Vector Qt1) {

        Vector update = new DenseVector(R.size());

        double min_Q = Double.MAX_VALUE;
        for(int i = 0; i < Qt1.size(); i++) {
            min_Q = Math.min(min_Q, Qt1.get(i));
        }

        for(int i = 0; i < R.size(); i++) {
            update.set(i, R.get(i) + gamma * min_Q );
        }

        return null;

    }



    public static void main(String[] args) {

        PortfolioQLSLearning qt = new PortfolioQLSLearning();

        DenseMatrix phi = qt.getPhi(-10.0, 10, 100, -1.0, 1.0, 8);

        double sum = 0;
        for(int i = 0; i < phi.numColumns(); i++) {
            sum += phi.get(0, i);
        }
        System.out.println(sum);

        //System.out.println(phi.numRows() + " " + phi.numColumns());

        //System.out.println(phi.toString());

        DenseMatrix S = qt.getS(phi);

        System.out.println(S.toString());
    }



}
