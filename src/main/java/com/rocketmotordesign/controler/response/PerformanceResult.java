package com.rocketmotordesign.controler.response;

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

    public PerformanceResult(String motorDescription,
                             double maxThrust,
                             double totalImpulse,
                             double specificImpulse,
                             double maxPressure,
                             double thrustTime,
                             boolean optimalDesign,
                             double nozzleExitDiameter,
                             double exitSpeedInitial,
                             double averagePressure,
                             Double convergenceCrossSectionDiameter,
                             Double divergenceCrossSectionDiameter,
                             double optimalNozzleExpansionRatio) {
        this.motorDescription = motorDescription;
        this.maxThrust = format(maxThrust);
        this.totalImpulse = format(totalImpulse);
        this.specificImpulse = format(specificImpulse);
        this.maxPressure = format(maxPressure);
        this.thrustTime = format(thrustTime);
        this.optimalDesign = optimalDesign;
        this.nozzleExitDiameter = format(nozzleExitDiameter);
        this.exitSpeedInitial = format(exitSpeedInitial);
        this.averagePressure = format(averagePressure);
        this.convergenceCrossSectionDiameter = convergenceCrossSectionDiameter;
        this.divergenceCrossSectionDiameter = divergenceCrossSectionDiameter;
        this.optimalNozzleExpansionRatio = format(optimalNozzleExpansionRatio);
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
