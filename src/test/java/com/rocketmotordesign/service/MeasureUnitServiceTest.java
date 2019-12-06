package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.FinocylGrain;
import com.github.jbgust.jsrm.application.motor.grain.GrainSurface;
import com.github.jbgust.jsrm.application.motor.grain.HollowCylinderGrain;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.github.jbgust.jsrm.application.result.Nozzle;
import com.google.common.collect.Sets;
import com.rocketmotordesign.controler.request.*;
import com.rocketmotordesign.controler.response.GraphResult;
import com.rocketmotordesign.controler.response.PerformanceResult;
import com.rocketmotordesign.propellant.BurnRateCoefficientConverter;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import tec.units.ri.unit.Units;

import java.util.Locale;

import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.service.MeasureUnit.IMPERIAL;
import static com.rocketmotordesign.utils.TestHelper.*;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.springframework.test.util.ReflectionTestUtils.getField;


@RunWith(SpringRunner.class)
@Import(MeasureUnitService.class)
public class MeasureUnitServiceTest {

    private static final Offset<Double> DEFAULT_OFFSET = offset(0.0001);
    private static final double MPa_TO_PSI_RATIO = 1000000d/6895;

    @Autowired
    private MeasureUnitService measureUnitService;

    @Test
    public void shouldConvertMotorFromImperialUnitToJSRMUnit() {

        HollowComputationRequest defaultRequestSIUnit = getDefaultRequest();

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(getDefaultRequestImperial());

        assertThat(solidRocketMotor.getThroatDiameterInMillimeter()).isEqualTo(defaultRequestSIUnit.getThroatDiameter());

        assertThat(solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()).isCloseTo(defaultRequestSIUnit.getChamberLength(), DEFAULT_OFFSET);
        assertThat(solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()).isEqualTo(defaultRequestSIUnit.getChamberInnerDiameter());

        HollowCylinderGrain grain = (HollowCylinderGrain) solidRocketMotor.getPropellantGrain().getGrainConfigutation();

        assertThat((double) getField(grain, "coreDiameter")).isEqualTo(defaultRequestSIUnit.getCoreDiameter());
        assertThat((double) getField(grain, "outerDiameter")).isEqualTo(defaultRequestSIUnit.getOuterDiameter());
        assertThat((double) getField(grain, "segmentLength")).isCloseTo(defaultRequestSIUnit.getSegmentLength(), DEFAULT_OFFSET);
        assertThat((int) getField(grain, "numberOfSegment")).isEqualTo(defaultRequestSIUnit.getNumberOfSegment());
        assertThat((GrainSurface) getField(grain, "outerSurface")).isEqualTo(defaultRequestSIUnit.getOuterSurface());
        assertThat((GrainSurface) getField(grain, "endsSurface")).isEqualTo(defaultRequestSIUnit.getEndsSurface());
        assertThat((GrainSurface) getField(grain, "coreSurface")).isEqualTo(defaultRequestSIUnit.getCoreSurface());
        assertThat(solidRocketMotor.getPropellantGrain().getPropellant()).isEqualTo(KNDX);
    }


    @Test
    public void shouldConvertFinocylGrainMotorFromImperialUnitToJSRMUnit() {

        FinocylComputationRequest finocylRequest = getDefaultFinocylRequest();

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(getDefaultFinocylRequestImperial());

        assertThat(solidRocketMotor.getThroatDiameterInMillimeter()).isEqualTo(finocylRequest.getThroatDiameter());

        assertThat(solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter()).isCloseTo(finocylRequest.getChamberLength(), DEFAULT_OFFSET);
        assertThat(solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter()).isEqualTo(finocylRequest.getChamberInnerDiameter());

        FinocylGrain grain = (FinocylGrain) solidRocketMotor.getPropellantGrain().getGrainConfigutation();

        assertThat((double) getField(grain, "innerDiameter")).isEqualTo(finocylRequest.getInnerDiameter());
        assertThat((double) getField(grain, "outerDiameter")).isEqualTo(finocylRequest.getOuterDiameter());
        assertThat((double) getField(grain, "finWidth")).isCloseTo(finocylRequest.getFinWidth(), offset(0.000001));
        assertThat((double) getField(grain, "finDiameter")).isEqualTo(finocylRequest.getFinDiameter());
        assertThat((int) getField(grain, "finCount")).isEqualTo(finocylRequest.getFinCount());

        assertThat(grain.getLength()).isCloseTo(finocylRequest.getSegmentLength(), DEFAULT_OFFSET);
        assertThat((int) getField(grain, "numberOfSegment")).isEqualTo(finocylRequest.getNumberOfSegment());
        assertThat(solidRocketMotor.getPropellantGrain().getPropellant()).isEqualTo(KNSU);
    }

