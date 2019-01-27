package com.rocketmotordesign.controler.dto;

import com.jsrm.application.JSRMConfig;
import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.result.Nozzle;

public class PerformanceResult {
    private final String motorDescription;
    private final String maxThrust;
    private final String totalImpulse;
    private final String specificImpulse;
    private final String maxPressure;
    private final String thrustTime;
    private final boolean optimalDesign;
    private final String nozzleExitDiameter;
    private final String divergenceLenght;
    private final String convergenceLenght;
    private final String exitSpeedInitial;

    public PerformanceResult(JSRMResult jsrmResult, JSRMConfig jsrmConfig) {
        motorDescription = jsrmResult.getMotorClassification()+String.valueOf(jsrmResult.getAverageThrustInNewton());
        maxThrust = format(jsrmResult.getMaxThrustInNewton()) + " N";
        totalImpulse = format(jsrmResult.getTotalImpulseInNewtonSecond()) + " N.S";
        specificImpulse = format(jsrmResult.getSpecificImpulseInSecond()) +" s";
        maxPressure = format(jsrmResult.getMaxChamberPressureInMPa()*10) + " Bar";
        thrustTime = format(jsrmResult.getThrustTimeInSecond()) + " s";

        Nozzle nozzle = jsrmResult.getNozzle();
        optimalDesign = jsrmConfig.isOptimalNozzleDesign();
        nozzleExitDiameter = format(nozzle.getNozzleExitDiameterInMillimeter()) +" mm";
        divergenceLenght = format(nozzle.getDivergenceLenghtInMillimeter(12))+" mm";
        convergenceLenght = format(nozzle.getConvergenceLenghtInMillimeter(30))+" mm";
        exitSpeedInitial = format(nozzle.getInitialNozzleExitSpeedInMach()) + " mach";

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

    public boolean isOptimalDesign() {
        return optimalDesign;
    }

    public String getNozzleExitDiameter() {
        return nozzleExitDiameter;
    }

    public String getDivergenceLenght() {
        return divergenceLenght;
    }

    public String getConvergenceLenght() {
        return convergenceLenght;
    }

    public String getExitSpeedInitial() {
        return exitSpeedInitial;
    }
}
