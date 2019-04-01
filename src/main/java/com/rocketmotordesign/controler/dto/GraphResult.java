package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.result.ThrustResult;

import java.math.BigDecimal;

public class GraphResult {

    private final double x;
    private final double y;

    public GraphResult(ThrustResult thrustResult) {
        this.x = toFrontendPrecision(thrustResult.getTimeSinceBurnStartInSecond());
        this.y = toFrontendPrecision(thrustResult.getThrustInNewton());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    private double toFrontendPrecision(double timeSinceBurnStartInSecond) {
        return new BigDecimal(timeSinceBurnStartInSecond).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
