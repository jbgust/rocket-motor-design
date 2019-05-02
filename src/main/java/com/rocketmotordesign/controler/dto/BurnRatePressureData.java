package com.rocketmotordesign.controler.dto;

public class BurnRatePressureData {

    private double burnRateCoefficient;
    private double pressureExponent;
    private double fromPressureIncluded;
    private double toPressureExcluded;

    public BurnRatePressureData() {
    }

    public BurnRatePressureData(double burnRateCoefficient, double pressureExponent, double fromPressureIncluded, double toPressureExcluded) {
        this.burnRateCoefficient = burnRateCoefficient;
        this.pressureExponent = pressureExponent;
        this.fromPressureIncluded = fromPressureIncluded;
        this.toPressureExcluded = toPressureExcluded;
    }

    public double getBurnRateCoefficient() {
        return burnRateCoefficient;
    }

    public void setBurnRateCoefficient(double burnRateCoefficient) {
        this.burnRateCoefficient = burnRateCoefficient;
    }

    public double getPressureExponent() {
        return pressureExponent;
    }

    public void setPressureExponent(double pressureExponent) {
        this.pressureExponent = pressureExponent;
    }

    public double getFromPressureIncluded() {
        return fromPressureIncluded;
    }

    public void setFromPressureIncluded(double fromPressureIncluded) {
        this.fromPressureIncluded = fromPressureIncluded;
    }

    public double getToPressureExcluded() {
        return toPressureExcluded;
    }

    public void setToPressureExcluded(double toPressureExcluded) {
        this.toPressureExcluded = toPressureExcluded;
    }

    @Override
    public String toString() {
        return "BurnRatePressureData{" +
                "a=" + burnRateCoefficient +
                ", n=" + pressureExponent +
                ", from=" + fromPressureIncluded +
                ", to=" + toPressureExcluded +
                '}';
    }
}
