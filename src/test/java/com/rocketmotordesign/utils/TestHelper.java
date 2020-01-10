package com.rocketmotordesign.utils;

import com.rocketmotordesign.controler.request.*;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.service.MeasureUnit.IMPERIAL;
import static com.rocketmotordesign.service.MeasureUnit.SI;

public class TestHelper {

    public static HollowComputationRequest getDefaultRequest() {
        HollowComputationRequest hollowComputationRequest = new HollowComputationRequest();
        hollowComputationRequest.setThroatDiameter(17.39);
        hollowComputationRequest.setOuterDiameter(69);
        hollowComputationRequest.setCoreDiameter(20);
        hollowComputationRequest.setSegmentLength(115);
        hollowComputationRequest.setNumberOfSegment(4);
        hollowComputationRequest.setOuterSurface(INHIBITED);
        hollowComputationRequest.setEndsSurface(EXPOSED);
        hollowComputationRequest.setCoreSurface(EXPOSED);
        hollowComputationRequest.setPropellantType(KNDX.name());
        hollowComputationRequest.setChamberInnerDiameter(75);
        hollowComputationRequest.setChamberLength(470);
        hollowComputationRequest.setExtraConfig(getDefaultExtraConfiguration());
        hollowComputationRequest.setMeasureUnit(SI);

        return hollowComputationRequest;
    }

    public static FinocylComputationRequest getDefaultFinocylRequest() {
        FinocylComputationRequest computationRequest = new FinocylComputationRequest();
        computationRequest.setOuterDiameter(30d);
        computationRequest.setInnerDiameter(10d);
        computationRequest.setFinWidth(2d);
        computationRequest.setFinDiameter(20d);
        computationRequest.setFinCount(5);

        //BasicComputationRequest
        computationRequest.setThroatDiameter(10d);
        computationRequest.setSegmentLength(70d);
        computationRequest.setNumberOfSegment(2);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d);
        computationRequest.setChamberLength(150d);
        computationRequest.setMeasureUnit(SI);
        computationRequest.setEndSurface(EXPOSED);

        computationRequest.setExtraConfig(configMotorSim());

