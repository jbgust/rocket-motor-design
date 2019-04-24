package com.rocketmotordesign.controler.dto;

import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;

public enum MeasureUnit {
    JSRM_UNITS(Constants.MPa, Constants.MILLI_METER),
    SI(Constants.BAR, Constants.MILLI_METER),
    IMPERIAL(Constants.PSI, Constants.INCH);

    private Unit<Pressure> pressureUnit;
    private Unit<Length> lenghtUnit;

    MeasureUnit(Unit<Pressure> pressureUnit, Unit<Length> lenghtUnit) {
        this.pressureUnit = pressureUnit;
        this.lenghtUnit = lenghtUnit;
    }

    public Unit<Pressure> getPressureUnit() {
        return pressureUnit;
    }

    public Unit<Length> getLenghtUnit() {
        return lenghtUnit;
    }

    public static class Constants {
        // Unités JSRM
        private static final Unit<Pressure> MPa = MetricPrefix.MEGA(Units.PASCAL).asType(Pressure.class);

        // Unités FRONTEND
        private static final Unit<Pressure> BAR = MPa.divide(10);
        private static final Unit<Pressure> PSI = Units.PASCAL.multiply(6895);
        public static final Unit<Length> MILLI_METER = MetricPrefix.MILLI(Units.METRE).asType(Length.class);
        public static final Unit<Length> INCH = MILLI_METER.multiply(25.4);
    }
}
