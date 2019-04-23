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
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.function.Function;
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
                .collect(toMap(o -> o.name(), Function.identity()));

        double a = 0.0174;
        double n = 0.4285;
        double aMetrique = BurnRateCoefficientConverter.toMetrique(a, n);
        double densite = 0.06 * 453.6 / Math.pow(2.54,3);
        double cstar = 5468.4 * 0.3048;
        double isp = 150d;
        double k = 1.2768;
        double molarMass = 56;
        propellants.put("WIMPY_RED", new CustomPropellant(cstar, isp,aMetrique, n, densite, k, molarMass));
        return propellants.get(request.getPropellantType());
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
