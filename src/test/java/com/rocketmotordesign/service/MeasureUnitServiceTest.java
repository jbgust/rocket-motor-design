package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.github.jbgust.jsrm.application.result.Nozzle;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ExtraConfiguration;
import com.rocketmotordesign.controler.dto.GraphResult;
import com.rocketmotordesign.controler.dto.PerformanceResult;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.rocketmotordesign.controler.dto.MeasureUnit.IMPERIAL;
import static com.rocketmotordesign.controler.dto.MeasureUnit.SI;
import static com.rocketmotordesign.utils.TestHelper.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;


@RunWith(SpringRunner.class)
@Import(MeasureUnitService.class)
public class MeasureUnitServiceTest {

    private static final Offset<Double> DEFAULT_OFFSET = offset(0.0001);
    private static final double MPa_TO_PSI_RATIO = 1000000d/6895;

    @Autowired
    private MeasureUnitService measureUnitService;

    @Test
    public void shouldConvertMotorFromImperialUnitToJSRMUnit() {

        ComputationRequest defaultRequestSIUnit = getDefaultRequest();

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(getDefaultRequestImperial());

        assertThat(solidRocketMotor.getThroatDiameterInMillimeter()).isEqualTo(defaultRequestSIUnit.getThroatDiameter());

        assertThat(solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()).isCloseTo(defaultRequestSIUnit.getChamberLength(), DEFAULT_OFFSET);
        assertThat(solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()).isEqualTo(defaultRequestSIUnit.getChamberInnerDiameter());

        assertThat(solidRocketMotor.getPropellantGrain().getCoreDiameter()).isEqualTo(defaultRequestSIUnit.getCoreDiameter());
        assertThat(solidRocketMotor.getPropellantGrain().getOuterDiameter()).isEqualTo(defaultRequestSIUnit.getOuterDiameter());
        assertThat(solidRocketMotor.getPropellantGrain().getSegmentLength()).isCloseTo(defaultRequestSIUnit.getSegmentLength(), DEFAULT_OFFSET);
        assertThat(solidRocketMotor.getPropellantGrain().getNumberOfSegment()).isEqualTo(defaultRequestSIUnit.getNumberOfSegment());
        assertThat(solidRocketMotor.getPropellantGrain().getOuterSurface()).isEqualTo(defaultRequestSIUnit.getOuterSurface());
        assertThat(solidRocketMotor.getPropellantGrain().getEndsSurface()).isEqualTo(defaultRequestSIUnit.getEndsSurface());
        assertThat(solidRocketMotor.getPropellantGrain().getCoreSurface()).isEqualTo(defaultRequestSIUnit.getCoreSurface());
        assertThat(solidRocketMotor.getPropellantGrain().getPropellant()).isEqualTo(KNDX);
    }

    @Test
    public void shouldConvertConfigFromImperialUnitToJSRMUnit() {

        ExtraConfiguration defaultExtraConfigSIUnit = getDefaultExtraConfiguration();

        ExtraConfiguration defaultImperialExtraConfiguration = getDefaultImperialExtraConfiguration();
        defaultImperialExtraConfiguration.setNozzleErosion(5);

        JSRMConfig jsrmConfig = measureUnitService.toJSRMConfig(defaultImperialExtraConfiguration, IMPERIAL);

        assertThat(jsrmConfig.getDensityRatio()).isEqualTo(defaultExtraConfigSIUnit.getDensityRatio());
        assertThat(jsrmConfig.getNozzleErosionInMillimeter()).isEqualTo(5*25.4);
        assertThat(jsrmConfig.getCombustionEfficiencyRatio()).isEqualTo(defaultExtraConfigSIUnit.getCombustionEfficiencyRatio());
        assertThat(jsrmConfig.getAmbiantPressureInMPa()).isEqualTo(defaultExtraConfigSIUnit.getAmbiantPressure());
        assertThat(jsrmConfig.getErosiveBurningAreaRatioThreshold()).isEqualTo(defaultExtraConfigSIUnit.getErosiveBurningAreaRatioThreshold());
        assertThat(jsrmConfig.getErosiveBurningVelocityCoefficient()).isEqualTo(defaultExtraConfigSIUnit.getErosiveBurningVelocityCoefficient());
        assertThat(jsrmConfig.getNozzleEfficiency()).isEqualTo(defaultExtraConfigSIUnit.getNozzleEfficiency());
        assertThat(jsrmConfig.isOptimalNozzleDesign()).isEqualTo(defaultExtraConfigSIUnit.isOptimalNozzleDesign());
        assertThat(jsrmConfig.getNozzleExpansionRatio()).isEqualTo(defaultExtraConfigSIUnit.getNozzleExpansionRatio());
    }

