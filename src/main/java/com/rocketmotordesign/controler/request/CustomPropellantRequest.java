package com.rocketmotordesign.controler.request;

import com.rocketmotordesign.service.MeasureUnit;

import java.util.Set;

import static java.util.stream.Collectors.joining;

public class CustomPropellantRequest {

    /**
     * Refer to a
     */
    private Double burnRateCoefficient;

    /**
     * refer to n
    */
    private Double pressureExponent;

    private Double cstar;
    private Double density;
    private Double k;
    private Double k2ph;
    private Double chamberTemperature;
    private Double molarMass;
    private Set<BurnRatePressureData> burnRateDataSet;

    private MeasureUnit unit;

    public CustomPropellantRequest() {
    }


    public void setBurnRateCoefficient(Double burnRateCoefficient) {
        this.burnRateCoefficient = burnRateCoefficient;
    }

    public void setPressureExponent(Double pressureExponent) {
        this.pressureExponent = pressureExponent;
    }

    public void setCstar(Double cstar) {
        this.cstar = cstar;
    }

    public void setDensity(Double density) {
        this.density = density;
    }

    public void setK(Double k) {
        this.k = k;
    }

    public void setK2ph(Double k2ph) {
        this.k2ph = k2ph;
    }

    public void setChamberTemperature(Double chamberTemperature) {
        this.chamberTemperature = chamberTemperature;
    }

    public void setMolarMass(Double molarMass) {
        this.molarMass = molarMass;
    }

    public void setBurnRateDataSet(Set<BurnRatePressureData> burnRateDataSet) {
        this.burnRateDataSet = burnRateDataSet;
    }

    public Double getBurnRateCoefficient() {
        return burnRateCoefficient;
    }

    public Double getPressureExponent() {
        return pressureExponent;
    }

    public Double getCstar() {
        return cstar;
    }

    public Double getDensity() {
        return density;
    }

    public Double getK() {
        return k;
    }

    public Double getMolarMass() {
        return molarMass;
    }

    public Double getK2ph() {
        return k2ph;
    }

    public Double getChamberTemperature() {
        return chamberTemperature;
    }

    public Set<BurnRatePressureData> getBurnRateDataSet() {
        return burnRateDataSet;
    }

    public MeasureUnit getUnit() {
        return unit;
    }

    public void setUnit(MeasureUnit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "CustomPropellantRequest{" +
                "burnRateCoefficient=" + burnRateCoefficient +
                ", pressureExponent=" + pressureExponent +
                ", cstar=" + cstar +
                ", density=" + density +
                ", k=" + k +
                ", k2ph=" + k2ph +
                ", chamberTemperature=" + chamberTemperature +
                ", molarMass=" + molarMass +
                ", burnRateDataSet=" + (burnRateDataSet != null ? "["+burnRateDataSet.stream().map(BurnRatePressureData::toString).collect(joining(","))+"]" : "null") +
                '}';
    }
}
