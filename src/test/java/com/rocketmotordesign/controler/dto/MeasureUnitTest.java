package com.rocketmotordesign.controler.dto;

import org.assertj.core.data.Offset;
import org.junit.Test;

import static com.rocketmotordesign.controler.dto.MeasureUnit.IMPERIAL;
import static org.assertj.core.api.Assertions.assertThat;

public class MeasureUnitTest {

    @Test
    public void shouldConvertToPSI() {
        double convertPressure = IMPERIAL.convertPressure(5.515);

        assertThat(convertPressure).isCloseTo(799.8, Offset.offset(0.1));
    }
}