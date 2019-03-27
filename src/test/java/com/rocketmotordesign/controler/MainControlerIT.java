package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ExtraConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSB_FINE;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MainControler.class)
public class MainControlerIT {

    @Autowired
    private MockMvc mvc;
    private ComputationRequest computationRequest;

    @Before
    public void setUp() throws Exception {
        computationRequest = new ComputationRequest();
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
    }

    @Test
    public void shouldRunComputation() throws Exception {
        // GIVEN
        String request = new ObjectMapper().writeValueAsString(computationRequest);

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

                .andExpect(jsonPath("$.performanceResult.maxThrust", is("2060.35 N")))
                .andExpect(jsonPath("$.performanceResult.totalImpulse", is("3603.07 N.S")))
                .andExpect(jsonPath("$.performanceResult.specificImpulse", is("130.65 s")))
                .andExpect(jsonPath("$.performanceResult.maxPressure", is("59.36 Bar")))
                .andExpect(jsonPath("$.performanceResult.thrustTime", is("2.15 s")))
                .andExpect(jsonPath("$.performanceResult.nozzleExitDiameter", is("54.03 mm")))
                .andExpect(jsonPath("$.performanceResult.exitSpeedInitial", is("3.07 mach")))
                .andExpect(jsonPath("$.performanceResult.averagePressure", is("49.06 Bar")))
                .andExpect(jsonPath("$.performanceResult.optimalNozzleExpansionRatio", is("9.65")))

                .andExpect(jsonPath("$.thrustResults", hasSize(89)));
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
        invalidMotorDesignRequest.setPropellantType(KNDX);
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
        request.setPropellantType(KNSB_FINE);
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

    private ExtraConfiguration getDefaultExtraConfiguration() {
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

}