package simplemodel.meanvarianceopt;

import no.uib.cipr.matrix.DenseMatrix;
import umontreal.ssj.functionfit.BSpline;

import java.util.Arrays;

public class AnyBSpline {

    //data_mat_t[t, :, :] = np.array([np.kron(basis_x(x[i]), basis_a(a[i])).reshape(num_basis, 1) for i in range(N_MC)])[:, :, 0]


    private final int p = 4; // order of spline (as-is; 3 = cubic, 4: B-spline)
    private int ncolloc = 16;
    private final double a_min = -1;
    private final double a_max = 1;


    public static void getBasisFunctions(double X_min, double X_max, int ncolloc) {





    }

    public double[] findKnots(double X_min, double X_max, int order, int n_knots) {

        double d = (X_max - X_min)/(double)(n_knots-1);

        double[] t = new double[n_knots];
        t[0] = X_min;
        for(int i = 1; i < n_knots; i++) {
            t[i] = t[i-1] + d;
            System.out.println(t[i]);
        }


        //DenseMatrix anymatrix = new DenseMatrix(nRows, nColumns);






        return t;
    }


    public static void main(String[] args) {

        System.out.println("What?");

        AnyBSpline bspline = new AnyBSpline();
        double[] xvals = bspline.findKnots(0, 1, 4, 16);
        double[] avals = new double[xvals.length];



//
//        BSpline anySplot = new BSpline(xvals, yvals, 4);
//        double v = 0.001;
//        for(int u = 0; u < 100; u++) {
//            System.out.println(anySplot.evaluate(v));
//            v += 0.0099;
//        }


    }


}