    @Test
    public void shouldConvertPerformanceResultFromJSRMUnitToImperialUnit() {

        Nozzle nozzle = new Nozzle(
                23,
                34,
                8,
                45.3,
                7,
                39.5,
                2.3,
                2.1
        );

        JSRMResult jsrmResult = new JSRMResult(
                10,
                11,
                12,
                5.12,
                4.23,
                13,
                MotorClassification.A,
                emptyList(),
                nozzle,
                15);

        PerformanceResult performanceResult = measureUnitService.toPerformanceResult(jsrmResult, measureUnitService.toJSRMConfig(getDefaultExtraConfiguration(), SI), IMPERIAL);

        assertThat(performanceResult.getMotorDescription()).isEqualTo("A15");
        assertThat(performanceResult.getMaxThrust()).isEqualTo(format(jsrmResult.getMaxThrustInNewton()));
        assertThat(performanceResult.getTotalImpulse()).isEqualTo(format(jsrmResult.getTotalImpulseInNewtonSecond()));
        assertThat(performanceResult.getSpecificImpulse()).isEqualTo(format(jsrmResult.getSpecificImpulseInSecond()));
        assertThat(performanceResult.getMaxPressure()).isEqualTo(format(jsrmResult.getMaxChamberPressureInMPa()*MPa_TO_PSI_RATIO));
        assertThat(performanceResult.getThrustTime()).isEqualTo(format(jsrmResult.getThrustTimeInSecond()));
        assertThat(performanceResult.isOptimalDesign()).isEqualTo(true);
        assertThat(performanceResult.getNozzleExitDiameter()).isEqualTo(format(nozzle.getNozzleExitDiameterInMillimeter()/25.4));
        assertThat(performanceResult.getExitSpeedInitial()).isEqualTo(format(nozzle.getInitialNozzleExitSpeedInMach()));
        assertThat(performanceResult.getAveragePressure()).isEqualTo(format(jsrmResult.getAverageChamberPressureInMPa()*MPa_TO_PSI_RATIO));
        assertThat(performanceResult.getConvergenceCrossSectionDiameter()).isCloseTo((nozzle.getChamberInsideDiameterInMillimeter()-nozzle.getNozzleThroatDiameterInMillimeter())/25.4, DEFAULT_OFFSET);
        assertThat(performanceResult.getDivergenceCrossSectionDiameter()).isCloseTo((nozzle.getNozzleExitDiameterInMillimeter() - nozzle.getNozzleThroatDiameterInMillimeter())/25.4, DEFAULT_OFFSET);
        assertThat(performanceResult.getOptimalNozzleExpansionRatio()).isEqualTo(format(nozzle.getOptimalNozzleExpansionRatio()));
    }

    @Test
    public void shouldConvertGraphResultFromJSRMUnitToImperialUnit() {

        MotorParameters motorParameters = new MotorParameters(23.6, 45.6, 234.12, 5.12, 23.12);

        GraphResult graphResult = measureUnitService.toGraphResult(motorParameters, IMPERIAL);

        assertThat(graphResult.getKn()).isEqualTo(motorParameters.getKn());
        assertThat(graphResult.getP()).isCloseTo(motorParameters.getChamberPressureInMPa()*MPa_TO_PSI_RATIO, DEFAULT_OFFSET);
        assertThat(graphResult.getX()).isEqualTo(motorParameters.getTimeSinceBurnStartInSecond());
        assertThat(graphResult.getY()).isEqualTo(motorParameters.getThrustInNewton());
        assertThat(graphResult.getM()).isCloseTo(motorParameters.getMassFlowRateInKgPerSec()/0.45359237, DEFAULT_OFFSET);
    }

    private String format(Double aDouble) {
        return String.format(Locale.ENGLISH, "%.2f", aDouble);
    }

}