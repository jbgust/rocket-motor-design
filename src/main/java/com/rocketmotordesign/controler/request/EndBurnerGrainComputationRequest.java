package com.rocketmotordesign.controler.request;

import java.util.Objects;

public class EndBurnerGrainComputationRequest extends BasicComputationRequest{

    //Grain
    private double outerDiameter;
    private double holeDiameter;
    private double holeDepth;

    public EndBurnerGrainComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "END_BURNER";
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public void setOuterDiameter(double outerDiameter) {
        this.outerDiameter = outerDiameter;
    }

    public double getHoleDiameter() {
        return holeDiameter;
    }

    public void setHoleDiameter(double holeDiameter) {
        this.holeDiameter = holeDiameter;
    }

    public double getHoleDepth() {
        return holeDepth;
    }

    public void setHoleDepth(double holeDepth) {
        this.holeDepth = holeDepth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EndBurnerGrainComputationRequest)) return false;
        EndBurnerGrainComputationRequest that = (EndBurnerGrainComputationRequest) o;
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
                Double.compare(that.holeDiameter, holeDiameter) == 0 &&
                Double.compare(that.holeDepth, holeDepth) == 0 &&
                Double.compare(that.holeDiameter, holeDiameter) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outerDiameter, holeDiameter, holeDepth, holeDiameter);
    }


    @Override
    public String toString() {
        return "StarComputationRequest{" +
                "outerDiameter=" + outerDiameter +
                ", holeDiameter=" + holeDiameter +
                ", holeDepth=" + holeDepth +
                ", holeDiameter=" + holeDiameter +
                ", throatDiameter=" + throatDiameter +
                ", chamberInnerDiameter=" + chamberInnerDiameter +
                ", chamberLength=" + chamberLength +
                ", propellantType='" + propellantType + '\'' +
                ", segmentLength=" + segmentLength +
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
