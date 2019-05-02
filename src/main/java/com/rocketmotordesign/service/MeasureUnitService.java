package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.rocketmotordesign.controler.dto.*;
import com.rocketmotordesign.propellant.BurnRateCoefficientConverter;
import com.rocketmotordesign.propellant.CustomPropellant;

import org.springframework.stereotype.Service;
import tec.units.ri.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Pressure;

import static com.rocketmotordesign.controler.dto.MeasureUnit.JSRM_UNITS;
import static com.rocketmotordesign.controler.dto.MeasureUnit.SI;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MeasureUnitService {

    public SolidRocketMotor toSolidRocketMotor(ComputationRequest request) {
        return new SolidRocketMotor(
                toPropellantGrain(request),
                toCombustionChamber(request),
                convertLengthToJSRM(request.getMeasureUnit().getLenghtUnit(), request.getThroatDiameter())
        );
    }

    public JSRMConfig toJSRMConfig(ExtraConfiguration extraConfig, MeasureUnit userUnits) {
        JSRMConfigBuilder jsrmConfigBuilder = new JSRMConfigBuilder()
                //TODO: champs non pris en compte car masqué dans IHM Meteor
                //.withAmbiantPressureInMPa(convertPressureToJSRM(userUnits.getPressureUnit(), extraConfig.getAmbiantPressure()))
                .withCombustionEfficiencyRatio(extraConfig.getCombustionEfficiencyRatio())
                .withDensityRatio(extraConfig.getDensityRatio())
                .withErosiveBurningAreaRatioThreshold(extraConfig.getErosiveBurningAreaRatioThreshold())
                .withErosiveBurningVelocityCoefficient(extraConfig.getErosiveBurningVelocityCoefficient())
                .withNozzleEfficiency(extraConfig.getNozzleEfficiency())
                .withNozzleErosionInMillimeter(convertLengthToJSRM(userUnits.getLenghtUnit(), extraConfig.getNozzleErosion()))
                .withOptimalNozzleDesign(extraConfig.isOptimalNozzleDesign());

        if(extraConfig.getNozzleExpansionRatio() != null){
            jsrmConfigBuilder.withNozzleExpansionRatio(extraConfig.getNozzleExpansionRatio());
        }

        return jsrmConfigBuilder.createJSRMConfig();
    }

    public PerformanceResult toPerformanceResult(JSRMResult jsrmResult, JSRMConfig jsrmConfig, MeasureUnit userUnits) {
        return new PerformanceResult(
                jsrmResult.getMotorClassification() + String.valueOf(jsrmResult.getAverageThrustInNewton()),
                jsrmResult.getMaxThrustInNewton(),
                jsrmResult.getTotalImpulseInNewtonSecond(),
                jsrmResult.getSpecificImpulseInSecond(),
                convertPressureToMeteor(userUnits.getPressureUnit(), jsrmResult.getMaxChamberPressureInMPa()),
                jsrmResult.getThrustTimeInSecond(),
                jsrmConfig.isOptimalNozzleDesign(),
                convertLengthToMeteor(userUnits.getLenghtUnit(), jsrmResult.getNozzle().getNozzleExitDiameterInMillimeter()),
                jsrmResult.getNozzle().getInitialNozzleExitSpeedInMach(),
                convertPressureToMeteor(userUnits.getPressureUnit(), jsrmResult.getAverageChamberPressureInMPa()),
                convertLengthToMeteor(userUnits.getLenghtUnit(), jsrmResult.getNozzle().getChamberInsideDiameterInMillimeter() - jsrmResult.getNozzle().getNozzleThroatDiameterInMillimeter()),
                convertLengthToMeteor(userUnits.getLenghtUnit(), jsrmResult.getNozzle().getNozzleExitDiameterInMillimeter() - jsrmResult.getNozzle().getNozzleThroatDiameterInMillimeter()),
                jsrmResult.getNozzle().getOptimalNozzleExpansionRatio()
        );
    }

    public GraphResult toGraphResult(MotorParameters motorParameters, MeasureUnit userUnits) {
        return new GraphResult(
                motorParameters.getTimeSinceBurnStartInSecond(),
                motorParameters.getThrustInNewton(),
                motorParameters.getKn(),
                convertPressureToMeteor(userUnits.getPressureUnit(), motorParameters.getChamberPressureInMPa()),
                convertMassToMeteor(userUnits.getMassUnit(), motorParameters.getMassFlowRateInKgPerSec())
        );
    }

    private CombustionChamber toCombustionChamber(ComputationRequest request) {
        Unit<Length> userLengthUnit = request.getMeasureUnit().getLenghtUnit();
        return new CombustionChamber(
                convertLengthToJSRM(userLengthUnit, request.getChamberInnerDiameter()),
                convertLengthToJSRM(userLengthUnit, request.getChamberLength()));
    }

    private PropellantGrain toPropellantGrain(ComputationRequest request) {
        Unit<Length> userLengthUnit = request.getMeasureUnit().getLenghtUnit();
        return new PropellantGrain(getPropellant(request),
                convertLengthToJSRM(userLengthUnit, request.getOuterDiameter()),
                convertLengthToJSRM(userLengthUnit, request.getCoreDiameter()),
                convertLengthToJSRM(userLengthUnit, request.getSegmentLength()),
                request.getNumberOfSegment(),
                request.getOuterSurface(),
                request.getEndsSurface(),
                request.getCoreSurface());
    }

    private SolidPropellant getPropellant(ComputationRequest request) {
        Map<String, SolidPropellant> propellants = Stream.of(PropellantType.values())
                .collect(toMap(Enum::name, Function.identity()));

        return propellants.computeIfAbsent(request.getPropellantType(), propellantType -> propellantToSIUnits(request));
    }

    private SolidPropellant propellantToSIUnits(ComputationRequest request) {
        CustomPropellantRequest customPropellantRequest = request.getCustomPropellant();
        boolean si = request.getMeasureUnit() == SI;

        CustomPropellant customPropellant = new CustomPropellant(
                si? customPropellantRequest.getCstar() : customPropellantRequest.getCstar()!=null? customPropellantRequest.getCstar() * 0.3048: null,
                si? customPropellantRequest.getBurnRateCoefficient() : customPropellantRequest.getBurnRateCoefficient()!=null ? BurnRateCoefficientConverter.toMetrique(customPropellantRequest.getBurnRateCoefficient(), customPropellantRequest.getPressureExponent()): null,
                customPropellantRequest.getPressureExponent(),
                si? customPropellantRequest.getDensity() : convertDensityToJSRM(customPropellantRequest.getDensity()),
                customPropellantRequest.getK(),
                customPropellantRequest.getK2ph(),
                customPropellantRequest.getMolarMass(),
                customPropellantRequest.getChamberTemperature(),
                convertBurnRateDataToJSRM(request));
        return customPropellant;
    }

    private Set<BurnRatePressureData> convertBurnRateDataToJSRM(ComputationRequest request) {
        boolean si = request.getMeasureUnit() == SI;

        Set<BurnRatePressureData> burnRateDataSet = request.getCustomPropellant().getBurnRateDataSet();
        if(burnRateDataSet != null){
            return burnRateDataSet.stream()
                    .map(burnRatePressureData -> new BurnRatePressureData(
                            si? burnRatePressureData.getBurnRateCoefficient() : BurnRateCoefficientConverter.toMetrique(burnRatePressureData.getBurnRateCoefficient(), burnRatePressureData.getPressureExponent()),
                            burnRatePressureData.getPressureExponent(),
                            convertPressureToJSRM(request.getMeasureUnit().getPressureUnit(), burnRatePressureData.getFromPressureIncluded()),
                            convertPressureToJSRM(request.getMeasureUnit().getPressureUnit(), burnRatePressureData.getToPressureExcluded())
                    ))
                    .collect(toSet());
            } else {
            return emptySet();
        }
    }

    private Double convertDensityToJSRM(double idealMassDensity) {
        return idealMassDensity*453.6/Math.pow(2.54, 3);
    }

    private double convertLengthToJSRM(Unit<Length> lenghtUnit, double length) {
        return Quantities.getQuantity(length, lenghtUnit).to(JSRM_UNITS.getLenghtUnit()).getValue().doubleValue();
    }

    private double convertPressureToJSRM(Unit<Pressure> pressureUnit, double pressure) {
        return Quantities.getQuantity(pressure, pressureUnit).to(JSRM_UNITS.getPressureUnit()).getValue().doubleValue();
    }

    private double convertPressureToMeteor(Unit<Pressure> pressureUnit, double pressure) {
        return Quantities.getQuantity(pressure, JSRM_UNITS.getPressureUnit()).to(pressureUnit).getValue().doubleValue();
    }

    private double convertMassToMeteor(Unit<Mass> massUnit, double mass) {
        return Quantities.getQuantity(mass, JSRM_UNITS.getMassUnit()).to(massUnit).getValue().doubleValue();
    }


    private double convertLengthToMeteor(Unit<Length> lengthUnit, double length) {
        return Quantities.getQuantity(length, JSRM_UNITS.getLenghtUnit()).to(lengthUnit).getValue().doubleValue();
    }
}
