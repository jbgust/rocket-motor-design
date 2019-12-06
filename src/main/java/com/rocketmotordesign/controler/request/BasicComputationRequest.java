package com.rocketmotordesign.controler.request;

import com.rocketmotordesign.service.MeasureUnit;

import java.util.Optional;

public abstract class BasicComputationRequest {

    protected double throatDiameter;

    //Motor chamber
    protected double chamberInnerDiameter;
    protected double chamberLength;

    protected String propellantType;

    protected double segmentLength;
    protected int numberOfSegment;

    protected ExtraConfiguration extraConfig;

    protected MeasureUnit measureUnit;

    //Extra
    protected  String computationHash;

    protected CustomPropellantRequest customPropellant;

    public BasicComputationRequest() {
    }

    public final String getPropellantType() {
        return propellantType;
    }

    public void setPropellantType(String propellantType) {
        this.propellantType = propellantType;
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

    public final CustomPropellantRequest getCustomPropellant() {
        return customPropellant;
    }

    public final void setCustomPropellant(CustomPropellantRequest customPropellant) {
        this.customPropellant = customPropellant;
    }

    public final void setChamberLength(double chamberLength) {
        this.chamberLength = chamberLength;
    }

    public final String getComputationHash() {
        return computationHash;
    }

    public final void setComputationHash(String computationHash) {
        this.computationHash = computationHash;
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
}