    @Test
    public void shouldConvertPropellantFromSIUnitToJSRMUnit() {
        // GIVEN
        HollowComputationRequest request = getDefaultRequest();
        CustomPropellantRequest propellantRequest = createPropellantWithBasicInfo(KNSU);
        request.setCustomPropellant(propellantRequest);
        request.setPropellantType("CUSTOM_PROPELLANT");

        // WHEN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        // THEN
        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();
        assertThat(propellant.getBurnRateCoefficient(0)).isEqualTo(propellantRequest.getBurnRateCoefficient());
        assertThat(propellant.getPressureExponent(0)).isEqualTo(propellantRequest.getPressureExponent());
        assertThat(propellant.getIdealMassDensity()).isEqualTo(propellantRequest.getDensity());
        assertThat(propellant.getK()).isEqualTo(propellantRequest.getK());
        assertThat(propellant.getK2Ph()).isEqualTo(propellantRequest.getK2ph());
        assertThat(propellant.getChamberTemperature()).isEqualTo(propellantRequest.getChamberTemperature());
        assertThat(propellant.getEffectiveMolecularWeight()).isEqualTo(propellantRequest.getMolarMass());
    }

    @Test
    public void shouldConvertPropellantWithComplexBurnRateFromSIUnitToJSRMUnit() {
        // GIVEN
        CustomPropellantRequest propellantRequest = createPropellantWithBasicInfo(KNSU);
        propellantRequest.setBurnRateDataSet(Sets.newHashSet(
                new BurnRatePressureData(1, 2, 12, 24),
                new BurnRatePressureData(3, 4, 24, 30)
        ));

        HollowComputationRequest request = getDefaultRequest();
        request.setCustomPropellant(propellantRequest);
        request.setPropellantType("CUSTOM_PROPELLANT");

        // THEN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        // WHEN
        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();
        assertThat(propellant.getIdealMassDensity()).isEqualTo(propellantRequest.getDensity());
        assertThat(propellant.getK()).isEqualTo(propellantRequest.getK());
        assertThat(propellant.getK2Ph()).isEqualTo(propellantRequest.getK2ph());
        assertThat(propellant.getChamberTemperature()).isEqualTo(propellantRequest.getChamberTemperature());
        assertThat(propellant.getEffectiveMolecularWeight()).isEqualTo(propellantRequest.getMolarMass());

        assertThat(propellant.getBurnRateCoefficient(12.1)).isEqualTo(1);
        assertThat(propellant.getBurnRateCoefficient(23.)).isEqualTo(1);
        assertThat(propellant.getBurnRateCoefficient(24.1)).isEqualTo(3);
        assertThat(propellant.getBurnRateCoefficient(29.)).isEqualTo(3);
    }

    @Test
    public void shouldConvertPropellantWithCstarFromSIUnitToJSRMUnit() {
        // GIVEN
        CustomPropellantRequest propellantRequest = createPropellantWithBasicInfo(KNDX);
        propellantRequest.setCstar(912.38154);
        propellantRequest.setChamberTemperature(null);

        HollowComputationRequest request = getDefaultRequest();
        request.setCustomPropellant(propellantRequest);
        request.setPropellantType("CUSTOM_PROPELLANT");

        // WHEN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        // THEN
        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();

        assertThat(propellant.getBurnRateCoefficient(0)).isCloseTo(KNDX.getBurnRateCoefficient(0), offset(0.01));
        assertThat(propellant.getPressureExponent(0)).isCloseTo(KNDX.getPressureExponent(0), offset(0.01));
        assertThat(propellant.getIdealMassDensity()).isCloseTo(KNDX.getIdealMassDensity(), offset(0.01));
        assertThat(propellant.getChamberTemperature()).isCloseTo(KNDX.getChamberTemperature(), offset(0.01));
    }

