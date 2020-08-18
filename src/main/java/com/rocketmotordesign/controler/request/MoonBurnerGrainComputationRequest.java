package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

import java.util.Objects;

public class MoonBurnerGrainComputationRequest extends BasicComputationRequest{

    //Grain
    private double outerDiameter;
    private double coreDiameter;
    private double coreOffset;
    private GrainSurface endsSurface;

    public MoonBurnerGrainComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "MOON_BURNER";
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public void setOuterDiameter(double outerDiameter) {
        this.outerDiameter = outerDiameter;
    }

    public double getCoreDiameter() {
        return coreDiameter;
    }

    public void setCoreDiameter(double coreDiameter) {
        this.coreDiameter = coreDiameter;
    }

    public double getCoreOffset() {
        return coreOffset;
    }

    public void setCoreOffset(double coreOffset) {
        this.coreOffset = coreOffset;
    }

    public GrainSurface getEndsSurface() {
        return endsSurface;
    }

    public void setEndsSurface(GrainSurface endsSurface) {
        this.endsSurface = endsSurface;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoonBurnerGrainComputationRequest)) return false;
        MoonBurnerGrainComputationRequest that = (MoonBurnerGrainComputationRequest) o;
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
                Double.compare(that.getCoreDiameter(), coreDiameter) == 0 &&
                Double.compare(that.getCoreOffset(), coreOffset) == 0 &&
                endsSurface == that.endsSurface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outerDiameter, coreDiameter, coreOffset, endsSurface);
    }


    @Override
    public String toString() {
        return "MoonBurnerGrainComputationRequest{" +
                "outerDiameter=" + outerDiameter +
                ", coreDiameter=" + coreDiameter +
                ", coreOffset=" + coreOffset +
                ", endsSurface=" + endsSurface +
                ", throatDiameter=" + throatDiameter +
                ", chamberInnerDiameter=" + chamberInnerDiameter +
                ", chamberLength=" + chamberLength +
                ", propellantType='" + propellantType + '\'' +
                ", segmentLength=" + segmentLength +
                ", numberOfSegment=" + numberOfSegment +
                ", extraConfig=" + extraConfig +
                ", measureUnit=" + measureUnit +
                ", customPropellant=" + customPropellant +
                '}';
    }

    @Override
    public boolean isRemovePostBurnResult() {
        return true;
    }
}
