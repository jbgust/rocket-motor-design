package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.grain.*;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.rocketmotordesign.controler.request.*;
import com.rocketmotordesign.controler.response.GraphResult;
import com.rocketmotordesign.controler.response.PerformanceResult;
import com.rocketmotordesign.propellant.BurnRateCoefficientConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tec.units.ri.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Pressure;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.rocketmotordesign.service.MeasureUnit.JSRM_UNITS;
import static com.rocketmotordesign.service.MeasureUnit.SI;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
public class MeasureUnitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureUnitService.class);

    public SolidRocketMotor toSolidRocketMotor(BasicComputationRequest request) {
        return new SolidRocketMotor(
                toPropellantGrain(request),
                toCombustionChamber(request),
                convertLengthToJSRM(request.getMeasureUnit().getLenghtUnit(), request.getThroatDiameter())
        );
    }

    public JSRMConfig toJSRMConfig(ExtraConfiguration extraConfig, MeasureUnit userUnits, boolean safeKNRun) {
        JSRMConfigBuilder jsrmConfigBuilder = new JSRMConfigBuilder()
                .withAmbiantPressureInMPa(extraConfig.getAmbiantPressureInMPa())
                .withCombustionEfficiencyRatio(extraConfig.getCombustionEfficiencyRatio())
                .withDensityRatio(extraConfig.getDensityRatio())
                .withErosiveBurningAreaRatioThreshold(extraConfig.getErosiveBurningAreaRatioThreshold())
                .withErosiveBurningVelocityCoefficient(extraConfig.getErosiveBurningVelocityCoefficient())
                .withNozzleEfficiency(extraConfig.getNozzleEfficiency())
                .withNozzleErosionInMillimeter(convertLengthToJSRM(userUnits.getLenghtUnit(), extraConfig.getNozzleErosion()))
                .withOptimalNozzleDesign(extraConfig.isOptimalNozzleDesign())
                .withSafeKNFailure(safeKNRun);

        if(extraConfig.getNumberOfCalculationLine() != null) {
            jsrmConfigBuilder.withNumberOfCalculationLine(extraConfig.getNumberOfCalculationLine());
        }

        if(extraConfig.getNozzleExpansionRatio() != null){
            jsrmConfigBuilder.withNozzleExpansionRatio(extraConfig.getNozzleExpansionRatio());
        }

        return jsrmConfigBuilder.createJSRMConfig();
    }

    public PerformanceResult toPerformanceResult(JSRMResult jsrmResult, boolean optimalNozzleDesign, MeasureUnit userUnits) {
        return new PerformanceResult(
                jsrmResult.getMotorClassification() + String.valueOf(jsrmResult.getAverageThrustInNewton()),
                jsrmResult.getMaxThrustInNewton(),
                jsrmResult.getTotalImpulseInNewtonSecond(),
                jsrmResult.getSpecificImpulseInSecond(),
                convertPressureToMeteor(userUnits.getResultPressureUnit(), jsrmResult.getMaxChamberPressureInMPa()),
                jsrmResult.getThrustTimeInSecond(),
                optimalNozzleDesign,
                convertLengthToMeteor(userUnits.getLenghtUnit(), jsrmResult.getNozzle().getNozzleExitDiameterInMillimeter()),
                jsrmResult.getNozzle().getInitialNozzleExitSpeedInMach(),
                convertPressureToMeteor(userUnits.getResultPressureUnit(), jsrmResult.getAverageChamberPressureInMPa()),
                convertLengthToMeteor(userUnits.getLenghtUnit(), jsrmResult.getNozzle().getChamberInsideDiameterInMillimeter() - jsrmResult.getNozzle().getNozzleThroatDiameterInMillimeter()),
                convertLengthToMeteor(userUnits.getLenghtUnit(), jsrmResult.getNozzle().getNozzleExitDiameterInMillimeter() - jsrmResult.getNozzle().getNozzleThroatDiameterInMillimeter()),
                jsrmResult.getNozzle().getOptimalNozzleExpansionRatio(),
                jsrmResult.getNumberOfKNCorrection(),
                convertMassToMeteor(userUnits.getMassUnit(), jsrmResult.getGrainMassInKg()),
                jsrmResult.getMotorClassification(),
                userUnits);
    }

    public GraphResult toGraphResult(MotorParameters motorParameters, MeasureUnit userUnits) {
        return new GraphResult(
                motorParameters.getTimeSinceBurnStartInSecond(),
                motorParameters.getThrustInNewton(),
                motorParameters.getKn(),
                convertPressureToMeteor(userUnits.getResultPressureUnit(), motorParameters.getChamberPressureInMPa()),
                convertMassToMeteor(userUnits.getMassUnit(), motorParameters.getMassFlowRateInKgPerSec())
        );
    }

    private CombustionChamber toCombustionChamber(BasicComputationRequest request) {
        Unit<Length> userLengthUnit = request.getMeasureUnit().getLenghtUnit();
        return new CombustionChamber(
                convertLengthToJSRM(userLengthUnit, request.getChamberInnerDiameter()),
                convertLengthToJSRM(userLengthUnit, request.getChamberLength()));
    }

    private PropellantGrain toPropellantGrain(BasicComputationRequest request) {
        Unit<Length> userLengthUnit = request.getMeasureUnit().getLenghtUnit();
        GrainConfigutation grainConfigutation;
        if(request instanceof HollowComputationRequest){
            HollowComputationRequest hollowComputationRequest = (HollowComputationRequest)request;
            grainConfigutation = new HollowCylinderGrain(
                    convertLengthToJSRM(userLengthUnit, hollowComputationRequest.getOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, hollowComputationRequest.getCoreDiameter()),
                    convertLengthToJSRM(userLengthUnit, hollowComputationRequest.getSegmentLength()),
                    request.getNumberOfSegment(),
                    hollowComputationRequest.getOuterSurface(),
                    hollowComputationRequest.getEndsSurface(),
                    hollowComputationRequest.getCoreSurface());
        } else if(request instanceof FinocylComputationRequest) {
            FinocylComputationRequest finocylRequest = (FinocylComputationRequest)request;
            grainConfigutation = new FinocylGrain(
                    convertLengthToJSRM(userLengthUnit, finocylRequest.getOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, finocylRequest.getInnerDiameter()),
                    convertLengthToJSRM(userLengthUnit, finocylRequest.getFinWidth()),
                    convertLengthToJSRM(userLengthUnit, finocylRequest.getFinDiameter()),
                    finocylRequest.getFinCount(),
                    convertLengthToJSRM(userLengthUnit, finocylRequest.getSegmentLength()),
                    finocylRequest.getNumberOfSegment(),
                    finocylRequest.getEndsSurface()
            );
        } else if(request instanceof StarGrainComputationRequest) {
            StarGrainComputationRequest starGrainComputationRequest = (StarGrainComputationRequest)request;
            grainConfigutation = new StarGrain(
                    convertLengthToJSRM(userLengthUnit, starGrainComputationRequest.getOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, starGrainComputationRequest.getInnerDiameter()),
                    convertLengthToJSRM(userLengthUnit, starGrainComputationRequest.getPointDiameter()),
                    starGrainComputationRequest.getPointCount(),
                    starGrainComputationRequest.getNumberOfSegment(),
                    convertLengthToJSRM(userLengthUnit, starGrainComputationRequest.getSegmentLength()),
                    starGrainComputationRequest.getEndsSurface()
            );
        } else if(request instanceof EndBurnerGrainComputationRequest) {
            EndBurnerGrainComputationRequest endBurnerGrainComputationRequest = (EndBurnerGrainComputationRequest)request;
            grainConfigutation = new EndBurnerGrain(
                    convertLengthToJSRM(userLengthUnit, endBurnerGrainComputationRequest.getSegmentLength()),
                    convertLengthToJSRM(userLengthUnit, endBurnerGrainComputationRequest.getOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, endBurnerGrainComputationRequest.getHoleDiameter()),
                    convertLengthToJSRM(userLengthUnit, endBurnerGrainComputationRequest.getHoleDepth())
            );
        } else if(request instanceof MoonBurnerGrainComputationRequest) {
            MoonBurnerGrainComputationRequest moonBurnerGrainComputationRequest = (MoonBurnerGrainComputationRequest)request;
            grainConfigutation = new MoonBurnerGrain(
                    convertLengthToJSRM(userLengthUnit, moonBurnerGrainComputationRequest.getOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, moonBurnerGrainComputationRequest.getCoreDiameter()),
                    convertLengthToJSRM(userLengthUnit, moonBurnerGrainComputationRequest.getCoreOffset()),
                    moonBurnerGrainComputationRequest.getNumberOfSegment(),
                    convertLengthToJSRM(userLengthUnit, moonBurnerGrainComputationRequest.getSegmentLength()),
                    moonBurnerGrainComputationRequest.getEndsSurface()
                    );
        } else if(request instanceof CSlotGrainComputationRequest) {
            CSlotGrainComputationRequest cSlotGrainComputationRequest = (CSlotGrainComputationRequest)request;
            grainConfigutation = new CSlotGrain(
                    convertLengthToJSRM(userLengthUnit, cSlotGrainComputationRequest.getOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, cSlotGrainComputationRequest.getCoreDiameter()),
                    convertLengthToJSRM(userLengthUnit, cSlotGrainComputationRequest.getSlotWidth()),
                    convertLengthToJSRM(userLengthUnit, cSlotGrainComputationRequest.getSlotDepth()),
                    convertLengthToJSRM(userLengthUnit, cSlotGrainComputationRequest.getSlotOffset()),
                    cSlotGrainComputationRequest.getNumberOfSegment(),
                    convertLengthToJSRM(userLengthUnit, cSlotGrainComputationRequest.getSegmentLength()),
                    cSlotGrainComputationRequest.getEndsSurface()
            );
        } else if(request instanceof RodTubeGrainComputationRequest) {
            RodTubeGrainComputationRequest rodTubeGrainComputationRequest = (RodTubeGrainComputationRequest)request;
            grainConfigutation = new RodAndTubeGrain(
                    convertLengthToJSRM(userLengthUnit, rodTubeGrainComputationRequest.getRodDiameter()),
                    convertLengthToJSRM(userLengthUnit, rodTubeGrainComputationRequest.getTubeOuterDiameter()),
                    convertLengthToJSRM(userLengthUnit, rodTubeGrainComputationRequest.getTubeInnerDiameter()),
                    rodTubeGrainComputationRequest.getNumberOfSegment(),
                    convertLengthToJSRM(userLengthUnit, rodTubeGrainComputationRequest.getSegmentLength()),
                    rodTubeGrainComputationRequest.getEndsSurface()
            );
        } else {
            throw new IllegalStateException("Request inconnue : "+ request.getClass().getSimpleName());
        }

        return new PropellantGrain(getPropellant(request), grainConfigutation);
    }

    private SolidPropellant getPropellant(BasicComputationRequest request) {
        Map<String, SolidPropellant> propellants = Stream.of(PropellantType.values())
                .collect(toMap(Enum::name, Function.identity()));

        return propellants.computeIfAbsent(request.getPropellantType(), propellantType -> propellantToSIUnits(request));
    }

    private SolidPropellant propellantToSIUnits(BasicComputationRequest request) {
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

    private Set<BurnRatePressureData> convertBurnRateDataToJSRM(BasicComputationRequest request) {
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

    public double convertLengthToJSRM(Unit<Length> lenghtUnit, double length) {
        return Quantities.getQuantity(length, lenghtUnit).to(JSRM_UNITS.getLenghtUnit()).getValue().doubleValue();
    }

    private double convertPressureToJSRM(Unit<Pressure> pressureUnit, double pressure) {
        return Quantities.getQuantity(pressure, pressureUnit).to(JSRM_UNITS.getResultPressureUnit()).getValue().doubleValue();
    }

    private double convertPressureToMeteor(Unit<Pressure> pressureUnit, double pressure) {
        return Quantities.getQuantity(pressure, JSRM_UNITS.getResultPressureUnit()).to(pressureUnit).getValue().doubleValue();
    }

    private double convertMassToMeteor(Unit<Mass> massUnit, double mass) {
        return Quantities.getQuantity(mass, JSRM_UNITS.getMassUnit()).to(massUnit).getValue().doubleValue();
    }

    public double convertMass(Unit<Mass> fromMassUnit, Unit<Mass> toMassUnit, double mass) {
        return Quantities.getQuantity(mass, fromMassUnit).to(toMassUnit).getValue().doubleValue();
    }

    private double convertLengthToMeteor(Unit<Length> lengthUnit, double length) {
        return Quantities.getQuantity(length, JSRM_UNITS.getLenghtUnit()).to(lengthUnit).getValue().doubleValue();
    }
}
