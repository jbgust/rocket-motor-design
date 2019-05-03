package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.infra.propellant.BurnRateData;
import com.google.common.collect.ImmutableRangeMap;
import com.google.common.collect.RangeMap;
import com.rocketmotordesign.controler.request.BurnRatePressureData;

import static com.github.jbgust.jsrm.infra.JSRMConstant.UNIVERSAL_GAS_CONSTANT;
import static com.google.common.collect.Range.all;
import static com.google.common.collect.Range.closedOpen;

import java.util.Optional;
import java.util.Set;

public class CustomPropellant implements SolidPropellant {

    private final Double density;
    private final Double k;
    private final Double k2ph;
    private final Double molarMass;
    private final Double chamberTemperature;

    private final RangeMap<Double, BurnRateData> byPressureData;

    public CustomPropellant(
            Double cstar,
            Double burnRateCoefficient,
            Double pressureExponent,
            Double density,
            Double k,
            Double k2ph, Double molarMass, Double chamberTemperature, Set<BurnRatePressureData> burnRatePressureDataSet) {
        this.density = density;
        this.k = k;
        this.k2ph = k2ph != null ? k2ph : k;
        this.molarMass = molarMass;
        this.chamberTemperature = chamberTemperature != null ? chamberTemperature : resolveChamberTemperature(cstar) ;


        if(burnRatePressureDataSet !=null && !burnRatePressureDataSet.isEmpty()) {
            try {
                ImmutableRangeMap.Builder<Double, BurnRateData> burnRateDataBuilder = new ImmutableRangeMap.Builder<>();
                burnRatePressureDataSet.forEach(burnRatePressureData -> burnRateDataBuilder
                                .put(closedOpen(burnRatePressureData.getFromPressureIncluded(), burnRatePressureData.getToPressureExcluded()),
                                        new BurnRateData(burnRatePressureData.getBurnRateCoefficient(), burnRatePressureData.getPressureExponent())));
                this.byPressureData = burnRateDataBuilder.build();
            } catch (Exception e) {
               throw new BurnRateDataException(e.getMessage());
            }

        } else {
            this.byPressureData = new ImmutableRangeMap.Builder<Double, BurnRateData>()
                    .put(all(), new BurnRateData(burnRateCoefficient, pressureExponent)).build();
        }
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
    public double getBurnRateCoefficient(double chamberPressure) throws ChamberPressureOutOfBoundException {
        return getBurnRateData(chamberPressure, "burn rate coefficient").getBurnRateCoefficient();
    }

    @Override
    public double getPressureExponent(double chamberPressure) throws ChamberPressureOutOfBoundException {
        return  getBurnRateData(chamberPressure, "pressure exponent").getPressureExponent();
    }

    private BurnRateData getBurnRateData(double chamberPressure, String errorType) {
        return Optional.ofNullable(byPressureData.get(chamberPressure)).orElseThrow(() ->
                new CustomPropellantChamberPressureOutOfBoundException(
                        "Your custom propellant has no " + errorType + " for this pressure ("+chamberPressure+" MPa). The pressure should be in the range you defined " + byPressureData.span()
                ));
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
