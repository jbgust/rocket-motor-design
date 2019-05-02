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
    // TODO : mettre t0? et le tableau de pression pour a et n
    private Set<BurnRatePressureData> burnRateDataSet;

    public CustomPropellantRequest() {
    }

    public CustomPropellantRequest(Double cstar, Double burnRateCoefficient, Double pressureExponent, Double density, Double k, Double molarMass) {
        this.burnRateCoefficient = burnRateCoefficient;
        this.pressureExponent = pressureExponent;
        this.cstar = cstar;
        this.density = density;
        this.k = k;
        this.molarMass = molarMass;
    }
    public CustomPropellantRequest(PropellantType propellantType, Set<BurnRatePressureData> burnRateDataSet) {
        this.burnRateCoefficient = propellantType.getBurnRateCoefficient(1);
        this.pressureExponent = propellantType.getPressureExponent(1);
        this.cstar = null;
        this.density = propellantType.getIdealMassDensity();
        // TODO : IMPERIAL => this.density = propellantType.getIdealMassDensity()/453.6*Math.pow(2.54, 3);
        this.k = propellantType.getK();
        this.k2ph = propellantType.getK2Ph();
        this.molarMass = propellantType.getEffectiveMolecularWeight();
        this.chamberTemperature = propellantType.getChamberTemperature();

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
