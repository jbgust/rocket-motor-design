package com.rocketmotordesign.controler.request;


import java.util.Objects;

public class ExtraConfiguration {
    private double densityRatio;
    private double nozzleErosion;
    private double combustionEfficiencyRatio;
    private double ambiantPressure;
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

    public void setNozzleErosion(double nozzleErosionInMillimeter) {
        this.nozzleErosion = nozzleErosionInMillimeter;
    }

    public void setCombustionEfficiencyRatio(double combustionEfficiencyRatio) {
        this.combustionEfficiencyRatio = combustionEfficiencyRatio;
    }

    public void setAmbiantPressure(double ambiantPressureInMPa) {
        this.ambiantPressure = ambiantPressureInMPa;
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

    public double getNozzleErosion() {
        return nozzleErosion;
    }

    public double getCombustionEfficiencyRatio() {
        return combustionEfficiencyRatio;
    }

    public double getAmbiantPressure() {
        return ambiantPressure;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraConfiguration that = (ExtraConfiguration) o;
        return Double.compare(that.densityRatio, densityRatio) == 0 &&
                Double.compare(that.nozzleErosion, nozzleErosion) == 0 &&
                Double.compare(that.combustionEfficiencyRatio, combustionEfficiencyRatio) == 0 &&
                Double.compare(that.ambiantPressure, ambiantPressure) == 0 &&
                Double.compare(that.erosiveBurningAreaRatioThreshold, erosiveBurningAreaRatioThreshold) == 0 &&
                Double.compare(that.erosiveBurningVelocityCoefficient, erosiveBurningVelocityCoefficient) == 0 &&
                Double.compare(that.nozzleEfficiency, nozzleEfficiency) == 0 &&
                optimalNozzleDesign == that.optimalNozzleDesign &&
                Objects.equals(nozzleExpansionRatio, that.nozzleExpansionRatio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(densityRatio, nozzleErosion, combustionEfficiencyRatio, ambiantPressure, erosiveBurningAreaRatioThreshold, erosiveBurningVelocityCoefficient, nozzleEfficiency, optimalNozzleDesign, nozzleExpansionRatio);
    }

    @Override
    public String toString() {
        return "ExtraConfiguration{" +
                "densityRatio=" + densityRatio +
                ", nozzleErosion=" + nozzleErosion +
                ", combustionEfficiencyRatio=" + combustionEfficiencyRatio +
                ", ambiantPressure=" + ambiantPressure +
                ", erosiveBurningAreaRatioThreshold=" + erosiveBurningAreaRatioThreshold +
                ", erosiveBurningVelocityCoefficient=" + erosiveBurningVelocityCoefficient +
                ", nozzleEfficiency=" + nozzleEfficiency +
                ", optimalNozzleDesign=" + optimalNozzleDesign +
                ", nozzleExpansionRatio=" + nozzleExpansionRatio +
                '}';
    }
}
