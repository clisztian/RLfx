package util;

public class DoubleUtils {
    public static boolean equals(double a1, double a2){
        return Math.abs(a1-a2) < 1e-10;
    }

    public static boolean isZero(double a){
        return a < 1e-20;
    }
}