    @Test
    public void shouldConvertPropellantFromImperialUnitToJSRMUnit() {
        // GIVEN
        CustomPropellantRequest propellantRequest = createPropellantWithBasicInfo(KNSU);
        propellantRequest.setBurnRateCoefficient(0.0665);
        propellantRequest.setPressureExponent(0.319);
        propellantRequest.setDensity(0.06824);

        HollowComputationRequest request = getDefaultRequestImperial();
        request.setCustomPropellant(propellantRequest);
        request.setPropellantType("CUSTOM_PROPELLANT");

        // WHEN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        // THEN
        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();

        assertThat(propellant.getBurnRateCoefficient(0)).isCloseTo(KNSU.getBurnRateCoefficient(0), offset(0.01));
        assertThat(propellant.getPressureExponent(0)).isCloseTo(KNSU.getPressureExponent(0), offset(0.01));
        assertThat(propellant.getIdealMassDensity()).isCloseTo(KNSU.getIdealMassDensity(), offset(0.01));
        assertThat(propellant.getChamberTemperature()).isCloseTo(KNSU.getChamberTemperature(), offset(0.01));
    }

    @Test
    public void shouldConvertPropellantWithComplexBurnRateFromIMPERIALUnitToJSRMUnit() {
        // GIVEN
        CustomPropellantRequest propellantRequest = createPropellantWithBasicInfo(KNDX);
        propellantRequest.setDensity(0.06824);
        propellantRequest.setBurnRateDataSet(Sets.newHashSet(
                new BurnRatePressureData(1, 2, 1200, 2400), //PSI
                new BurnRatePressureData(3, 4, 2400, 3000)  //PSI
        ));
        double burnRateCoeff1Metrique = BurnRateCoefficientConverter.toMetrique(1, 2);
        double burnRateCoeff2Metrique = BurnRateCoefficientConverter.toMetrique(3, 4);

        HollowComputationRequest request = getDefaultRequestImperial();
        request.setCustomPropellant(propellantRequest);
        request.setPropellantType("CUSTOM_PROPELLANT");

        // THEN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        // WHEN
        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();
        assertThat(propellant.getIdealMassDensity()).isCloseTo(KNDX.getIdealMassDensity(), offset(0.01));
        assertThat(propellant.getK()).isEqualTo(KNDX.getK());
        assertThat(propellant.getK2Ph()).isEqualTo(KNDX.getK2Ph());
        assertThat(propellant.getChamberTemperature()).isEqualTo(KNDX.getChamberTemperature());
        assertThat(propellant.getEffectiveMolecularWeight()).isEqualTo(KNDX.getEffectiveMolecularWeight());

        assertThat(propellant.getBurnRateCoefficient(8.3)).isEqualTo(burnRateCoeff1Metrique);
        assertThat(propellant.getBurnRateCoefficient(16.54)).isEqualTo(burnRateCoeff1Metrique);
        assertThat(propellant.getBurnRateCoefficient(16.55)).isEqualTo(burnRateCoeff2Metrique);
        assertThat(propellant.getBurnRateCoefficient(20.67)).isEqualTo(burnRateCoeff2Metrique);
    }

    @Test
    public void shouldConvertPropellantWithCstarFromIMPERIALUnitToJSRMUnit() {
        // GIVEN
        CustomPropellantRequest propellantRequest = createPropellantWithBasicInfo(KNDX);
        propellantRequest.setBurnRateCoefficient(0.0665);    //converti de KNSU
        propellantRequest.setPressureExponent(0.319);        //converti de KNSU
        propellantRequest.setDensity(0.06824);
        propellantRequest.setCstar(912.38154 / 0.3048);      //converti de KNDX
        propellantRequest.setChamberTemperature(null);

        HollowComputationRequest request = getDefaultRequestImperial();
        request.setCustomPropellant(propellantRequest);
        request.setPropellantType("CUSTOM_PROPELLANT");

        // WHEN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        // THEN
        SolidPropellant propellant = solidRocketMotor.getPropellantGrain().getPropellant();

        assertThat(propellant.getBurnRateCoefficient(0)).isCloseTo(KNSU.getBurnRateCoefficient(0), offset(0.01));
        assertThat(propellant.getPressureExponent(0)).isCloseTo(KNSU.getPressureExponent(0), offset(0.01));
        assertThat(propellant.getIdealMassDensity()).isCloseTo(KNDX.getIdealMassDensity(), offset(0.01));
        assertThat(propellant.getChamberTemperature()).isCloseTo(KNDX.getChamberTemperature(), offset(0.01));
    }

