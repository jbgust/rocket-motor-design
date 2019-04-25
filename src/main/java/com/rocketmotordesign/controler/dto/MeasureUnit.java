package com.rocketmotordesign.controler.dto;

import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Pressure;

public enum MeasureUnit {
    JSRM_UNITS(Constants.MPa, Constants.MILLI_METER, Units.KILOGRAM),
    SI(Constants.BAR, Constants.MILLI_METER, Units.KILOGRAM),
    IMPERIAL(Constants.PSI, Constants.INCH, Constants.POUND);

    private final Unit<Pressure> pressureUnit;
    private final Unit<Length> lenghtUnit;
    private final Unit<Mass> massUnit;

    MeasureUnit(Unit<Pressure> pressureUnit, Unit<Length> lenghtUnit, Unit<Mass> massUnit) {
        this.pressureUnit = pressureUnit;
        this.lenghtUnit = lenghtUnit;
        this.massUnit = massUnit;
    }

    public Unit<Pressure> getPressureUnit() {
        return pressureUnit;
    }

    public Unit<Length> getLenghtUnit() {
        return lenghtUnit;
    }

    public Unit<Mass> getMassUnit() {
        return massUnit;
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
    }
}
