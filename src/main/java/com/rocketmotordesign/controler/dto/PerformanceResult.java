package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.Nozzle;

import java.util.Locale;

public class PerformanceResult {
    private final String motorDescription;
    private final String maxThrust;
    private final String totalImpulse;
    private final String specificImpulse;
    private final String maxPressure;
    private final String thrustTime;
    private final boolean optimalDesign;
    private final String nozzleExitDiameter;
    private final String exitSpeedInitial;
    private final String averagePressure;
    private final Double convergenceCrossSectionDiameter;
    private final Double divergenceCrossSectionDiameter;
    private final String optimalNozzleExpansionRatio;

    public PerformanceResult(JSRMResult jsrmResult, JSRMConfig jsrmConfig) {
        motorDescription = jsrmResult.getMotorClassification()+String.valueOf(jsrmResult.getAverageThrustInNewton());
        maxThrust = format(jsrmResult.getMaxThrustInNewton()) + " N";
        totalImpulse = format(jsrmResult.getTotalImpulseInNewtonSecond()) + " Ns";
        specificImpulse = format(jsrmResult.getSpecificImpulseInSecond()) +" s";
        maxPressure = format(toBar(jsrmResult.getMaxChamberPressureInMPa())) + " bar";
        averagePressure = format(toBar(jsrmResult.getAverageChamberPressure())) + " bar";
        thrustTime = format(jsrmResult.getThrustTimeInSecond()) + " s";

        Nozzle nozzle = jsrmResult.getNozzle();
        optimalDesign = jsrmConfig.isOptimalNozzleDesign();
        nozzleExitDiameter = format(nozzle.getNozzleExitDiameterInMillimeter()) +" mm";
        convergenceCrossSectionDiameter = nozzle.getChamberInsideDiameterInMillimeter() - nozzle.getNozzleThroatDiameterInMillimeter();
        divergenceCrossSectionDiameter = nozzle.getNozzleExitDiameterInMillimeter() - nozzle.getNozzleThroatDiameterInMillimeter();
        exitSpeedInitial = "Mach "+format(nozzle.getInitialNozzleExitSpeedInMach());
        optimalNozzleExpansionRatio = format(nozzle.getOptimalNozzleExpansionRatio());

    }

    private double toBar(double averageChamberPressure) {
        return averageChamberPressure * 10;
    }

    private String format(Double aDouble) {
        return String.format(Locale.ENGLISH, "%.2f", aDouble);
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

    public String getExitSpeedInitial() {
        return exitSpeedInitial;
    }

    public String getAveragePressure() {
        return averagePressure;
    }

    public Double getConvergenceCrossSectionDiameter() {
        return convergenceCrossSectionDiameter;
    }

    public Double getDivergenceCrossSectionDiameter() {
        return divergenceCrossSectionDiameter;
    }

    public String getOptimalNozzleExpansionRatio() {
        return optimalNozzleExpansionRatio;
    }
}
