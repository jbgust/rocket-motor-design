package com.rocketmotordesign.controler.request;


import java.util.Objects;

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
    private Integer numberOfCalculationLine;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraConfiguration that = (ExtraConfiguration) o;
        return Double.compare(that.densityRatio, densityRatio) == 0 &&
                Double.compare(that.nozzleErosionInMillimeter, nozzleErosionInMillimeter) == 0 &&
                Double.compare(that.combustionEfficiencyRatio, combustionEfficiencyRatio) == 0 &&
                Double.compare(that.ambiantPressureInMPa, ambiantPressureInMPa) == 0 &&
                Double.compare(that.erosiveBurningAreaRatioThreshold, erosiveBurningAreaRatioThreshold) == 0 &&
                Double.compare(that.erosiveBurningVelocityCoefficient, erosiveBurningVelocityCoefficient) == 0 &&
                Double.compare(that.nozzleEfficiency, nozzleEfficiency) == 0 &&
                Double.compare(that.numberOfCalculationLine, numberOfCalculationLine) == 0 &&
                optimalNozzleDesign == that.optimalNozzleDesign &&
                Objects.equals(nozzleExpansionRatio, that.nozzleExpansionRatio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(densityRatio, nozzleErosionInMillimeter, combustionEfficiencyRatio, ambiantPressureInMPa, erosiveBurningAreaRatioThreshold, erosiveBurningVelocityCoefficient, nozzleEfficiency, optimalNozzleDesign, nozzleExpansionRatio, numberOfCalculationLine);
    }

    @Override
    public String toString() {
        return "ExtraConfiguration{" +
                "densityRatio=" + densityRatio +
                ", nozzleErosion=" + nozzleErosionInMillimeter +
                ", combustionEfficiencyRatio=" + combustionEfficiencyRatio +
                ", ambiantPressureInMPa=" + ambiantPressureInMPa +
                ", erosiveBurningAreaRatioThreshold=" + erosiveBurningAreaRatioThreshold +
                ", erosiveBurningVelocityCoefficient=" + erosiveBurningVelocityCoefficient +
                ", nozzleEfficiency=" + nozzleEfficiency +
                ", optimalNozzleDesign=" + optimalNozzleDesign +
                ", numberOfCalculationLine=" + numberOfCalculationLine +
                ", nozzleExpansionRatio=" + nozzleExpansionRatio +
                '}';
    }

    public Integer getNumberOfCalculationLine() {
        return numberOfCalculationLine;
    }

    public void setNumberOfCalculationLine(Integer numberOfCalculationLine) {
        this.numberOfCalculationLine = numberOfCalculationLine;
    }
}
