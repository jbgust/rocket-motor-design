package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.controler.request.HollowComputationRequest;
import com.rocketmotordesign.controler.request.ExportRASPRequest;
import com.rocketmotordesign.service.JSRMService;
import com.rocketmotordesign.service.MeasureUnitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.utils.TestHelper.*;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ExportControler.class)
@Import({JSRMService.class, MeasureUnitService.class})
public class ExportControlerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void shoulExportToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setHollowComputationRequest(getDefaultRequest());
        exportRequest.setDelay("P");
        exportRequest.setMotorDiameter(72); //mm
        exportRequest.setMotorLength(500); //mm
        exportRequest.setMotorWeight(3.2); //kg
        exportRequest.setProjectName("motorTest");

        String request = new ObjectMapper().writeValueAsString(exportRequest);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/export/rasp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=meteor_motorTest_L1672.eng"))
                .andExpect(content().string(startsWith(
                        "L1672 72.0 500.0 P 2.812 3.200 METEOR\n" +
                        "    0.0 0.0\n"+
                        "    0.0137 580.829\n"+
                        "    0.0176 797.3091\n"+
                        "    0.0216 864.2988\n"+
                        "    0.0253 917.1436\n"+
                        "    0.029 959.4651\n"+
                        "    0.0325 993.7791\n")));
    }

    @Test
    void shoulExportToRASPFromIMPERIAL() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setHollowComputationRequest(getDefaultRequestImperial());
        exportRequest.setDelay("P");
        exportRequest.setMotorDiameter(2.83465); //inch
        exportRequest.setMotorLength(19.685); //inch
        exportRequest.setMotorWeight(7.054792); //pounds
        exportRequest.setProjectName("motorTest");

        String request = Jackson2ObjectMapperBuilder.json().build().writeValueAsString(exportRequest);

        // WHEN
        ResultActions resultActions = mvc.perform(post("/export/rasp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=meteor_motorTest_L1672.eng"))
                .andExpect(content().string(startsWith(
                        "L1672 72.0 500.0 P 2.812 3.200 METEOR\n" +
                                "    0.0 0.0\n"+
                                "    0.0137 580.829\n"+
                                "    0.0176 797.3091\n"+
                                "    0.0216 864.2988\n"+
                                "    0.0253 917.1436\n"+
                                "    0.029 959.4651\n"+
                                "    0.0325 993.7791\n")));
    }

    @Test
    void shouldRunComputationForLowKNMotor() throws Exception {
        // GIVEN
        HollowComputationRequest lowKNRequest = new HollowComputationRequest();
        lowKNRequest.setThroatDiameter(19);
        lowKNRequest.setOuterDiameter(37);
        lowKNRequest.setCoreDiameter(20);
        lowKNRequest.setSegmentLength(10);
        lowKNRequest.setNumberOfSegment(5);
        lowKNRequest.setOuterSurface(INHIBITED);
        lowKNRequest.setEndsSurface(EXPOSED);
        lowKNRequest.setCoreSurface(EXPOSED);
        lowKNRequest.setPropellantType(KNSU.name());
        lowKNRequest.setChamberInnerDiameter(38);
        lowKNRequest.setChamberLength(500);
        lowKNRequest.setExtraConfig(getDefaultExtraConfiguration());

        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setHollowComputationRequest(lowKNRequest);
        exportRequest.setDelay("P");
        exportRequest.setMotorDiameter(72); //mm
        exportRequest.setMotorLength(500); //mm
        exportRequest.setMotorWeight(3.2); //kg
        exportRequest.setProjectName("motorTest");
        exportRequest.setSafeKN(true);

        // WHEN
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        ResultActions resultActions = mvc.perform(post("/export/rasp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(exportRequest)));

        //THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=meteor_motorTest_H134.eng"))
                .andExpect(content().string(startsWith(
                        "H134 72.0 500.0 P 0.068 3.200 METEOR")));
    }
}
