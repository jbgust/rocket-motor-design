package com.rocketmotordesign.service;

import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Pressure;

public enum MeasureUnit {
    JSRM_UNITS(Constants.MPa, Constants.MPa, Constants.MILLI_METER, Units.KILOGRAM, Units.KILOGRAM),

    SI(Constants.MPa, Constants.BAR, Constants.MILLI_METER, Units.KILOGRAM, Constants.GRAMME),
    IMPERIAL(Constants.PSI, Constants.PSI, Constants.INCH, Constants.POUND, Constants.POUND);

    private Unit<Pressure> pressureUnit;
    private final Unit<Pressure> resultPressureUnit;
    private final Unit<Length> lenghtUnit;
    private final Unit<Mass> massUnit;
    private final Unit<Mass> grainMassUnit;

    MeasureUnit(Unit<Pressure> pressureUnit, Unit<Pressure> resultPressureUnit, Unit<Length> lenghtUnit, Unit<Mass> massUnit, Unit<Mass> grainMassUnit) {
        this.pressureUnit = pressureUnit;
        this.resultPressureUnit = resultPressureUnit;
        this.lenghtUnit = lenghtUnit;
        this.massUnit = massUnit;
        this.grainMassUnit = grainMassUnit;
    }

    public Unit<Pressure> getResultPressureUnit() {
        return resultPressureUnit;
    }

    public Unit<Length> getLenghtUnit() {
        return lenghtUnit;
    }

    public Unit<Mass> getMassUnit() {
        return massUnit;
    }

    public Unit<Mass> getGrainMassUnit() {
        return grainMassUnit;
    }

    public Unit<Pressure> getPressureUnit() {
        return pressureUnit;
    }

    public static class Constants {
        // Unités JSRM
        public static final Unit<Pressure> MPa = MetricPrefix.MEGA(Units.PASCAL).asType(Pressure.class);

        // Unités FRONTEND
        public static final Unit<Pressure> BAR = MPa.divide(10);
        public static final Unit<Pressure> PSI = Units.PASCAL.multiply(6895);
        public static final Unit<Length> MILLI_METER = MetricPrefix.MILLI(Units.METRE);
        public static final Unit<Length> INCH = MILLI_METER.multiply(25.4);
        public static final Unit<Mass> POUND = Units.KILOGRAM.multiply(0.45359237);
        public static final Unit<Mass> GRAMME = Units.KILOGRAM.divide(1000);
    }
}
