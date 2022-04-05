package com.rocketmotordesign.controler.response;

import java.math.BigDecimal;

public class GraphResult {

    private final double x;
    private final double y;
    private final double kn;
    private final double p;
    private final double m;
    private final double grainMass;

    public GraphResult(double x, double y, double kn, double p, double m, double grainMass) {
        this.x = toFrontendPrecision(x);
        this.y = toFrontendPrecision(y);
        this.kn = toFrontendPrecision(kn);
        this.p = toFrontendPrecision(p);
        this.m = toFrontendPrecision(m);
        this.grainMass = toFrontendGramPrecision(grainMass);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getKn() {
        return kn;
    }

    public double getP() {
        return p;
    }

    public double getM() {
        return m;
    }

    public double getGrainMass() {
        return grainMass;
    }

    public static double toFrontendPrecision(double value) {
        return new BigDecimal(value).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double toFrontendGramPrecision(double value) {
        return new BigDecimal(value).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
