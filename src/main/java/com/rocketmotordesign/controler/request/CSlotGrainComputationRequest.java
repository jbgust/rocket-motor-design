package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;

import java.util.Objects;

public class CSlotGrainComputationRequest extends BasicComputationRequest{

    //Grain
    private double outerDiameter;
    private double coreDiameter;
    private double slotWidth;
    private double slotDepth;
    private double slotOffset;
    private GrainSurface endsSurface;

    public CSlotGrainComputationRequest() {
    }

    @Override
    public String getGrainType() {
        return "C_SLOT";
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

    public double getSlotWidth() {
        return slotWidth;
    }

    public void setSlotWidth(double slotWidth) {
        this.slotWidth = slotWidth;
    }

    public double getSlotDepth() {
        return slotDepth;
    }

    public void setSlotDepth(double slotDepth) {
        this.slotDepth = slotDepth;
    }

    public double getSlotOffset() {
        return slotOffset;
    }

    public void setSlotOffset(double slotOffset) {
        this.slotOffset = slotOffset;
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
        if (!(o instanceof CSlotGrainComputationRequest)) return false;
        CSlotGrainComputationRequest that = (CSlotGrainComputationRequest) o;
        return Double.compare(that.throatDiameter, throatDiameter) == 0 &&
                Double.compare(that.segmentLength, segmentLength) == 0 &&
                Double.compare(that.numberOfSegment, numberOfSegment) == 0 &&
                Double.compare(that.chamberInnerDiameter, chamberInnerDiameter) == 0 &&
                Double.compare(that.chamberLength, chamberLength) == 0 &&
                Objects.equals(propellantId, that.propellantId) &&
                Objects.equals(extraConfig, that.extraConfig) &&
                measureUnit == that.measureUnit &&

                Double.compare(that.outerDiameter, outerDiameter) == 0 &&
                Double.compare(that.getCoreDiameter(), coreDiameter) == 0 &&
                Double.compare(that.getSlotWidth(), slotWidth) == 0 &&
                Double.compare(that.getSlotDepth(), slotDepth) == 0 &&
                Double.compare(that.getSlotOffset(), slotOffset) == 0 &&
                endsSurface == that.endsSurface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outerDiameter, coreDiameter, slotWidth, slotDepth, slotOffset, endsSurface);
    }


    @Override
    public String toString() {
        return "CSlotGrainComputationRequest{" +
                "outerDiameter=" + outerDiameter +
                ", coreDiameter=" + coreDiameter +
                ", slotWidth=" + slotWidth +
                ", slotDepth=" + slotDepth +
                ", slotOffset=" + slotOffset +
                ", endsSurface=" + endsSurface +
                ", throatDiameter=" + throatDiameter +
                ", chamberInnerDiameter=" + chamberInnerDiameter +
                ", chamberLength=" + chamberLength +
                ", propellantType='" + propellantId + '\'' +
                ", segmentLength=" + segmentLength +
                ", numberOfSegment=" + numberOfSegment +
                ", extraConfig=" + extraConfig +
                ", measureUnit=" + measureUnit +
                '}';
    }

    @Override
    public boolean isRemovePostBurnResult() {
        return true;
    }
}
