package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

import java.util.Objects;

public class FinocylComputationRequest  extends BasicComputationRequest{

    //Grain
    private double outerDiameter;
    private double innerDiameter;
    private double finWidth;
    private double finDiameter;
    private int finCount;
    private GrainSurface endSurface;


    public FinocylComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "FINOCYL";
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public void setOuterDiameter(double outerDiameter) {
        this.outerDiameter = outerDiameter;
    }

    public double getInnerDiameter() {
        return innerDiameter;
    }

    public void setInnerDiameter(double innerDiameter) {
        this.innerDiameter = innerDiameter;
    }

    public double getFinWidth() {
        return finWidth;
    }

    public void setFinWidth(double finWidth) {
        this.finWidth = finWidth;
    }

    public double getFinDiameter() {
        return finDiameter;
    }

    public void setFinDiameter(double finDiameter) {
        this.finDiameter = finDiameter;
    }

    public int getFinCount() {
        return finCount;
    }

    public void setFinCount(int finCount) {
        this.finCount = finCount;
    }

    public GrainSurface getEndSurface() {
        return endSurface;
    }

    public void setEndSurface(GrainSurface endSurface) {
        this.endSurface = endSurface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FinocylComputationRequest)) return false;
        FinocylComputationRequest that = (FinocylComputationRequest) o;
        return Double.compare(that.throatDiameter, throatDiameter) == 0 &&
                Double.compare(that.segmentLength, segmentLength) == 0 &&
                Double.compare(that.numberOfSegment, numberOfSegment) == 0 &&
                Double.compare(that.chamberInnerDiameter, chamberInnerDiameter) == 0 &&
                Double.compare(that.chamberLength, chamberLength) == 0 &&
                Objects.equals(propellantType, that.propellantType) &&
                Objects.equals(extraConfig, that.extraConfig) &&
                measureUnit == that.measureUnit &&
                Objects.equals(customPropellant, that.customPropellant) &&

                Double.compare(that.outerDiameter, outerDiameter) == 0 &&
                Double.compare(that.innerDiameter, innerDiameter) == 0 &&
                Double.compare(that.finWidth, finWidth) == 0 &&
                Double.compare(that.finDiameter, finDiameter) == 0 &&
                finCount == that.finCount &&
                endSurface == that.endSurface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outerDiameter, innerDiameter, finWidth, finDiameter, finCount, endSurface);
    }


    @Override
    public String toString() {
        return "FinocylComputationRequest{" +
                "outerDiameter=" + outerDiameter +
                ", innerDiameter=" + innerDiameter +
                ", finWidth=" + finWidth +
                ", finDiameter=" + finDiameter +
                ", finCount=" + finCount +
                ", endSurface=" + endSurface +
                ", throatDiameter=" + throatDiameter +
                ", chamberInnerDiameter=" + chamberInnerDiameter +
                ", chamberLength=" + chamberLength +
                ", propellantType='" + propellantType + '\'' +
                ", segmentLength=" + segmentLength +
                ", numberOfSegment=" + numberOfSegment +
                ", extraConfig=" + extraConfig +
                ", measureUnit=" + measureUnit +
                ", computationHash='" + computationHash + '\'' +
                ", customPropellant=" + customPropellant +
                '}';
    }
}
