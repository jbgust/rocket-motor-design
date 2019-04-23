package com.rocketmotordesign.propellant;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;


public class BurnRateCoefficientConverterTest {

    @Test
    public void shouldConvertBurnRateCoefficientToMetrique() {
        assertThat(BurnRateCoefficientConverter.toMetrique(0.0160236220472441, 0.6193))
                .isCloseTo(8.8754449, offset(0.0000001));
    }
}