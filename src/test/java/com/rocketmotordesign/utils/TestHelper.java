package com.rocketmotordesign.utils;

import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ExtraConfiguration;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.rocketmotordesign.controler.dto.MeasureUnit.IMPERIAL;
import static com.rocketmotordesign.controler.dto.MeasureUnit.SI;

public class TestHelper {

    public static ComputationRequest getDefaultRequest() {
        ComputationRequest computationRequest = new ComputationRequest();
        computationRequest.setThroatDiameter(17.39);
        computationRequest.setOuterDiameter(69);
        computationRequest.setCoreDiameter(20);
        computationRequest.setSegmentLength(115);
        computationRequest.setNumberOfSegment(4);
        computationRequest.setOuterSurface(INHIBITED);
        computationRequest.setEndsSurface(EXPOSED);
        computationRequest.setCoreSurface(EXPOSED);
        computationRequest.setPropellantType(KNDX);
        computationRequest.setChamberInnerDiameter(75);
        computationRequest.setChamberLength(470);
        computationRequest.setExtraConfig(getDefaultExtraConfiguration());
        computationRequest.setMeasureUnit(SI);

        return computationRequest;
    }

    public static ComputationRequest getDefaultRequestImperial() {
        ComputationRequest computationRequest = new ComputationRequest();
        computationRequest.setThroatDiameter(17.39/25.4);
        computationRequest.setOuterDiameter(69/25.4);
        computationRequest.setCoreDiameter(20/25.4);
        computationRequest.setSegmentLength(115/25.4);
        computationRequest.setNumberOfSegment(4);
        computationRequest.setOuterSurface(INHIBITED);
        computationRequest.setEndsSurface(EXPOSED);
        computationRequest.setCoreSurface(EXPOSED);
        computationRequest.setPropellantType(KNDX);
        computationRequest.setChamberInnerDiameter(75/25.4);
        computationRequest.setChamberLength(470/25.4);
        computationRequest.setExtraConfig(getDefaultImperialExtraConfiguration());
        computationRequest.setMeasureUnit(IMPERIAL);

        return computationRequest;
    }

    public static ExtraConfiguration getDefaultExtraConfiguration() {
        ExtraConfiguration extraConfig = new ExtraConfiguration();
        extraConfig.setDensityRatio(0.95);
        extraConfig.setAmbiantPressure(0.101);
        extraConfig.setCombustionEfficiencyRatio(0.95);
        extraConfig.setErosiveBurningAreaRatioThreshold(6.0);
        extraConfig.setNozzleEfficiency(0.85);
        extraConfig.setNozzleErosion(0);
        extraConfig.setErosiveBurningVelocityCoefficient(0);
        extraConfig.setNozzleExpansionRatio(null);
        extraConfig.setOptimalNozzleDesign(true);
        return extraConfig;
    }

    public static ExtraConfiguration getDefaultImperialExtraConfiguration() {
        double ratioPsi = 1000000 / 6895;
        ExtraConfiguration extraConfig = new ExtraConfiguration();
        extraConfig.setDensityRatio(0.95);
        extraConfig.setAmbiantPressure(0.101 * ratioPsi);
        extraConfig.setCombustionEfficiencyRatio(0.95);
        extraConfig.setErosiveBurningAreaRatioThreshold(6.0);
        extraConfig.setNozzleEfficiency(0.85);
        extraConfig.setNozzleErosion(0);
        extraConfig.setErosiveBurningVelocityCoefficient(0);
        extraConfig.setNozzleExpansionRatio(null);
        extraConfig.setOptimalNozzleDesign(true);
        return extraConfig;
    }
}
