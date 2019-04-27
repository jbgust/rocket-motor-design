package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.controler.dto.ComputationRequest;
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
import static com.rocketmotordesign.utils.TestHelper.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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