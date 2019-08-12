package com.rocketmotordesign.controler.request;

import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.rocketmotordesign.service.MeasureUnit;

import java.util.Objects;
import java.util.Optional;

public class ComputationRequest {
    private double throatDiameter;

    //Grain
    private double outerDiameter;
    private double coreDiameter;
    private double segmentLength;
    private double numberOfSegment;
    private GrainSurface outerSurface;
    private GrainSurface endsSurface;
    private GrainSurface coreSurface;
    private String propellantType;

    //Motor chamber
    private double chamberInnerDiameter;
    private double chamberLength;

    private ExtraConfiguration extraConfig;

    private MeasureUnit measureUnit;

    //Extra
    private  String computationHash;

    private CustomPropellantRequest customPropellant;

    public ComputationRequest() {
    }

    public MeasureUnit getMeasureUnit() {
        return Optional.ofNullable(measureUnit).orElse(MeasureUnit.SI);
    }

    public void setMeasureUnit(MeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }

    public ExtraConfiguration getExtraConfig() {
        return extraConfig;
    }

    public void setExtraConfig(ExtraConfiguration extraConfig) {
        this.extraConfig = extraConfig;
    }

    public double getThroatDiameter() {
        return throatDiameter;
    }

    public double getOuterDiameter() {
        return outerDiameter;
    }

    public double getCoreDiameter() {
        return coreDiameter;
    }

    public double getSegmentLength() {
        return segmentLength;
    }

    public double getNumberOfSegment() {
        return numberOfSegment;
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

    public String getPropellantType() {
        return propellantType;
    }

    public double getChamberInnerDiameter() {
        return chamberInnerDiameter;
    }

    public double getChamberLength() {
        return chamberLength;
    }

    public void setOuterDiameter(double outerDiameter) {
        this.outerDiameter = outerDiameter;
    }

    public void setCoreDiameter(double coreDiameter) {
        this.coreDiameter = coreDiameter;
    }

    public void setSegmentLength(double segmentLength) {
        this.segmentLength = segmentLength;
    }

    public void setNumberOfSegment(double numberOfSegment) {
        this.numberOfSegment = numberOfSegment;
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

    public void setPropellantType(String propellantType) {
        this.propellantType = propellantType;
    }

    public void setThroatDiameter(double throatDiameter) {
        this.throatDiameter = throatDiameter;
    }

    public void setChamberInnerDiameter(double chamberInnerDiameter) {
        this.chamberInnerDiameter = chamberInnerDiameter;
    }

    public CustomPropellantRequest getCustomPropellant() {
        return customPropellant;
    }

    public void setCustomPropellant(CustomPropellantRequest customPropellant) {
        this.customPropellant = customPropellant;
    }

    public void setChamberLength(double chamberLength) {
        this.chamberLength = chamberLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComputationRequest that = (ComputationRequest) o;
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
                Objects.equals(propellantType, that.propellantType) &&
                Objects.equals(extraConfig, that.extraConfig) &&
                measureUnit == that.measureUnit &&
                Objects.equals(customPropellant, that.customPropellant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(throatDiameter, outerDiameter, coreDiameter, segmentLength, numberOfSegment, outerSurface, endsSurface, coreSurface, propellantType, chamberInnerDiameter, chamberLength, extraConfig, measureUnit, customPropellant);
    }

    @Override
    public String toString() {
        return "ComputationRequest{" +
                "computationHash=" + computationHash +
                ", throatDiameter=" + throatDiameter +
                ", outerDiameter=" + outerDiameter +
                ", coreDiameter=" + coreDiameter +
                ", segmentLength=" + segmentLength +
                ", numberOfSegment=" + numberOfSegment +
                ", outerSurface=" + outerSurface +
                ", endsSurface=" + endsSurface +
                ", coreSurface=" + coreSurface +
                ", propellantType='" + propellantType + '\'' +
                ", chamberInnerDiameter=" + chamberInnerDiameter +
                ", chamberLength=" + chamberLength +
                ", extraConfig=" + extraConfig.toString() +
                ", measureUnit=" + measureUnit +
                ", customPropellant=" + (customPropellant != null ? customPropellant.toString() : "null") +
                '}';
    }

    public String getComputationHash() {
        return computationHash;
    }

    public void setComputationHash(String computationHash) {
        this.computationHash = computationHash;
    }
}
