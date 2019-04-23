package com.rocketmotordesign.controler.dto;

import tec.units.ri.quantity.Quantities;
import tec.units.ri.unit.MetricPrefix;
import tec.units.ri.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Pressure;

public enum MeasureUnit {
    SI(Constants.BAR),
    IMPERIAL(Constants.PSI);

    private Unit<Pressure> pressureUnit;

    MeasureUnit(Unit<Pressure> pressureUnit) {
        this.pressureUnit = pressureUnit;
    }

    public double convertPressure(double value) {
        return Quantities.getQuantity(value, Constants.defaultPressureUnit).to(pressureUnit).getValue().doubleValue();
    }

    private static class Constants {
        // Unités JSRM
        private static final Unit<Pressure> defaultPressureUnit = MetricPrefix.MEGA(Units.PASCAL).asType(Pressure.class);

        // Unités FRONTEND
        private static final Unit<Pressure> BAR = defaultPressureUnit.divide(10);
        private static final Unit<Pressure> PSI = Units.PASCAL.multiply(6895);
    }
}
