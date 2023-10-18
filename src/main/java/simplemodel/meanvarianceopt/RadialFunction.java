package simplemodel.meanvarianceopt;

import no.uib.cipr.matrix.Vector;

import java.awt.*;

public class RadialFunction {

    public static double pos(double r) {
        double v = (1.0 - r);
        return v > 0 ? v : 0;
    }

    public static double radius(double x, double x0, double scale) {
        return pos(Math.abs(x - x0) * Math.abs(x - x0));
    }

    public static double phi(double x, double x0, double scale) {

        double r = Math.abs(x - x0) * Math.abs(x - x0)/scale;
        return r < 1 ? Math.pow(1.0 - r, 5)*(3.0 + 15.0*r + 24.0*r*r)/840.0 : 0;

    }

    public static double phi2(double x, double x0, double scale) {

        double r = Math.abs(x - x0) * Math.abs(x - x0)/scale;
        return r < 1 ? Math.pow(1.0 - r, 5)*(8.0*r*r + 5.0*r + 1.0): 0;

    }

    public static double phi3(double x, double x0, double scale) {

        double r = Math.abs(x - x0) * Math.abs(x - x0);
        return Math.exp(-r / (2 * scale));

    }


    public static double phi(Vector state, double x0, double y0) {

        double r = Math.pow(state.get(0) - x0, 2) +  Math.pow(state.get(1) - y0,2);
        return r < 1 ? Math.pow(1.0 - r, 5)*(3.0 + 15.0*r + 24.0*r*r)/840.0 : 0;

    }

    public static void main(String[] args) {

        int M = 20;
        double[] knots = new double[M];
        for( int k=0; k<knots.length; k++){
            knots[k] = 10.0*k/M;
        }

        double[] basis1 = new double[100];
        double[] basis2 = new double[100];
        double[] basis3 = new double[100];


        double[] axis = new double[100];
        for(int i = 0; i < 100; i++) {

            double p = 10.0*i/100.0;
            axis[i] = p;

            basis1[i] = phi3(p, knots[2], 1.0);
            basis2[i] = phi3(p, knots[8], 1.0);
            basis3[i] = phi3(p, knots[13], 1.0);
        }

        Figure figure = new Figure("radial","x","betta");
        figure.line(axis,basis1, Color.BLUE, 2.0f);
        figure.line(axis,basis2, Color.RED, 2.0f);
        figure.line(axis,basis3, Color.BLACK, 2.0f);

    }
}
