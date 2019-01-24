package com.rocketmotordesign.controler.dto;

import com.jsrm.application.result.JSRMResult;

public class PerformanceResult {
    private final String motorDescription;
    private final String maxThrust;
    private final String totalImpulse;
    private final String specificImpulse;
    private final String maxPressure;
    private final String thrustTime;

    public PerformanceResult(JSRMResult jsrmResult) {
        motorDescription = jsrmResult.getMotorClassification()+String.valueOf(jsrmResult.getAverageThrustInNewton());
        maxThrust = format(jsrmResult.getMaxThrustInNewton()) + " N";
        totalImpulse = format(jsrmResult.getTotalImpulseInNewtonSecond()) + " N.S";
        specificImpulse = format(jsrmResult.getSpecificImpulseInSecond()) +" s";
        maxPressure = format(jsrmResult.getMaxChamberPressureInMPa()*10) + " Bar";
        thrustTime = format(jsrmResult.getThrustTimeInSecond()) + " s";
    }

    private String format(Double aDouble) {
        return String.format("%.2f", aDouble);
    }

    public String getMotorDescription() {
        return motorDescription;
    }

    public String getTotalImpulse() {
        return totalImpulse;
    }

    public String getSpecificImpulse() {
        return specificImpulse;
    }

    public String getMaxPressure() {
        return maxPressure;
    }

    public String getThrustTime() {
        return thrustTime;
    }

    public String getMaxThrust() {
        return maxThrust;
    }
}
