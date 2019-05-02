package com.rocketmotordesign.controler.dto;

import java.util.Set;

import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.infra.propellant.BurnRateData;

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
}
