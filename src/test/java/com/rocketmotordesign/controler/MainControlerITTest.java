package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.rocketmotordesign.controler.dto.BurnRatePressureData;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.CustomPropellantRequest;
import com.rocketmotordesign.controler.dto.MeasureUnit;
import com.rocketmotordesign.service.JSRMService;
import com.rocketmotordesign.service.MeasureUnitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSB_FINE;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.utils.TestHelper.*;
import static java.util.Collections.emptySet;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.Set;

@RunWith(SpringRunner.class)
@WebMvcTest(MainControler.class)
@Import({JSRMService.class, MeasureUnitService.class})
public class MainControlerITTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void shouldRunComputation() throws Exception {
        // GIVEN
        String request = new ObjectMapper().writeValueAsString(getDefaultRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(true)))
                .andExpect(jsonPath("$.performanceResult.convergenceCrossSectionDiameter", is(57.61)))
                .andExpect(jsonPath("$.performanceResult.divergenceCrossSectionDiameter", is(36.63558888655025)))

                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("3603.07")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("130.65")))
                .andExpect(jsonPath("$.performanceResult.maxPressure", is("59.36")))
                .andExpect(jsonPath("$.performanceResult.thrustTime", is("2.15")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("54.03")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("49.06")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))

                .andExpect(jsonPath("$.motorParameters", hasSize(883)))

                .andExpect(jsonPath("$.motorParameters[400].x", is(closeTo(1.0343, 0.01d))))
                .andExpect(jsonPath("$.motorParameters[400].y", is(closeTo(2058.5999, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].p", is(closeTo(59.3117, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].m", is(closeTo(1.584, 0.0001d))));
    }

    @Test
    public void shouldConvertToImperialUnits() throws Exception {
        // GIVEN
        ComputationRequest defaultRequest = getDefaultRequestImperial();

        String request = new ObjectMapper().writeValueAsString(defaultRequest);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(true)))
                .andExpect(jsonPath("$.performanceResult.convergenceCrossSectionDiameter", is(closeTo(2.2681, 0.001d))))
                .andExpect(jsonPath("$.performanceResult.divergenceCrossSectionDiameter", is(closeTo(1.4423, 0.001d))))

                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("3603.07")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("130.65")))
                .andExpect(jsonPath("$.performanceResult.maxPressure", is("860.88")))
                .andExpect(jsonPath("$.performanceResult.thrustTime", is("2.15")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("2.13")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("711.47")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))

                .andExpect(jsonPath("$.motorParameters", hasSize(883)))

                .andExpect(jsonPath("$.motorParameters[400].x", is(closeTo(1.0343, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].y", is(closeTo(2058.5999, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].p", is(closeTo(860.2126, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].m", is(closeTo(3.4921, 0.0001d))));
    }

    @Test
    public void shouldUseCustomPropellantInImperialUnits() throws Exception {
        // GIVEN
        ComputationRequest request = getDefaultRequestImperial();
        //TODO voir la valeur ci-dessous
        request.setPropellantType("To be defined");
        request.getExtraConfig().setNozzleExpansionRatio(8.0);
        request.getExtraConfig().setNozzleEfficiency(0.85);
        request.getExtraConfig().setOptimalNozzleDesign(false);
        request.getExtraConfig().setCombustionEfficiencyRatio(1);
        request.getExtraConfig().setDensityRatio(1);

        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setCstar(5468.4);
        customPropellant.setBurnRateCoefficient(0.0174);
        customPropellant.setPressureExponent(0.4285);
        customPropellant.setDensity(0.06);
        customPropellant.setK(1.2768);
        customPropellant.setMolarMass(45.0);
        request.setCustomPropellant(customPropellant);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("M1907")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2142.54")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("5850.73")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("228.01")));
    }

    @Test
    public void shouldUseCustomPropellantInSIUnits() throws Exception {
        // GIVEN
        ComputationRequest request = getDefaultRequest();
        request.setPropellantType("To be defined");
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setDensity(KNSU.getIdealMassDensity());
        customPropellant.setChamberTemperature(KNSU.getChamberTemperature());
        customPropellant.setK(KNSU.getK());
        customPropellant.setK2ph(KNSU.getK2Ph());
        customPropellant.setMolarMass(KNSU.getEffectiveMolecularWeight());
        customPropellant.setBurnRateCoefficient(KNSU.getBurnRateCoefficient(1));
        customPropellant.setPressureExponent(KNSU.getPressureExponent(1));

        request.setCustomPropellant(customPropellant);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L2174")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(true)));
    }

    @Test
    public void shouldUseCustomPropellantInSIUnitsWithMultipleBurnRateData() throws Exception {
        // GIVEN
        ComputationRequest request = getDefaultRequest();
        request.setPropellantType("To be defined");
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setDensity(KNDX.getIdealMassDensity());
        customPropellant.setChamberTemperature(KNDX.getChamberTemperature());
        customPropellant.setK(KNDX.getK());
        customPropellant.setK2ph(KNDX.getK2Ph());
        customPropellant.setMolarMass(KNDX.getEffectiveMolecularWeight());
        customPropellant.setBurnRateDataSet(Sets.newHashSet(
                //data taken from SRM_2014
                new BurnRatePressureData(8.87544496778536, 0.6193, toBar(0.1), toBar(0.779135)),
                new BurnRatePressureData(7.55278442387944, -0.0087, toBar(0.779135), toBar(2.571835)),
                new BurnRatePressureData(3.84087990499602, 0.6882, toBar(2.571835), toBar(5.9297)),
                new BurnRatePressureData(17.2041864098062, -0.1481, toBar(5.9297), toBar(8.501535)),
                new BurnRatePressureData(4.77524086347659, 0.4417, toBar(8.501535), toBar(11.20))
        ));

        request.setCustomPropellant(customPropellant);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(true)))
                .andExpect(jsonPath("$.performanceResult.convergenceCrossSectionDiameter", is(57.61)))
                .andExpect(jsonPath("$.performanceResult.divergenceCrossSectionDiameter", is(36.63558888655025)))

                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("3603.07")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("130.65")))
                .andExpect(jsonPath("$.performanceResult.maxPressure", is("59.36")))
                .andExpect(jsonPath("$.performanceResult.thrustTime", is("2.15")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("54.03")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("49.06")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))

                .andExpect(jsonPath("$.motorParameters", hasSize(883)))

                .andExpect(jsonPath("$.motorParameters[400].x", is(closeTo(1.0343, 0.01d))))
                .andExpect(jsonPath("$.motorParameters[400].y", is(closeTo(2058.5999, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].p", is(closeTo(59.3117, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].m", is(closeTo(1.584, 0.0001d))));
    }

    @Test
    public void shouldUseCustomPropellantInImperialUnitsWithMultipleBurnRateData() throws Exception {
        // GIVEN
        ComputationRequest request = getDefaultRequestImperial();
        request.setPropellantType("To be defined");

        // TODO mettre les valeur de KNDX au format IMPERIAL (densité, ...)
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setBurnRateDataSet(Sets.newHashSet(
                //data taken from SRM_2014
                new BurnRatePressureData(0.0160236, 0.6193000, 14.63, 113),
                new BurnRatePressureData(0.3105118, -0.0087000, 113, 373),
                new BurnRatePressureData(0.0049213, 0.6882000, 373, 860),
                new BurnRatePressureData(1.4155118, -0.1481000, 860, 1233),
                new BurnRatePressureData(0.0208661, 0.4417000, 1233, 1625)
        ));
        customPropellant.setDensity(KNDX.getIdealMassDensity()/453.6*Math.pow(2.54, 3));
        customPropellant.setChamberTemperature(KNDX.getChamberTemperature());
        customPropellant.setK(KNDX.getK());
        customPropellant.setK2ph(KNDX.getK2Ph());
        customPropellant.setMolarMass(KNDX.getEffectiveMolecularWeight());


        request.setCustomPropellant(customPropellant);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")));
    }

    @Test
    public void shouldSendErrorIfBurnRateDataAreOverlaping() throws Exception {
        // GIVEN
        ComputationRequest request = getDefaultRequestImperial();
        request.setPropellantType("To be defined");

        // TODO mettre les valeur de KNDX au format IMPERIAL (densité, ...)
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setBurnRateDataSet(Sets.newHashSet(
                //data taken from SRM_2014
                new BurnRatePressureData(0.0160236, 0.6193000, 14.63, 113),
                new BurnRatePressureData(0.3105118, -0.0087000, 110, 373)
        ));
        customPropellant.setDensity(KNDX.getIdealMassDensity()/453.6*Math.pow(2.54, 3));
        customPropellant.setChamberTemperature(KNDX.getChamberTemperature());
        customPropellant.setK(KNDX.getK());
        customPropellant.setK2ph(KNDX.getK2Ph());
        customPropellant.setMolarMass(KNDX.getEffectiveMolecularWeight());


        request.setCustomPropellant(customPropellant);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("METEOR can't run this computation due to the following error:")))
                .andExpect(jsonPath("$.detail", is(
                        "Your burn rate data are invalid. " +
                                "Overlapping ranges: range [0.10087385..0.779135) overlaps with entry [0.75845..2.571835)")));
    }

    @Test
    public void shouldSendErrorWhenPressureIsOutOfBound() throws Exception {
        // GIVEN
        ComputationRequest request = getDefaultRequestImperial();
        request.setPropellantType("To be defined");

        // TODO mettre les valeur de KNDX au format IMPERIAL (densité, ...)
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setBurnRateDataSet(Sets.newHashSet(
                new BurnRatePressureData(0.0160236, 0.6193000, 15, 113),
                new BurnRatePressureData(0.3105118, -0.0087000, 113, 373)
        ));
        customPropellant.setDensity(KNDX.getIdealMassDensity()/453.6*Math.pow(2.54, 3));
        customPropellant.setChamberTemperature(KNDX.getChamberTemperature());
        customPropellant.setK(KNDX.getK());
        customPropellant.setK2ph(KNDX.getK2Ph());
        customPropellant.setMolarMass(KNDX.getEffectiveMolecularWeight());


        request.setCustomPropellant(customPropellant);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("METEOR can't run this computation due to the following error:")))
                .andExpect(jsonPath("$.detail", is(
                        "Your custom propellant has no burn rate coefficient for this pressure (0.101 MPa). " +
                                "The pressure should be in the range you defined [0.103425..2.571835)")));
    }

    private double toBar(double pressure) {
        return 10*pressure;
    }

    @Test
    public void shouldReturnErrorOnWrongMotorDesing() throws Exception {

        // GIVEN
        ComputationRequest invalidMotorDesignRequest = new ComputationRequest();
        invalidMotorDesignRequest.setThroatDiameter(17.39);
        invalidMotorDesignRequest.setOuterDiameter(69);
        invalidMotorDesignRequest.setCoreDiameter(20);
        invalidMotorDesignRequest.setSegmentLength(115);
        invalidMotorDesignRequest.setNumberOfSegment(4);
        invalidMotorDesignRequest.setOuterSurface(INHIBITED);
        invalidMotorDesignRequest.setEndsSurface(EXPOSED);
        invalidMotorDesignRequest.setCoreSurface(EXPOSED);
        invalidMotorDesignRequest.setPropellantType(KNDX.name());
        invalidMotorDesignRequest.setChamberInnerDiameter(75);
        invalidMotorDesignRequest.setChamberLength(47);
        invalidMotorDesignRequest.setExtraConfig(getDefaultExtraConfiguration());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(invalidMotorDesignRequest)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Combustion chamber length should be >= than Grain total length")))
                .andExpect(jsonPath("$.detail", isEmptyOrNullString()));
    }

    @Test
    public void shouldReturnErrorWhenComputationFailed() throws Exception {

        // GIVEN
        ComputationRequest request = new ComputationRequest();
        request.setThroatDiameter(6);
        request.setOuterDiameter(21.2);
        request.setCoreDiameter(8);
        request.setSegmentLength(60);
        request.setNumberOfSegment(1);
        request.setOuterSurface(INHIBITED);
        request.setEndsSurface(INHIBITED);
        request.setCoreSurface(EXPOSED);
        request.setPropellantType(KNSB_FINE.name());
        request.setChamberInnerDiameter(21.2);
        request.setChamberLength(60);
        request.setExtraConfig(getDefaultExtraConfiguration());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("METEOR can't run this computation due to the following error:")))
                .andExpect(jsonPath("$.detail", startsWith("Failed to compute PROPELLANT_BURN_RATE in line 3:\nformula :")));
    }

}