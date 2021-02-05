package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.rocketmotordesign.controler.request.BurnRatePressureData;
import com.rocketmotordesign.controler.request.CustomPropellantRequest;
import com.rocketmotordesign.controler.request.FinocylComputationRequest;
import com.rocketmotordesign.controler.request.HollowComputationRequest;
import com.rocketmotordesign.propellant.repository.MeteorPropellantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;
import java.util.UUID;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.service.MeasureUnit.IMPERIAL;
import static com.rocketmotordesign.utils.TestHelper.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser("spring")
public class ComputationControllerIT {

    @Autowired
    private MockMvc mvc;
    private ObjectMapper jsonObjectMapper = Jackson2ObjectMapperBuilder.json().build();

    @MockBean
    private MeteorPropellantRepository propellantRepository;

    @Test
    void shouldRunComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultRequest());

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
                .andExpect(jsonPath("$.performanceResult.divergenceCrossSectionDiameter", closeTo(36.6355, 0.0001)))

                .andExpect(jsonPath("$.performanceResult.portToThroatArea", is("4.18")))
                .andExpect(jsonPath("$.performanceResult.portToThroatAreaWarning", is("NORMAL")))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35")))

                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("3603.07")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("130.65")))
                .andExpect(jsonPath("$.performanceResult.maxPressure", is("59.36")))
                .andExpect(jsonPath("$.performanceResult.thrustTime", is("2.15")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("54.03")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("49.06")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("2.812")))
                .andExpect(jsonPath("$.performanceResult.classPercentage", is(41)))

                .andExpect(jsonPath("$.motorParameters", hasSize(883)))

                .andExpect(jsonPath("$.motorParameters[400].x", is(closeTo(1.0343, 0.01d))))
                .andExpect(jsonPath("$.motorParameters[400].y", is(closeTo(2058.5999, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].p", is(closeTo(59.3117, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].m", is(closeTo(1.584, 0.0001d))));
    }

    @Test
    void shouldRunFinocylComputationWithFullNumberOfPoints() throws Exception {
        // GIVEN
        FinocylComputationRequest defaultFinocylRequest = getDefaultFinocylRequest();
        defaultFinocylRequest.getExtraConfig().setNumberOfCalculationLine(883);
        String request = jsonObjectMapper.writeValueAsString(defaultFinocylRequest);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/finocyl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H214")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("392.73")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("181.36")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("126.00")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("28.28")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.147")))

                .andExpect(jsonPath("$.motorParameters", hasSize(847)));
    }

    @Test
    void shouldRunFinocylComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultFinocylRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/finocyl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(192)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H216")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("396.17")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("182.59")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("126.86")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("28.28")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.147")));
    }

    @Test
    void shouldRunStarGrainComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultStarGrainRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/star")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(192)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H197")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("366.78")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("201.53")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("124.17")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("28.28")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.166")));
    }

    @Test
    void shouldRunStarGrainComputationInImperial() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultStarGrainRequestImperial());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/star")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(192)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H197")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("366.78")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("201.53")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("124.17")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("1.1136")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.365")));
    }

    @Test
    void shouldRunMoonBurnerGrainComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultMoonBurnerGrainRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/moonburner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(835)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H100")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("204.00")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("181.87")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("116.24")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("28.28")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.160")));
    }

    @Test
    void shouldRunMoonBurnerGrainComputationImperial() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultMoonBurnerGrainRequestImperial());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/moonburner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(835)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H100")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("204.00")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("181.87")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("116.24")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("1.1136")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.352")));
    }

    @Test
    void shouldRunCSlotGrainComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultCSlotGrainRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/cslot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(835)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H74")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("180.77")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("166.58")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("114.08")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("28.28")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.149")));
    }

    @Test
    void shouldRunCSlotGrainComputationImperial() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultCSlotGrainRequestImperial());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/cslot")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(835)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H74")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("180.77")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("166.58")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("114.08")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("1.1136")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.328")));
    }

    @Test
    void shouldRunRodTubeGrainComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultRodTubeGrainRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/rodtube")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(883)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("G350")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("438.59")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("149.47")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("127.40")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("28.28")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.120")));
    }

    @Test
    void shouldRunRodTubeGrainComputationImperial() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultRodTubeGrainRequestImperial());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/rodtube")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(883)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("G350")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("438.59")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("149.47")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("127.40")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("1.1136")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.264")));
    }

    @Test
    void shouldRunEndBurnerGrainComputation() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultEndBurnerGrainRequest());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/endburner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(835)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("G9")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("33.97")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("92.36")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("106.66")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("16.97")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.088")));
    }

    @Test
    void shouldRunEndBurnerGrainComputationImperial() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultEndBurnerGrainRequestImperial());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/endburner")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(835)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("G9")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("33.97")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("92.36")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("106.66")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("0.6681")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.195")));
    }

    @Test
    void shouldRunFinocylComputationImperial() throws Exception {
        // GIVEN
        String request = jsonObjectMapper.writeValueAsString(getDefaultFinocylRequestImperial());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute/finocyl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motorParameters", hasSize(192)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("H216")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(false)))
                .andExpect(jsonPath("$.performanceResult.maxThrust", is("396.17")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("182.59")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("126.86")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("1.1136")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("0.324")));
    }

    @Test
    void shouldConvertToImperialUnits() throws Exception {
        // GIVEN
        HollowComputationRequest defaultRequest = getDefaultRequestImperial();

        String request = jsonObjectMapper.writeValueAsString(defaultRequest);

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
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("2.1270")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("711.47")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("6.200")))

                .andExpect(jsonPath("$.motorParameters", hasSize(883)))

                .andExpect(jsonPath("$.motorParameters[400].x", is(closeTo(1.0343, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].y", is(closeTo(2058.5999, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].p", is(closeTo(860.2126, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].m", is(closeTo(3.4921, 0.0001d))));
    }

    @Test
    void shouldRunComputationForLowKNMotor() throws Exception {
        // GIVEN
        HollowComputationRequest lowKNRequest = new HollowComputationRequest();
        lowKNRequest.setThroatDiameter(8);
        lowKNRequest.setOuterDiameter(28);
        lowKNRequest.setCoreDiameter(12);
        lowKNRequest.setSegmentLength(98);
        lowKNRequest.setNumberOfSegment(1);
        lowKNRequest.setOuterSurface(INHIBITED);
        lowKNRequest.setEndsSurface(EXPOSED);
        lowKNRequest.setCoreSurface(EXPOSED);
        lowKNRequest.setPropellantId(KNDX.name());
        lowKNRequest.setChamberInnerDiameter(28);
        lowKNRequest.setChamberLength(98);
        lowKNRequest.setExtraConfig(getDefaultExtraConfiguration());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(lowKNRequest)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.safeKN", is(true)))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(true)))
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("G106")));
    }

    @Test
    void shouldUseCustomPropellantInImperialUnits() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequestImperial();
        request.setPropellantId("My propellant");
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

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant, IMPERIAL)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

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
    void shouldUseCustomPropellantInSIUnits() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequest();
        request.setPropellantId("To be defined");
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setDensity(KNSU.getIdealMassDensity());
        customPropellant.setChamberTemperature(KNSU.getChamberTemperature());
        customPropellant.setK(KNSU.getK());
        customPropellant.setK2ph(KNSU.getK2Ph());
        customPropellant.setMolarMass(KNSU.getEffectiveMolecularWeight());
        customPropellant.setBurnRateCoefficient(KNSU.getBurnRateCoefficient(1));
        customPropellant.setPressureExponent(KNSU.getPressureExponent(1));

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L2174")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(true)));
    }

    @Test
    void shouldUseCustomPropellantInSIUnitsWithMultipleBurnRateData() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequest();

        CustomPropellantRequest customPropellantRequest = new CustomPropellantRequest();
        customPropellantRequest.setDensity(KNDX.getIdealMassDensity());
        customPropellantRequest.setChamberTemperature(KNDX.getChamberTemperature());
        customPropellantRequest.setK(KNDX.getK());
        customPropellantRequest.setK2ph(KNDX.getK2Ph());
        customPropellantRequest.setMolarMass(KNDX.getEffectiveMolecularWeight());
        customPropellantRequest.setBurnRateDataSet(Sets.newHashSet(
                //data taken from SRM_2014
                new BurnRatePressureData(8.87544496778536, 0.6193, 0.1, 0.779135),
                new BurnRatePressureData(7.55278442387944, -0.0087, 0.779135, 2.571835),
                new BurnRatePressureData(3.84087990499602, 0.6882, 2.571835, 5.9297),
                new BurnRatePressureData(17.2041864098062, -0.1481, 5.9297, 8.501535),
                new BurnRatePressureData(4.77524086347659, 0.4417, 8.501535, 11.20)
        ));

        // TODO : dette passer par le vrai repo (A faire dans tout les test de cette classe
        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellantRequest)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")))
                .andExpect(jsonPath("$.performanceResult.optimalDesign", is(true)))
                .andExpect(jsonPath("$.performanceResult.convergenceCrossSectionDiameter", is(57.61)))
                .andExpect(jsonPath("$.performanceResult.divergenceCrossSectionDiameter", closeTo(36.6355, 0.001d)))

                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("3603.07")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("130.65")))
                .andExpect(jsonPath("$.performanceResult.maxPressure", is("59.36")))
                .andExpect(jsonPath("$.performanceResult.thrustTime", is("2.15")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("54.03")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("49.06")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))

                .andExpect(jsonPath("$.motorParameters", hasSize(883)))

                .andExpect(jsonPath("$.motorParameters[400].x", is(closeTo(1.0343, 0.01d))))
                .andExpect(jsonPath("$.motorParameters[400].y", is(closeTo(2058.5999, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].p", is(closeTo(59.3117, 0.0001d))))
                .andExpect(jsonPath("$.motorParameters[400].m", is(closeTo(1.584, 0.0001d))));
    }

    @Test
    void shouldUseCustomPropellantInImperialUnitsWithMultipleBurnRateData() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequestImperial();
        request.setPropellantId("To be defined");


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

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant, IMPERIAL)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")));
    }

    @Test
    void shouldUseComplexeCustomPropellantInImperialWithMotorInSI() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequest();

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

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant, IMPERIAL)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performanceResult.motorDescription", is("L1672")));
    }

    @Test
    void shouldSendErrorIfBurnRateDataAreOverlaping() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequestImperial();
        request.setPropellantId("To be defined");

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

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant, IMPERIAL)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("METEOR can't run this computation due to the following error:")))
                .andExpect(jsonPath("$.detail", is(
                        "Your burn rate data are invalid. " +
                                "Overlapping ranges: range [0.10087385..0.779135) overlaps with entry [0.75845..2.571835)")));
    }

    @Test
    void shouldSendErrorWhenPressureIsOutOfBound() throws Exception {
        // GIVEN
        HollowComputationRequest request = getDefaultRequestImperial();
        request.setPropellantId("To be defined");

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

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant, IMPERIAL)));
        request.setPropellantId(customPropellantId.toString());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

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
    void shouldReturnErrorOnWrongMotorDesing() throws Exception {

        // GIVEN
        HollowComputationRequest invalidMotorDesignRequest = new HollowComputationRequest();
        invalidMotorDesignRequest.setThroatDiameter(17.39);
        invalidMotorDesignRequest.setOuterDiameter(69);
        invalidMotorDesignRequest.setCoreDiameter(20);
        invalidMotorDesignRequest.setSegmentLength(115);
        invalidMotorDesignRequest.setNumberOfSegment(4);
        invalidMotorDesignRequest.setOuterSurface(INHIBITED);
        invalidMotorDesignRequest.setEndsSurface(EXPOSED);
        invalidMotorDesignRequest.setCoreSurface(EXPOSED);
        invalidMotorDesignRequest.setPropellantId(KNDX.name());
        invalidMotorDesignRequest.setChamberInnerDiameter(75);
        invalidMotorDesignRequest.setChamberLength(47);
        invalidMotorDesignRequest.setExtraConfig(getDefaultExtraConfiguration());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(invalidMotorDesignRequest)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Combustion chamber length should be >= than Grain total length")))
                .andExpect(jsonPath("$.detail", is(emptyOrNullString())));
    }

    @Test
    void shouldReturnErrorWheCoreDiamIsLowerThanThroat() throws Exception {

        // GIVEN
        HollowComputationRequest invalidMotorDesignRequest = new HollowComputationRequest();
        invalidMotorDesignRequest.setThroatDiameter(17.39);
        invalidMotorDesignRequest.setOuterDiameter(69);
        invalidMotorDesignRequest.setCoreDiameter(15);
        invalidMotorDesignRequest.setSegmentLength(115);
        invalidMotorDesignRequest.setNumberOfSegment(4);
        invalidMotorDesignRequest.setOuterSurface(INHIBITED);
        invalidMotorDesignRequest.setEndsSurface(EXPOSED);
        invalidMotorDesignRequest.setCoreSurface(EXPOSED);
        invalidMotorDesignRequest.setPropellantId(KNDX.name());
        invalidMotorDesignRequest.setChamberInnerDiameter(75);
        invalidMotorDesignRequest.setChamberLength(475);
        invalidMotorDesignRequest.setExtraConfig(getDefaultExtraConfiguration());

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(invalidMotorDesignRequest)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Throat diameter should be <= than grain core diameter")))
                .andExpect(jsonPath("$.detail", is(emptyOrNullString())));
    }

    @Test
    void shouldReturnErrorWhenComputationFailed() throws Exception {

        // GIVEN
        HollowComputationRequest request = new HollowComputationRequest();
        request.setThroatDiameter(100);
        request.setOuterDiameter(300);
        request.setCoreDiameter(150);
        request.setSegmentLength(1000);
        request.setNumberOfSegment(15);
        request.setOuterSurface(INHIBITED);
        request.setEndsSurface(INHIBITED);
        request.setCoreSurface(EXPOSED);
        request.setPropellantId(KNSU.name());
        request.setChamberInnerDiameter(400);
        request.setChamberLength(20000);
        request.setExtraConfig(getDefaultExtraConfiguration());

        //trick pour avoir un moteur encore plus puissant
        request.setMeasureUnit(IMPERIAL);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/compute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonObjectMapper.writeValueAsString(request)));

        //THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("METEOR can't run this computation due to the following error:")))
                .andExpect(jsonPath("$.detail", is("The total impulse of this motor is not in [A;V] classes")));
    }

}
