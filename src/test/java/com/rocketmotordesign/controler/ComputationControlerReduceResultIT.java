package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.service.JSRMService;
import com.rocketmotordesign.service.MeasureUnitService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.rocketmotordesign.utils.TestHelper.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ComputationControler.class)
@Import({JSRMService.class, MeasureUnitService.class})
@TestPropertySource(properties = "computation.response.limit.size=4")
public class ComputationControlerReduceResultIT {

    @Autowired
    private MockMvc mvc;
    private ObjectMapper objectMapper;

    @Test
    public void shoulReduceResultSize() throws Exception {
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
                .andExpect(jsonPath("$.performanceResult.lowKNCorrection", is(false)))
                .andExpect(jsonPath("$.performanceResult.grainMass", is("2.812")))

                .andExpect(jsonPath("$.motorParameters", hasSize(221)));
    }


}
