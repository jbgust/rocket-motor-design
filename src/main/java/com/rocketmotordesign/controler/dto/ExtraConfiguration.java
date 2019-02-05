package com.rocketmotordesign.controler.dto;


public class ExtraConfiguration {
    private double densityRatio;
    private double nozzleErosionInMillimeter;
    private double combustionEfficiencyRatio;
    private double ambiantPressureInMPa;
    private double erosiveBurningAreaRatioThreshold;
    private double erosiveBurningVelocityCoefficient;
    private double nozzleEfficiency;
    private boolean optimalNozzleDesign;
    private Double nozzleExpansionRatio;

    public ExtraConfiguration() {
    }

    public void setDensityRatio(double densityRatio) {
        this.densityRatio = densityRatio;
    }

    public void setNozzleErosionInMillimeter(double nozzleErosionInMillimeter) {
        this.nozzleErosionInMillimeter = nozzleErosionInMillimeter;
    }

    public void setCombustionEfficiencyRatio(double combustionEfficiencyRatio) {
        this.combustionEfficiencyRatio = combustionEfficiencyRatio;
    }

    public void setAmbiantPressureInMPa(double ambiantPressureInMPa) {
        this.ambiantPressureInMPa = ambiantPressureInMPa;
    }

    public void setErosiveBurningAreaRatioThreshold(double erosiveBurningAreaRatioThreshold) {
        this.erosiveBurningAreaRatioThreshold = erosiveBurningAreaRatioThreshold;
    }

    public void setErosiveBurningVelocityCoefficient(double erosiveBurningVelocityCoefficient) {
        this.erosiveBurningVelocityCoefficient = erosiveBurningVelocityCoefficient;
    }

    public void setNozzleEfficiency(double nozzleEfficiency) {
        this.nozzleEfficiency = nozzleEfficiency;
    }

    public void setOptimalNozzleDesign(boolean optimalNozzleDesign) {
        this.optimalNozzleDesign = optimalNozzleDesign;
    }

    public void setNozzleExpansionRatio(Double nozzleExpansionRatio) {
        this.nozzleExpansionRatio = nozzleExpansionRatio;
    }

    public double getDensityRatio() {
        return densityRatio;
    }

    public double getNozzleErosionInMillimeter() {
        return nozzleErosionInMillimeter;
    }

    public double getCombustionEfficiencyRatio() {
        return combustionEfficiencyRatio;
    }

    public double getAmbiantPressureInMPa() {
        return ambiantPressureInMPa;
    }

    public double getErosiveBurningAreaRatioThreshold() {
        return erosiveBurningAreaRatioThreshold;
    }

    public double getErosiveBurningVelocityCoefficient() {
        return erosiveBurningVelocityCoefficient;
    }

    public double getNozzleEfficiency() {
        return nozzleEfficiency;
    }

    public boolean isOptimalNozzleDesign() {
        return optimalNozzleDesign;
    }

    public Double getNozzleExpansionRatio() {
        return nozzleExpansionRatio;
    }
}