        return computationRequest;
    }

    public static StarGrainComputationRequest getDefaultStarGrainRequest() {
        StarGrainComputationRequest computationRequest = new StarGrainComputationRequest();
        computationRequest.setOuterDiameter(30d);
        computationRequest.setInnerDiameter(5d);
        computationRequest.setPointDiameter(15d);
        computationRequest.setPointCount(5);

        //BasicComputationRequest
        computationRequest.setThroatDiameter(10d);
        computationRequest.setSegmentLength(70d);
        computationRequest.setNumberOfSegment(2);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d);
        computationRequest.setChamberLength(150d);
        computationRequest.setMeasureUnit(SI);
        computationRequest.setEndSurface(EXPOSED);

        computationRequest.setExtraConfig(configMotorSim());

        return computationRequest;
    }

    public static EndBurnerGrainComputationRequest getDefaultEndBurnerGrainRequest() {
        EndBurnerGrainComputationRequest computationRequest = new EndBurnerGrainComputationRequest();
        computationRequest.setOuterDiameter(30d);
        computationRequest.setHoleDiameter(10d);
        computationRequest.setHoleDepth(10d);

        //BasicComputationRequest
        computationRequest.setThroatDiameter(6d);
        computationRequest.setSegmentLength(70d);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d);
        computationRequest.setChamberLength(75d);
        computationRequest.setMeasureUnit(SI);

        computationRequest.setExtraConfig(configMotorSim());

        return computationRequest;
    }

    public static StarGrainComputationRequest getDefaultStarGrainRequestImperial() {
        StarGrainComputationRequest computationRequest = new StarGrainComputationRequest();
        computationRequest.setOuterDiameter(1.1811023622);
        computationRequest.setInnerDiameter(0.1968503937);
        computationRequest.setPointDiameter(0.5905511811);
        computationRequest.setPointCount(5);

        //BasicComputationRequest
        computationRequest.setThroatDiameter(10d/25.4);
        computationRequest.setSegmentLength(70d/25.4);
        computationRequest.setNumberOfSegment(2);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d/25.4);
        computationRequest.setChamberLength(150d/25.4);
        computationRequest.setMeasureUnit(IMPERIAL);

        computationRequest.setExtraConfig(configMotorSim());

        return computationRequest;
    }

    public static EndBurnerGrainComputationRequest getDefaultEndBurnerGrainRequestImperial() {
        EndBurnerGrainComputationRequest computationRequest = new EndBurnerGrainComputationRequest();
        computationRequest.setOuterDiameter(30d/25.4);
        computationRequest.setHoleDiameter(10d/25.4);
        computationRequest.setHoleDepth(10d/25.4);

        //BasicComputationRequest
        computationRequest.setThroatDiameter(6d/25.4);
        computationRequest.setSegmentLength(70d/25.4);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d/25.4);
        computationRequest.setChamberLength(75d/25.4);
        computationRequest.setMeasureUnit(IMPERIAL);

        computationRequest.setExtraConfig(configMotorSim());

        return computationRequest;
    }

    private static ExtraConfiguration configMotorSim() {
        ExtraConfiguration extraConfig = new ExtraConfiguration();
        extraConfig.setDensityRatio(0.96);
        extraConfig.setAmbiantPressureInMPa(0.101);
        extraConfig.setCombustionEfficiencyRatio(0.97);
        extraConfig.setErosiveBurningAreaRatioThreshold(6.0);
        extraConfig.setNozzleEfficiency(0.85);
        extraConfig.setNozzleErosion(0);
        extraConfig.setErosiveBurningVelocityCoefficient(0);
        extraConfig.setNozzleExpansionRatio(8d);
        extraConfig.setOptimalNozzleDesign(false);
        return extraConfig;
    }

    public static FinocylComputationRequest getDefaultFinocylRequestImperial() {
        FinocylComputationRequest computationRequest = new FinocylComputationRequest();
        computationRequest.setOuterDiameter(30d/25.4);
        computationRequest.setInnerDiameter(10d/25.4);
        computationRequest.setFinWidth(2d/25.4);
        computationRequest.setFinDiameter(20d/25.4);
        computationRequest.setFinCount(5);

        //BasicComputationRequest
        computationRequest.setThroatDiameter(10d/25.4);
        computationRequest.setSegmentLength(70d/25.4);
        computationRequest.setNumberOfSegment(2);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d/25.4);
        computationRequest.setChamberLength(150d/25.4);
        computationRequest.setMeasureUnit(IMPERIAL);

        ExtraConfiguration extraConfig = configMotorSim();

        computationRequest.setExtraConfig(extraConfig);

        return computationRequest;
    }

    public static HollowComputationRequest getDefaultRequestImperial() {
        HollowComputationRequest hollowComputationRequest = new HollowComputationRequest();
        hollowComputationRequest.setThroatDiameter(17.39/25.4);
        hollowComputationRequest.setOuterDiameter(69/25.4);
        hollowComputationRequest.setCoreDiameter(20/25.4);
        hollowComputationRequest.setSegmentLength(115/25.4);
        hollowComputationRequest.setNumberOfSegment(4);
        hollowComputationRequest.setOuterSurface(INHIBITED);
        hollowComputationRequest.setEndsSurface(EXPOSED);
        hollowComputationRequest.setCoreSurface(EXPOSED);
        hollowComputationRequest.setPropellantType(KNDX.name());
        hollowComputationRequest.setChamberInnerDiameter(75/25.4);
        hollowComputationRequest.setChamberLength(470/25.4);
        hollowComputationRequest.setExtraConfig(getDefaultImperialExtraConfiguration());
        hollowComputationRequest.setMeasureUnit(IMPERIAL);

        return hollowComputationRequest;
    }

    public static ExtraConfiguration getDefaultExtraConfiguration() {
        ExtraConfiguration extraConfig = new ExtraConfiguration();
        extraConfig.setDensityRatio(0.95);
        extraConfig.setAmbiantPressureInMPa(0.101);
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
        ExtraConfiguration extraConfig = new ExtraConfiguration();
        extraConfig.setDensityRatio(0.95);
        extraConfig.setAmbiantPressureInMPa(0.101);
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