    @Test
    public void shouldConvertConfigFromImperialUnitToJSRMUnit() {

        ExtraConfiguration defaultExtraConfigSIUnit = getDefaultExtraConfiguration();

        ExtraConfiguration defaultImperialExtraConfiguration = getDefaultImperialExtraConfiguration();
        defaultImperialExtraConfiguration.setNozzleErosion(5);

        JSRMConfig jsrmConfig = measureUnitService.toJSRMConfig(defaultImperialExtraConfiguration, IMPERIAL, true);

        assertThat(jsrmConfig.getDensityRatio()).isEqualTo(defaultExtraConfigSIUnit.getDensityRatio());
        assertThat(jsrmConfig.getNozzleErosionInMillimeter()).isEqualTo(5*25.4);
        assertThat(jsrmConfig.getCombustionEfficiencyRatio()).isEqualTo(defaultExtraConfigSIUnit.getCombustionEfficiencyRatio());
        assertThat(jsrmConfig.getAmbiantPressureInMPa()).isEqualTo(defaultExtraConfigSIUnit.getAmbiantPressureInMPa());
        assertThat(jsrmConfig.getErosiveBurningAreaRatioThreshold()).isEqualTo(defaultExtraConfigSIUnit.getErosiveBurningAreaRatioThreshold());
        assertThat(jsrmConfig.getErosiveBurningVelocityCoefficient()).isEqualTo(defaultExtraConfigSIUnit.getErosiveBurningVelocityCoefficient());
        assertThat(jsrmConfig.getNozzleEfficiency()).isEqualTo(defaultExtraConfigSIUnit.getNozzleEfficiency());
        assertThat(jsrmConfig.isOptimalNozzleDesign()).isEqualTo(defaultExtraConfigSIUnit.isOptimalNozzleDesign());
        assertThat(jsrmConfig.getNozzleExpansionRatio()).isEqualTo(defaultExtraConfigSIUnit.getNozzleExpansionRatio());
        assertThat(jsrmConfig.isSafeKNFailure()).isTrue();
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
                15,
                1.0,
                0);

        PerformanceResult performanceResult = measureUnitService.toPerformanceResult(jsrmResult, getDefaultExtraConfiguration().isOptimalNozzleDesign(), IMPERIAL);

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
        assertThat(performanceResult.isLowKNCorrection()).isFalse();
        assertThat(performanceResult.getGrainMass()).isEqualTo("2.205");
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

    @Test
    public void shouldConvertMass() {
        assertThat(measureUnitService.convertMass(IMPERIAL.getMassUnit(), Units.KILOGRAM, 7.054792))
                .isCloseTo(3.2, offset(0.001));
    }

    @Test
    public void shouldconvertLengthToJSRM() {
        assertThat(measureUnitService.convertLengthToJSRM(IMPERIAL.getLenghtUnit(),19.685))
                .isCloseTo(500.0, offset(0.01));
    }

    private String format(Double aDouble) {
        return String.format(Locale.ENGLISH, "%.2f", aDouble);
    }


    private CustomPropellantRequest createPropellantWithBasicInfo(SolidPropellant solidPropellant) {
        CustomPropellantRequest propellantRequest = new CustomPropellantRequest();
        propellantRequest.setBurnRateCoefficient(solidPropellant.getBurnRateCoefficient(0));
        propellantRequest.setPressureExponent(solidPropellant.getPressureExponent(0));
        propellantRequest.setDensity(solidPropellant.getIdealMassDensity());
        propellantRequest.setK(solidPropellant.getK());
        propellantRequest.setK2ph(solidPropellant.getK2Ph());
        propellantRequest.setMolarMass(solidPropellant.getEffectiveMolecularWeight());
        propellantRequest.setChamberTemperature(solidPropellant.getChamberTemperature());
        return propellantRequest;
    }
}
