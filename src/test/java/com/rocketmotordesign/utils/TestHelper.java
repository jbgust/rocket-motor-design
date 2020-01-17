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
        computationRequest.setEndsSurface(EXPOSED);
        basicMotorSimConfig(computationRequest);

        return computationRequest;
    }

    public static StarGrainComputationRequest getDefaultStarGrainRequest() {
        StarGrainComputationRequest computationRequest = new StarGrainComputationRequest();
        computationRequest.setOuterDiameter(30d);
        computationRequest.setInnerDiameter(5d);
        computationRequest.setPointDiameter(15d);
        computationRequest.setPointCount(5);
        computationRequest.setEndsSurface(EXPOSED);

        basicMotorSimConfig(computationRequest);

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

        basicMotorSimConfigImperial(computationRequest);

        return computationRequest;
    }

    public static MoonBurnerGrainComputationRequest getDefaultMoonBurnerGrainRequest() {
        MoonBurnerGrainComputationRequest computationRequest = new MoonBurnerGrainComputationRequest();
        computationRequest.setOuterDiameter(30d);
        computationRequest.setCoreDiameter(10d);
        computationRequest.setCoreOffset(5d);
        computationRequest.setEndsSurface(EXPOSED);

        basicMotorSimConfig(computationRequest);

        return computationRequest;
    }

    public static MoonBurnerGrainComputationRequest getDefaultMoonBurnerGrainRequestImperial() {
        MoonBurnerGrainComputationRequest computationRequest = new MoonBurnerGrainComputationRequest();
        computationRequest.setOuterDiameter(30d/25.4);
        computationRequest.setCoreDiameter(10d/25.4);
        computationRequest.setCoreOffset(5d/25.4);

        basicMotorSimConfigImperial(computationRequest);

        return computationRequest;
    }

    public static CSlotGrainComputationRequest getDefaultCSlotGrainRequest() {
        CSlotGrainComputationRequest computationRequest = new CSlotGrainComputationRequest();
        computationRequest.setOuterDiameter(30d);
        computationRequest.setCoreDiameter(10d);
        computationRequest.setSlotWidth(5d);
        computationRequest.setSlotDepth(15d);
        computationRequest.setSlotOffset(7d);
        computationRequest.setEndsSurface(EXPOSED);

        basicMotorSimConfig(computationRequest);

        return computationRequest;
    }

    public static CSlotGrainComputationRequest getDefaultCSlotGrainRequestImperial() {
        CSlotGrainComputationRequest computationRequest = new CSlotGrainComputationRequest();
        computationRequest.setOuterDiameter(30d/25.4);
        computationRequest.setCoreDiameter(10d/25.4);
        computationRequest.setSlotWidth(5d/25.4);
        computationRequest.setSlotDepth(15d/25.4);
        computationRequest.setSlotOffset(7d/25.4);
        computationRequest.setEndsSurface(EXPOSED);

        basicMotorSimConfigImperial(computationRequest);

        return computationRequest;
    }

    public static RodTubeGrainComputationRequest getDefaultRodTubeGrainRequest() {
        RodTubeGrainComputationRequest computationRequest = new RodTubeGrainComputationRequest();
        computationRequest.setRodDiameter(10d);
        computationRequest.setTubeOuterDiameter(30d);
        computationRequest.setTubeInnerDiameter(20d);
        computationRequest.setEndsSurface(EXPOSED);

        basicMotorSimConfig(computationRequest);

        return computationRequest;
    }

    public static RodTubeGrainComputationRequest getDefaultRodTubeGrainRequestImperial() {
        RodTubeGrainComputationRequest computationRequest = new RodTubeGrainComputationRequest();
        computationRequest.setRodDiameter(10d/25.4);
        computationRequest.setTubeOuterDiameter(30d/25.4);
        computationRequest.setTubeInnerDiameter(20d/25.4);
        computationRequest.setEndsSurface(EXPOSED);

        basicMotorSimConfigImperial(computationRequest);

        return computationRequest;
    }

    private static void basicMotorSimConfigImperial(BasicComputationRequest computationRequest) {
        //BasicComputationRequest
        computationRequest.setThroatDiameter(10d/25.4);
        computationRequest.setSegmentLength(70d/25.4);
        computationRequest.setNumberOfSegment(2);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d/25.4);
        computationRequest.setChamberLength(150d/25.4);
        computationRequest.setMeasureUnit(IMPERIAL);

        computationRequest.setExtraConfig(configMotorSim());
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

        basicMotorSimConfigImperial(computationRequest);

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

    private static void basicMotorSimConfig(BasicComputationRequest computationRequest) {
        //BasicComputationRequest
        computationRequest.setThroatDiameter(10d);
        computationRequest.setSegmentLength(70d);
        computationRequest.setNumberOfSegment(2);
        computationRequest.setPropellantType(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d);
        computationRequest.setChamberLength(150d);
        computationRequest.setMeasureUnit(SI);

        computationRequest.setExtraConfig(configMotorSim());
    }
}
