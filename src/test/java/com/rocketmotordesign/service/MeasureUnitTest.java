package com.rocketmotordesign.service;

import org.junit.jupiter.api.Test;

import static com.rocketmotordesign.service.MeasureUnit.Constants.*;
import static com.rocketmotordesign.service.MeasureUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static tec.units.ri.unit.Units.KILOGRAM;

public class MeasureUnitTest {

    @Test
    void checkJSRMUnits() {
        assertThat(JSRM_UNITS.getLenghtUnit()).isEqualTo(MILLI_METER);
        assertThat(JSRM_UNITS.getPressureUnit()).isEqualTo(MPa);
        assertThat(JSRM_UNITS.getResultPressureUnit()).isEqualTo(MPa);
        assertThat(JSRM_UNITS.getMassUnit()).isEqualTo(KILOGRAM);
    }

    @Test
    void checkSIUnits() {
        assertThat(SI.getLenghtUnit()).isEqualTo(MILLI_METER);
        assertThat(SI.getPressureUnit()).isEqualTo(MPa);
        assertThat(SI.getResultPressureUnit()).isEqualTo(BAR);
        assertThat(SI.getMassUnit()).isEqualTo(KILOGRAM);
    }

    @Test
    void checkImperialUnits() {
        assertThat(IMPERIAL.getLenghtUnit()).isEqualTo(INCH);
        assertThat(IMPERIAL.getPressureUnit()).isEqualTo(PSI);
        assertThat(IMPERIAL.getResultPressureUnit()).isEqualTo(PSI);
        assertThat(IMPERIAL.getMassUnit()).isEqualTo(POUND);
    }

}
