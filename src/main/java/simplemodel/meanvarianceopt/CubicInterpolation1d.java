package simplemodel.meanvarianceopt;

import java.awt.Color;
public class CubicInterpolation1d {

    private double[] mirrorW1d(double s[]){
        double [] s_mirror = new double[s.length+3];
        s_mirror[0] = s[1];
        for(int k=0; k<s.length; k++){
            s_mirror[k+1] = s[k];
        }
        s_mirror[s_mirror.length-2] = s[s.length-2];
        return s_mirror;
    }

    public double[] coeffs(double s[]){
        DirectBsplFilter1d directFilter =
                new DirectBsplFilter1d(s.length);
        double coeffs[] = directFilter.filter(s);
        double coeffs_mirror[] = mirrorW1d(coeffs);
        return coeffs_mirror;
    }
    public double interp(double coeffs_mirror[], double x1){
        BSplines bS = new BSplines();
        int k = (int)Math.floor(x1);
        double y1 = coeffs_mirror[k+0]*bS.bspline(3,x1-k+1)+
                coeffs_mirror[k+1]*bS.bspline(3,x1-k+0)+
                coeffs_mirror[k+2]*bS.bspline(3,x1-k-1)+
                coeffs_mirror[k+3]*bS.bspline(3,x1-k-2);
        return y1;
    }

    public double[] interpolate(double s[], int rate){
        double coeffs_mirror[] = coeffs(s);
        double s_interp[] = new double[rate*s.length-(rate-1)];
        for(int k = 0; k < s_interp.length; k++){
            s_interp[k] = interp(coeffs_mirror, k*(1.0/rate));
        }
        return s_interp;
    }

    public static void main(String[] args){

        double y[] = {  0.2486289591,
                0.4516387582,
                0.2277128249,
                0.8044495583,
                0.9861042500,
                0.0299919508,
                0.5356642008,
                0.0870772228,
                0.8020914197,
                0.9891449213,
                0.0669462606,
                0.9393983483,
                0.0181775335,
                0.6838386059,
                0.7837364674,
                0.5341375470,
                0.8853594661,
                0.8990048766,
                0.6259376407,
                0.1378689855
        };
        double x[] = new double[y.length];
        for(int k=0; k<y.length; k++){
            x[k] = k;
        }
        int rate = 6;
        CubicInterpolation1d cubicInterpolation1d =
                new CubicInterpolation1d();
        double y_interp[] = cubicInterpolation1d.interpolate(y,rate);
        double x_interp[] = new double[y_interp.length];
        for(int k=0; k<x_interp.length; k++){
            x_interp[k] = (double)k/(double)rate;
        }
        Figure figure = new Figure("Spline interpolation","", "");
        figure.stem(x,y, Color.BLUE, 1.0f);
        figure.line(x_interp, y_interp, Color.RED, 2.0f);
    }
}

class DirectBsplFilter1d {

    private int N;
    private double z1;
    private double cplus [];
    private double cminus [];
    private int k;
    private double sum0;

    public DirectBsplFilter1d(int N){
        this.N = N;
        z1 = -2.0 +  Math.sqrt(3.0);
        cplus = new double[N];
        cminus = new double[N];
    }

    public void reset(){
        for(k=0; k<N; k++){
            cplus[k] = 0.0;
            cminus[k] = 0.0;
        }
    }

    public double [] filter(double s[]){
        sum0 = 0.0;
        for(k = 0; k < N; k++){
            sum0 = sum0 + 6.0*s[k]*Math.pow(z1, k);
        }
        cplus[0] = sum0;
        for(k = 1; k < N; k++){
            cplus[k] = 6.0*s[k] + z1*cplus[k-1];
        }
        cminus[N-1] = (z1/(z1*z1-1.0))*
                (cplus[N-1] + z1*cplus[N-2]);
        for(k = N-2; k >= 0; k--){
            cminus[k] = z1*(cminus[k+1]-cplus[k]);
        }
        return cminus;
    }
}
