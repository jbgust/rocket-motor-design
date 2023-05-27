package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.google.common.collect.Sets;
import com.rocketmotordesign.controler.request.BurnRatePressureData;

import com.rocketmotordesign.controler.request.CustomPropellantRequest;
import com.rocketmotordesign.utils.TestHelper;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.infra.JSRMConstant.UNIVERSAL_GAS_CONSTANT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

public class CustomPropellantTest {

    @Test
    void shouldCreateCustomPropellant() {
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
    void shouldCreateCustomPropellantWithAllValueWithoutCstar() {
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
    void shouldResolveChamberTemperature() {

        CustomPropellantRequest kndxFromSRM2014 = TestHelper.buildKNDXFromSRM2014();
        double k = kndxFromSRM2014.getK();

        CustomPropellant customPropellant = new CustomPropellant(
                912.38154,
                2d,
                3d,
                kndxFromSRM2014.getDensity(),
                k,
                null, kndxFromSRM2014.getMolarMass(), null, null);

        assertThat(customPropellant.getRat()).isCloseTo(UNIVERSAL_GAS_CONSTANT / kndxFromSRM2014.getMolarMass(), Offset.offset(0.0001d));
        assertThat(customPropellant.getChamberTemperature()).isCloseTo(kndxFromSRM2014.getChamberTemperature(), Offset.offset(0.0001d));

    }

    @Test
    void shouldNotResolveChamberTemperature() {
        PropellantType propellant = KNDX;
        double k = propellant.getK();

        CustomPropellant customPropellant = new CustomPropellant(
                912.38154,
                2d,
                3d,
                propellant.getIdealMassDensity(),
                k,
                null, propellant.getEffectiveMolecularWeight(), 2345d, null);

        assertThat(customPropellant.getChamberTemperature()).isEqualTo(2345d);
    }

    @Test
    void shoudlUseBurnRateDatasInstead() {

        PropellantType propellant = KNDX;
        double k = propellant.getK();

        CustomPropellant customPropellant = new CustomPropellant(
                912.38154,
                2d,
                3d,
                propellant.getIdealMassDensity(),
                k,
                null, propellant.getEffectiveMolecularWeight(), 2345d, Sets.newHashSet(
                new BurnRatePressureData(1, 2, 10, 11),
                new BurnRatePressureData(3, 4, 11, 12)
        ));

        assertThat(customPropellant.getBurnRateCoefficient(10)).isEqualTo(1);
        assertThat(customPropellant.getBurnRateCoefficient(10.999)).isEqualTo(1);
        assertThat(customPropellant.getPressureExponent(10)).isEqualTo(2);
        assertThat(customPropellant.getPressureExponent(10.999)).isEqualTo(2);

        assertThat(customPropellant.getBurnRateCoefficient(11)).isEqualTo(3);
        assertThat(customPropellant.getBurnRateCoefficient(11.99)).isEqualTo(3);
        assertThat(customPropellant.getPressureExponent(11)).isEqualTo(4);
        assertThat(customPropellant.getPressureExponent(11.99)).isEqualTo(4);
    }

    @Test
    void shoudlThrowExceptionWhenPressureIsOUtOfBounds() {

        PropellantType propellant = KNDX;
        double k = propellant.getK();

        CustomPropellant customPropellant = new CustomPropellant(
                912.38154,
                2d,
                3d,
                propellant.getIdealMassDensity(),
                k,
                null, propellant.getEffectiveMolecularWeight(), 2345d, Sets.newHashSet(
                new BurnRatePressureData(1, 2, 10, 11)
        ));

        assertThatThrownBy(() -> customPropellant.getBurnRateCoefficient(12))
                .isInstanceOf(CustomPropellantChamberPressureOutOfBoundException.class)
                .hasMessage("Your custom propellant has no burn rate coefficient for this pressure (12.0 MPa). The pressure should be in the range you defined [10.0..11.0)");

        assertThatThrownBy(() -> customPropellant.getPressureExponent(12))
                .isInstanceOf(CustomPropellantChamberPressureOutOfBoundException.class)
                .hasMessage("Your custom propellant has no pressure exponent for this pressure (12.0 MPa). The pressure should be in the range you defined [10.0..11.0)");
    }

    @Test
    void shoudlThrowExceptionWhenBurnRateDataOverlapping() {
        assertThatThrownBy(() -> new CustomPropellant(
                912.38154,
                2d,
                3d,
                4d,
                5d,
                null, 3d, 2345d, Sets.newHashSet(
                        new BurnRatePressureData(1, 2, 10, 11),
                        new BurnRatePressureData(1, 2, 9, 11)
                )))
                .isInstanceOf(BurnRateDataException.class)
                .hasMessage("Overlapping ranges: range [9.0..11.0) overlaps with entry [10.0..11.0)");
    }

    @Test
    void shoudlThrowExceptionWhenBurnRateDataIsInvalid() {
        assertThatThrownBy(() -> new CustomPropellant(
                912.38154,
                2d,
                3d,
                4d,
                5d,
                null, 3d, 2345d, Sets.newHashSet(
                new BurnRatePressureData(1, 2, 11, 2)
        )))
                .isInstanceOf(BurnRateDataException.class)
                .hasMessage("Invalid range: [11.0..2.0)");
    }
}
