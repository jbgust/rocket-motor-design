package com.rocketmotordesign.propellant;

import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;

import static com.github.jbgust.jsrm.infra.JSRMConstant.UNIVERSAL_GAS_CONSTANT;

public class CustomPropellant implements SolidPropellant {

    private final Double cstar;
    private final Double isp;
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
    private final Double molarMass;

    public CustomPropellant(Double cstar, Double isp, Double burnRateCoefficient, Double pressureExponent, Double density, Double k, Double molarMass) {
        this.cstar = cstar;
        this.isp = isp;
        this.burnRateCoefficient = burnRateCoefficient;
        this.pressureExponent = pressureExponent;
        this.density = density;
        this.k = k;
        this.molarMass = molarMass;
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
        return k;
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
        return resolveChamberTemperature();
    }

    @Override
    public double getBurnRateCoefficient(double v) throws ChamberPressureOutOfBoundException {
        return burnRateCoefficient;
    }

    @Override
    public double getPressureExponent(double v) throws ChamberPressureOutOfBoundException {
        return pressureExponent;
    }

    public Double getIsp() {
        return isp;
    }

    private double resolveChamberTemperature() {
        return Math.pow(cstar, 2) * getX() / getRat();
    }

    double getRat() {
        return UNIVERSAL_GAS_CONSTANT/molarMass;
    }

    private double getX() {
        return k*Math.pow(2/(k+1), (k+1)/(k-1));
    }
}
