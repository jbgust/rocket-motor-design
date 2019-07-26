package com.rocketmotordesign.service;

import org.junit.Test;

import static com.rocketmotordesign.service.MeasureUnit.Constants.*;
import static com.rocketmotordesign.service.MeasureUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static tec.units.ri.unit.Units.KILOGRAM;

public class MeasureUnitTest {

    @Test
    public void checkJSRMUnits() {
        assertThat(JSRM_UNITS.getLenghtUnit()).isEqualTo(MILLI_METER);
        assertThat(JSRM_UNITS.getResultPressureUnit()).isEqualTo(MPa);
        assertThat(JSRM_UNITS.getMassUnit()).isEqualTo(KILOGRAM);
    }

    @Test
    public void checkSIUnits() {
        assertThat(SI.getLenghtUnit()).isEqualTo(MILLI_METER);
        assertThat(SI.getResultPressureUnit()).isEqualTo(BAR);
        assertThat(SI.getMassUnit()).isEqualTo(KILOGRAM);
    }

    @Test
    public void checkImperialUnits() {
        assertThat(IMPERIAL.getLenghtUnit()).isEqualTo(INCH);
        assertThat(IMPERIAL.getResultPressureUnit()).isEqualTo(PSI);
        assertThat(IMPERIAL.getMassUnit()).isEqualTo(POUND);
    }

}