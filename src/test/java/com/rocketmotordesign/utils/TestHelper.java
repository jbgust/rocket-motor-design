package com.rocketmotordesign.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.controler.request.*;
import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import com.rocketmotordesign.service.MeasureUnit;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.Random;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.service.MeasureUnit.IMPERIAL;
import static com.rocketmotordesign.service.MeasureUnit.SI;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TestHelper {

    private static ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();

    public static MeteorPropellant customPropellantToMeteorPropellant(CustomPropellantRequest customPropellant) {

        return customPropellantToMeteorPropellant(customPropellant, SI);
    }

    public static MeteorPropellant customPropellantToMeteorPropellant(CustomPropellantRequest customPropellant, MeasureUnit unit) {

        int i = new Random().nextInt();
        try {
            return new MeteorPropellant("propellant-"+ i, "description-"+i, ""+objectMapper.writeValueAsString(customPropellant), unit);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

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
        hollowComputationRequest.setPropellantId(KNDX.name());
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
        computationRequest.setPropellantId(KNSU.name());
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
        computationRequest.setPropellantId(KNSU.name());
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
        computationRequest.setPropellantId(KNSU.name());
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
        extraConfig.setNozzleErosionInMillimeter(0);
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
        hollowComputationRequest.setPropellantId(KNDX.name());
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
        extraConfig.setNozzleErosionInMillimeter(0);
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
        extraConfig.setNozzleErosionInMillimeter(0);
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
        computationRequest.setPropellantId(KNSU.name());
        computationRequest.setChamberInnerDiameter(40d);
        computationRequest.setChamberLength(150d);
        computationRequest.setMeasureUnit(SI);

        computationRequest.setExtraConfig(configMotorSim());
    }

    public static String buildExpectedMail(String title, String message, String baseURl, String url) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <style>\n" +
                "        .header {\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "        }\n" +
                "\n" +
                "        .bloc {\n" +
                "            margin: 20px 0;\n" +
                "        }\n" +
                "\n" +
                "        .content {\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "            justify-content: space-between;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body style=\"padding:25px;font-family: ubuntu, Arial, Helvetica, sans-serif;\">\n" +
                "<div class=\"header bloc\">\n" +
                "    <a href=\"" + baseURl + "\"><img src='cid:logoMeteorImg' /></a>\n" +
                "    <h1><a href=\"" + baseURl + "\" style=\"color: black; text-decoration: none;\">" + title + "</a></h1>\n" +
                "</div>\n" +
                "<div class=\"content\">\n" +
                "    <div class=\"bloc\">\n" +
                "    <p>\n" +
                "        <b>" + message + "</b>\n" +
                "    </p>\n" +
                "    <a href=\"" + url + "\">" + url + "</a>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"bloc\">\n" +
                "    <div>\n" +
                "        <b>Jérôme</b>\n" +
                "    </div>\n" +
                "    <hr />\n" +
                "    <p>Any question? Visit to the\n" +
                "\n" +
                "\n" +
                "        <a href=\"https://meteor.boards.net/\">forum</a>\n" +
                "    </p>\n" +
                "    <p>Feel free to make a donation to support this totally free application on\n" +
                "\n" +
                "\n" +
                "        <a href=\"https://pages.donately.com/meteor/campaign/meteor/donate\">donately</a>\n" +
                "    </p>\n" +
                "    <div>\n" +
                "        <p style=\"font-size: 9px\">To unsubscribe send a mail to : <b>meteor@open-sky.fr</b></p>\n" +
                "    </div>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
    }

    public static String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
