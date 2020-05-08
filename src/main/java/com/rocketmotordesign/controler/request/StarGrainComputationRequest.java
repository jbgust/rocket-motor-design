package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

import java.util.Objects;

public class StarGrainComputationRequest extends BasicComputationRequest{

    //Grain
    private double outerDiameter;
    private double innerDiameter;
    private double pointDiameter;
    private int pointCount;
    private GrainSurface endsSurface;

    public StarGrainComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "STAR";
    }

    public GrainSurface getEndsSurface() {
        return endsSurface;
    }

    public void setEndsSurface(GrainSurface endsSurface) {
        this.endsSurface = endsSurface;
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

    public double getPointDiameter() {
        return pointDiameter;
    }

    public void setPointDiameter(double pointDiameter) {
        this.pointDiameter = pointDiameter;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StarGrainComputationRequest)) return false;
        StarGrainComputationRequest that = (StarGrainComputationRequest) o;
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
                Double.compare(that.pointDiameter, pointDiameter) == 0 &&
                Double.compare(that.pointCount, pointCount) == 0 &&
                endsSurface == that.endsSurface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outerDiameter, innerDiameter, pointDiameter, pointCount, endsSurface);
    }


    @Override
    public String toString() {
        return "StarComputationRequest{" +
                "outerDiameter=" + outerDiameter +
                ", innerDiameter=" + innerDiameter +
                ", pointDiameter=" + pointDiameter +
                ", pointCount=" + pointCount +
                ", endsSurface=" + endsSurface +
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

    @Override
    public boolean isRemovePostBurnResult() {
        return true;
    }
}
