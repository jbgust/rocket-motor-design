package com.rocketmotordesign.propellant;

import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.data.Offset;
import org.junit.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.infra.JSRMConstant.UNIVERSAL_GAS_CONSTANT;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomPropellantTest {

    @Test
    public void shouldCreateCustomPropellant() {
        CustomPropellant customPropellant = new CustomPropellant(
                1d,
                2d,
                3d,
                4d,
                5d,
                6d,
                7d);

        assertThat(customPropellant.getDescription()).isEqualTo("CUSTOM");
        assertThat(customPropellant.getIsp()).isEqualTo(2d);
        assertThat(customPropellant.getBurnRateCoefficient(92)).isEqualTo(3d);
        assertThat(customPropellant.getPressureExponent(45)).isEqualTo(4d);
        assertThat(customPropellant.getIdealMassDensity()).isEqualTo(5d);
        assertThat(customPropellant.getK()).isEqualTo(6d);
        assertThat(customPropellant.getEffectiveMolecularWeight()).isEqualTo(7d);

        assertThat(customPropellant.getK2Ph()).isEqualTo(6d);
    }

    @Test
    public void shouldResolveChamberTemperature() {

        PropellantType propellant = KNDX;
        double k = propellant.getK();

        CustomPropellant customPropellant = new CustomPropellant(
                912.38154,
                546d,
                2d,
                3d,
                propellant.getIdealMassDensity(),
                k,
                propellant.getEffectiveMolecularWeight());

        ImmutableMap<String, Double> variables = ImmutableMap.of(
                "rat", customPropellant.getRat(), "to", propellant.getChamberTemperature(), "k", k
        );

        assertThat(customPropellant.getRat()).isCloseTo(UNIVERSAL_GAS_CONSTANT/KNDX.getEffectiveMolecularWeight(), Offset.offset(0.0001d));
        assertThat(customPropellant.getChamberTemperature()).isCloseTo(propellant.getChamberTemperature(), Offset.offset(0.0001d));

    }

}