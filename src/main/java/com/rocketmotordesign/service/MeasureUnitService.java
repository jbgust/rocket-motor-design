package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorParameters;
import com.rocketmotordesign.controler.dto.*;
import org.springframework.stereotype.Service;
import tec.units.ri.quantity.Quantities;

import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.measure.quantity.Pressure;

import static com.rocketmotordesign.controler.dto.MeasureUnit.JSRM_UNITS;

@Service
public class MeasureUnitService {

    public SolidRocketMotor toSolidRocketMotor(ComputationRequest request) {
        return new SolidRocketMotor(
                toPropellantGrain(request),
                toCombustionChamber(request),
                convertLengthToJSRM(request.getMeasureUnit().getLenghtUnit(), request.getThroatDiameter())
        );
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
                jsrmResult.getNozzle().getNozzleExitDiameterInMillimeter(),
                jsrmResult.getNozzle().getInitialNozzleExitSpeedInMach(),
                convertPressureToMeteor(userUnits.getPressureUnit(), jsrmResult.getAverageChamberPressureInMPa()),
                jsrmResult.getNozzle().getChamberInsideDiameterInMillimeter() - jsrmResult.getNozzle().getNozzleThroatDiameterInMillimeter(),
                jsrmResult.getNozzle().getNozzleExitDiameterInMillimeter() - jsrmResult.getNozzle().getNozzleThroatDiameterInMillimeter(),
                jsrmResult.getNozzle().getOptimalNozzleExpansionRatio()
        );
    }

    public GraphResult toGraphResult(MotorParameters motorParameters, MeasureUnit userUnits) {
        return new GraphResult(
                motorParameters.getTimeSinceBurnStartInSecond(),
                motorParameters.getThrustInNewton(),
                motorParameters.getKn(),
                convertPressureToMeteor(userUnits.getPressureUnit(), motorParameters.getChamberPressureInMPa()),
                motorParameters.getMassFlowRateInKgPerSec()
        );
    }

    private double convertLengthToJSRM(Unit<Length> lenghtUnit, double length) {
        return Quantities.getQuantity(length, lenghtUnit).to(JSRM_UNITS.getLenghtUnit()).getValue().doubleValue();
    }

    private double convertPressureToMeteor(Unit<Pressure> pressureUnit, double pressure) {
        return Quantities.getQuantity(pressure, JSRM_UNITS.getPressureUnit()).to(pressureUnit).getValue().doubleValue();
    }

    private CombustionChamber toCombustionChamber(ComputationRequest request) {
        return new CombustionChamber(request.getChamberInnerDiameter(), request.getChamberLength());
    }

    private PropellantGrain toPropellantGrain(ComputationRequest request) {
        return new PropellantGrain(request.getPropellantType(),
                request.getOuterDiameter(), request.getCoreDiameter(),
                request.getSegmentLength(),request.getNumberOfSegment(),
                request.getOuterSurface(), request.getEndsSurface(),
                request.getCoreSurface());
    }
}
