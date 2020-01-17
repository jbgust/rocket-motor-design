package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

import java.util.Objects;

public class RodTubeGrainComputationRequest extends BasicComputationRequest{

    //Grain
    private double rodDiameter;
    private double tubeOuterDiameter;
    private double tubeInnerDiameter;
    private GrainSurface endSurface;

    public RodTubeGrainComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "ROD_TUBE";
    }

    public double getRodDiameter() {
        return rodDiameter;
    }

    public void setRodDiameter(double rodDiameter) {
        this.rodDiameter = rodDiameter;
    }

    public double getTubeOuterDiameter() {
        return tubeOuterDiameter;
    }

    public void setTubeOuterDiameter(double tubeOuterDiameter) {
        this.tubeOuterDiameter = tubeOuterDiameter;
    }

    public double getTubeInnerDiameter() {
        return tubeInnerDiameter;
    }

    public void setTubeInnerDiameter(double tubeInnerDiameter) {
        this.tubeInnerDiameter = tubeInnerDiameter;
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
        if (!(o instanceof RodTubeGrainComputationRequest)) return false;
        RodTubeGrainComputationRequest that = (RodTubeGrainComputationRequest) o;
        return Double.compare(that.throatDiameter, throatDiameter) == 0 &&
                Double.compare(that.segmentLength, segmentLength) == 0 &&
                Double.compare(that.numberOfSegment, numberOfSegment) == 0 &&
                Double.compare(that.chamberInnerDiameter, chamberInnerDiameter) == 0 &&
                Double.compare(that.chamberLength, chamberLength) == 0 &&
                Objects.equals(propellantType, that.propellantType) &&
                Objects.equals(extraConfig, that.extraConfig) &&
                measureUnit == that.measureUnit &&
                Objects.equals(customPropellant, that.customPropellant) &&

                Double.compare(that.rodDiameter, rodDiameter) == 0 &&
                Double.compare(that.getTubeOuterDiameter(), tubeOuterDiameter) == 0 &&
                Double.compare(that.getTubeInnerDiameter(), tubeInnerDiameter) == 0 &&
                endSurface == that.endSurface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rodDiameter, tubeOuterDiameter, tubeInnerDiameter, endSurface);
    }


    @Override
    public String toString() {
        return "RodTubeGrainComputationRequest{" +
                "rodDiameter=" + rodDiameter +
                ", tubeOuterDiameter=" + tubeOuterDiameter +
                ", tubeInnerDiameter=" + tubeInnerDiameter +
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
