package com.rocketmotordesign.propellant;

import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.data.Offset;
import org.junit.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.infra.JSRMConstant.UNIVERSAL_GAS_CONSTANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class CustomPropellantTest {

    @Test
    public void shouldCreateCustomPropellant() {
        CustomPropellant customPropellant = new CustomPropellant(
                1d,
                3d,
                4d,
                5d,
                6d,
                null,
                7d,
                null,
                null);

        assertThat(customPropellant.getDescription()).isEqualTo("CUSTOM");
        assertThat(customPropellant.getBurnRateCoefficient(92)).isEqualTo(3d);
        assertThat(customPropellant.getPressureExponent(45)).isEqualTo(4d);
        assertThat(customPropellant.getIdealMassDensity()).isEqualTo(5d);
        assertThat(customPropellant.getK()).isEqualTo(6d);
        assertThat(customPropellant.getEffectiveMolecularWeight()).isEqualTo(7d);

        assertThat(customPropellant.getK2Ph()).isEqualTo(6d);
    }

    @Test
    public void shouldCreateCustomPropellantWithAllValueWithoutCstar() {
        CustomPropellant customPropellant = new CustomPropellant(
                null,
                3d,
                4d,
                5d,
                6d,
                8d,
                7d,
                9d,
                null);

        assertThat(customPropellant.getDescription()).isEqualTo("CUSTOM");
        assertThat(customPropellant.getBurnRateCoefficient(92)).isEqualTo(3d);
        assertThat(customPropellant.getPressureExponent(45)).isEqualTo(4d);
        assertThat(customPropellant.getIdealMassDensity()).isEqualTo(5d);
        assertThat(customPropellant.getK()).isEqualTo(6d);
        assertThat(customPropellant.getEffectiveMolecularWeight()).isEqualTo(7d);

        assertThat(customPropellant.getK2Ph()).isEqualTo(8d);
        assertThat(customPropellant.getChamberTemperature()).isEqualTo(9d);
    }

    @Test
    public void shouldResolveChamberTemperature() {

        PropellantType propellant = KNDX;
        double k = propellant.getK();

        CustomPropellant customPropellant = new CustomPropellant(
                912.38154,
                2d,
                3d,
                propellant.getIdealMassDensity(),
                k,
                null, propellant.getEffectiveMolecularWeight(), null, null);

        ImmutableMap<String, Double> variables = ImmutableMap.of(
                "rat", customPropellant.getRat(), "to", propellant.getChamberTemperature(), "k", k
        );

        assertThat(customPropellant.getRat()).isCloseTo(UNIVERSAL_GAS_CONSTANT/KNDX.getEffectiveMolecularWeight(), Offset.offset(0.0001d));
        assertThat(customPropellant.getChamberTemperature()).isCloseTo(propellant.getChamberTemperature(), Offset.offset(0.0001d));

    }

    @Test
    public void assertBurnRateDatas() {

        fail("TODO");

    }

}