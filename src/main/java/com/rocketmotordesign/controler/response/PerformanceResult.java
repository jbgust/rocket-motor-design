package com.rocketmotordesign.controler.response;

import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.github.jbgust.jsrm.application.result.PortToThroatAreaWarning;
import com.rocketmotordesign.service.MeasureUnit;

import java.util.Locale;

import static com.rocketmotordesign.service.MeasureUnit.SI;

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
    private final boolean lowKNCorrection;
    private final String grainMass;
    private final boolean safeKN;
    private final int classPercentage;
    private final String portToThroatArea;
    private final PortToThroatAreaWarning portToThroatAreaWarning;

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
                             double optimalNozzleExpansionRatio,
                             long numberOfKNCorrection,
                             double grainMass,
                             MotorClassification motorClassification,
                             MeasureUnit measureUnit,
                             double portToThroatArea,
                             PortToThroatAreaWarning portToThroatAreaWarning) {

        // ça n'a pas de sens d'avoir la même précision en SI et en IMPERIAL. En IMPERIAL on perd trop en précision sur avec 2 décimales.
        String formatPourlongueur = SI == measureUnit ? "%.2f" : "%.4f";

        this.motorDescription = motorDescription;
        this.maxThrust = format(maxThrust);
        this.totalImpulse = format(totalImpulse);
        this.specificImpulse = format(specificImpulse);
        this.maxPressure = format(maxPressure);
        this.thrustTime = format(thrustTime);
        this.optimalDesign = optimalDesign;
        this.nozzleExitDiameter = format(nozzleExitDiameter, formatPourlongueur);
        this.exitSpeedInitial = format(exitSpeedInitial);
        this.averagePressure = format(averagePressure);
        this.convergenceCrossSectionDiameter = convergenceCrossSectionDiameter;
        this.divergenceCrossSectionDiameter = divergenceCrossSectionDiameter;
        this.optimalNozzleExpansionRatio = format(optimalNozzleExpansionRatio);
        this.lowKNCorrection = isLowKNCorrection(numberOfKNCorrection);
        this.grainMass = format(grainMass, "%.3f");
        this.safeKN = numberOfKNCorrection > 0 ? true : false;
        this.classPercentage = (int)Math.ceil(100 * (totalImpulse - motorClassification.getTotalImpulseRangeInNewtowSecond().lowerEndpoint()) / (motorClassification.getTotalImpulseRangeInNewtowSecond().upperEndpoint()-motorClassification.getTotalImpulseRangeInNewtowSecond().lowerEndpoint()));

        this.portToThroatArea = format(portToThroatArea);
        this.portToThroatAreaWarning = portToThroatAreaWarning;
    }

    public static String format(Double aDouble) {
        return format(aDouble, "%.2f");
    }

    public static String format(Double aDouble, String format) {
        return String.format(Locale.ENGLISH, format, aDouble);
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

    public boolean isLowKNCorrection() {
        return lowKNCorrection;
    }

    public String getGrainMass() {
        return grainMass;
    }

    public int getClassPercentage() {
        return classPercentage;
    }

    /**
     * Si il y a plus de 200 points qiu ont subit une correction
     * on flag a true lowKNCorrection qui permet d'afficher coté front
     * une message pour indiquer que le moteur a probleme de conception
     * @param lowKNCorrection
     * @return
     */
    private boolean isLowKNCorrection(long lowKNCorrection) {
        return lowKNCorrection > 100 ? true : false;
    }

    public boolean isSafeKN() {
        return safeKN;
    }

    public String getPortToThroatArea() {
        return portToThroatArea;
    }

    public PortToThroatAreaWarning getPortToThroatAreaWarning() {
        return portToThroatAreaWarning;
    }
}
