package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

import java.util.Objects;

public class HollowComputationRequest extends BasicComputationRequest{
    //Grain
    private double outerDiameter;
    private double coreDiameter;
    private GrainSurface outerSurface;
    private GrainSurface endsSurface;
    private GrainSurface coreSurface;

    public HollowComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "HOLLOW";
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public double getCoreDiameter() {
        return coreDiameter;
    }

    public GrainSurface getOuterSurface() {
        return outerSurface;
    }

    public GrainSurface getEndsSurface() {
        return endsSurface;
    }

    public GrainSurface getCoreSurface() {
        return coreSurface;
    }

    public void setOuterDiameter(double outerDiameter) {
        this.outerDiameter = outerDiameter;
    }

    public void setCoreDiameter(double coreDiameter) {
        this.coreDiameter = coreDiameter;
    }

    public void setOuterSurface(GrainSurface outerSurface) {
        this.outerSurface = outerSurface;
    }

    public void setEndsSurface(GrainSurface endsSurface) {
        this.endsSurface = endsSurface;
    }

    public void setCoreSurface(GrainSurface coreSurface) {
        this.coreSurface = coreSurface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HollowComputationRequest that = (HollowComputationRequest) o;
        return Double.compare(that.throatDiameter, throatDiameter) == 0 &&
                Double.compare(that.outerDiameter, outerDiameter) == 0 &&
                Double.compare(that.coreDiameter, coreDiameter) == 0 &&
                Double.compare(that.segmentLength, segmentLength) == 0 &&
                Double.compare(that.numberOfSegment, numberOfSegment) == 0 &&
                Double.compare(that.chamberInnerDiameter, chamberInnerDiameter) == 0 &&
                Double.compare(that.chamberLength, chamberLength) == 0 &&
                outerSurface == that.outerSurface &&
                endsSurface == that.endsSurface &&
                coreSurface == that.coreSurface &&
                Objects.equals(propellantId, that.propellantId) &&
                Objects.equals(extraConfig, that.extraConfig) &&
                measureUnit == that.measureUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(throatDiameter, outerDiameter, coreDiameter, segmentLength, numberOfSegment, outerSurface, endsSurface, coreSurface, propellantId, chamberInnerDiameter, chamberLength, extraConfig, measureUnit);
    }

    @Override
    public String toString() {
        return "ComputationRequest{" +
                ", throatDiameter=" + throatDiameter +
                ", outerDiameter=" + outerDiameter +
                ", coreDiameter=" + coreDiameter +
                ", segmentLength=" + segmentLength +
                ", numberOfSegment=" + numberOfSegment +
                ", outerSurface=" + outerSurface +
                ", endsSurface=" + endsSurface +
                ", coreSurface=" + coreSurface +
                ", propellantType='" + propellantId + '\'' +
                ", chamberInnerDiameter=" + chamberInnerDiameter +
                ", chamberLength=" + chamberLength +
                ", extraConfig=" + extraConfig.toString() +
                ", measureUnit=" + measureUnit +
                '}';
    }
}
