package com.rocketmotordesign.controler.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.rocketmotordesign.service.MeasureUnit;

import java.util.Optional;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "grainType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HollowComputationRequest.class, name = "HOLLOW"),
        @JsonSubTypes.Type(value = MoonBurnerGrainComputationRequest.class, name = "MOON_BURNER"),
        @JsonSubTypes.Type(value = RodTubeGrainComputationRequest.class, name = "ROD_TUBE"),
        @JsonSubTypes.Type(value = CSlotGrainComputationRequest.class, name = "C_SLOT"),
        @JsonSubTypes.Type(value = FinocylComputationRequest.class, name = "FINOCYL"),
        @JsonSubTypes.Type(value = StarGrainComputationRequest.class, name = "STAR"),
        @JsonSubTypes.Type(value = EndBurnerGrainComputationRequest.class, name = "END_BURNER")
})
public abstract class BasicComputationRequest {

    protected double throatDiameter;

    //Motor chamber
    protected double chamberInnerDiameter;
    protected double chamberLength;

    protected String propellantId;

    protected double segmentLength;
    protected int numberOfSegment;

    protected ExtraConfiguration extraConfig;

    protected MeasureUnit measureUnit;

    public BasicComputationRequest() {
    }

    public final String getPropellantId() {
        return propellantId;
    }

    public void setPropellantId(String propellantId) {
        this.propellantId = propellantId;
    }

    public final MeasureUnit getMeasureUnit() {
        return Optional.ofNullable(measureUnit).orElse(MeasureUnit.SI);
    }

    public final void setMeasureUnit(MeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }

    public final ExtraConfiguration getExtraConfig() {
        return extraConfig;
    }

    public final void setExtraConfig(ExtraConfiguration extraConfig) {
        this.extraConfig = extraConfig;
    }

    public final double getThroatDiameter() {
        return throatDiameter;
    }


    public final double getChamberInnerDiameter() {
        return chamberInnerDiameter;
    }

    public final double getChamberLength() {
        return chamberLength;
    }


    public final void setThroatDiameter(double throatDiameter) {
        this.throatDiameter = throatDiameter;
    }

    public final void setChamberInnerDiameter(double chamberInnerDiameter) {
        this.chamberInnerDiameter = chamberInnerDiameter;
    }

    public final void setChamberLength(double chamberLength) {
        this.chamberLength = chamberLength;
    }

    public final void setSegmentLength(double segmentLength) {
        this.segmentLength = segmentLength;
    }

    public final void setNumberOfSegment(int numberOfSegment) {
        this.numberOfSegment = numberOfSegment;
    }

    public final double getSegmentLength() {
        return segmentLength;
    }

    public final int getNumberOfSegment() {
        return numberOfSegment;
    }

    public abstract String getGrainType();

    public boolean isRemovePostBurnResult() {
        return false;
    }
}
