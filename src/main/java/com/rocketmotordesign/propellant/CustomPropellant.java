package com.rocketmotordesign.propellant;

import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;

import static com.github.jbgust.jsrm.infra.JSRMConstant.UNIVERSAL_GAS_CONSTANT;

public class CustomPropellant implements SolidPropellant {

    /**
     * Refer to a
     */
    private final Double burnRateCoefficient;

    /**
     * refer to n
    */
    private final Double pressureExponent;

    private final Double density;
    private final Double k;
    private final Double k2ph;
    private final Double molarMass;
    private final Double chamberTemperature;

    public CustomPropellant(Double cstar, Double burnRateCoefficient, Double pressureExponent, Double density, Double k, Double k2ph, Double molarMass, Double chamberTemperature) {
        this.burnRateCoefficient = burnRateCoefficient;
        this.pressureExponent = pressureExponent;
        this.density = density;
        this.k = k;
        this.k2ph = k2ph != null ? k2ph : k;
        this.molarMass = molarMass;
        this.chamberTemperature = chamberTemperature != null ? chamberTemperature : resolveChamberTemperature(cstar) ;
    }


    @Override
    public String getDescription() {
        return "CUSTOM";
    }

    @Override
    public double getIdealMassDensity() {
        return density;
    }

    @Override
    public double getK2Ph() {
        return k2ph;
    }

    @Override
    public double getK() {
        return k;
    }

    @Override
    public double getEffectiveMolecularWeight() {
        return molarMass;
    }

    @Override
    public double getChamberTemperature() {
        return chamberTemperature;
    }

    @Override
    public double getBurnRateCoefficient(double v) throws ChamberPressureOutOfBoundException {
        return burnRateCoefficient;
    }

    @Override
    public double getPressureExponent(double v) throws ChamberPressureOutOfBoundException {
        return pressureExponent;
    }

    double getRat() {
        return UNIVERSAL_GAS_CONSTANT/molarMass;
    }

    private double resolveChamberTemperature(Double cstar) {
        return Math.pow(cstar, 2) * getX() / getRat();
    }

    private double getX() {
        return k*Math.pow(2/(k+1), (k+1)/(k-1));
    }
}
