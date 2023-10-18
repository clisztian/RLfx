package simplemodel.meanvarianceopt;
import java.awt.Color;
public class BSplines {

    public double bspline(int degree, double x){
        double betta;
        double t;
        betta = 0;
        if(degree == 0){
            if ((x > -0.5) && (x < 0.5)){
                betta = 1.0;
            }
            else if( Math.abs(x) == 0.5){
                betta = 0.5;
            }
            else if( Math.abs(x) > 0.5){
                betta = 0.0;
            }
        }
        else if( degree == 1){
            if ((x<=-1) || (x>=1)){
                betta = 0.0;
            }
            else if ((x>-1) && (x<0)){
                betta = x+1;
            }
            else if ((x>0) && (x<1)){
                betta = -x+1;
            }
            else if( x==0){
                betta = 1.0;
            }
        }
        else if (degree == 2 ){
            t = 1.5;
            if ((x <= (0-t)) || (x >= (3-t))){
                betta = 0.0;
            }
            else if ((x >= (0-t)) && (x< (1-t))) {
                betta = ((x+t)*(x+t))/2.0;
            }
            else if ((x >= (1-t)) && (x< (2-t))) {
                betta = ((x+t)*(x+t)-3.0*(x-1+t)*(x-1+t))/2.0;
            }
            else if ((x >= (2-t)) && (x< (3-t))) {
                betta = ((x+t)*(x+t) - 3.0*(x-1+t)*(x-1+t) +
                        3.0*(x-2+t)*(x-2+t))/2.0;
            }
        }
        else if (degree == 3 ){
            if ((Math.abs(x)>=0) && (Math.abs(x)< 1)) {
                betta = 2.0/3.0 - Math.abs(x)*Math.abs(x) +
                        (Math.abs(x)*Math.abs(x)*Math.abs(x))/2.0;
            }
            else if ((Math.abs(x)>=1) && (Math.abs(x)< 2)) {
                betta = ((2-Math.abs(x))*(2-Math.abs(x))*
                        (2-Math.abs(x)))/6.0;
            }
            else if (Math.abs(x) >=2) {
                betta = 0.0;
            }
        }
        return betta;
    }
    public static void main(String[] args){

        double x[];
        double b0[];
        double b1[];
        double b2[];
        double b3[];

        int M = 200;
        x = new double[M];
        b0 = new double[M];
        b1 = new double[M];
        b2 = new double[M];
        b3 = new double[M];

        for( int k=0; k<x.length; k++){
            x[k] = (k-50.0)/30.0;
        }

        BSplines bSplines = new BSplines();

        for( int k=0; k<x.length; k++){
            b0[k]= bSplines.bspline(0,x[k]);
            b1[k]= bSplines.bspline(1,x[k]-1);
            b2[k]= bSplines.bspline(2,x[k]-2);
            b3[k]= bSplines.bspline(3,x[k]-3);
        }

        Figure figure = new Figure("bsplines","x","betta");
        figure.line(x,b0, Color.BLUE, 2.0f);
        figure.line(x,b1, Color.RED, 2.0f);
        figure.line(x,b2, Color.BLACK, 2.0f);
        figure.line(x,b3, Color.MAGENTA, 2.0f);
    }
}


