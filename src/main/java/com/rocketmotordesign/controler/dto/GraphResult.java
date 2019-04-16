package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.result.MotorParameters;

import java.math.BigDecimal;

import static com.rocketmotordesign.controler.dto.ComputationResponse.toBar;

public class GraphResult {

    private final double x;
    private final double y;
    private final double kn;
    private final double p;
    private final double m;

    public GraphResult(MotorParameters motorParameters) {
        this.x = toFrontendPrecision(motorParameters.getTimeSinceBurnStartInSecond());
        this.y = toFrontendPrecision(motorParameters.getThrustInNewton());
        this.kn = toFrontendPrecision(motorParameters.getKn());
        this.p = toFrontendPrecision(toBar(motorParameters.getChamberPressureInMPa()));
        this.m = toFrontendPrecision(motorParameters.getMassFlowRateInKgPerSec());
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

    private double toFrontendPrecision(double timeSinceBurnStartInSecond) {
        return new BigDecimal(timeSinceBurnStartInSecond).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
