package com.rocketmotordesign.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rocketmotordesign.controler.request.ExportRASPRequest;
import com.rocketmotordesign.controler.request.HollowComputationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.grain.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNSU;
import static com.rocketmotordesign.utils.TestHelper.*;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser("spring") //TODO : a changer
public class ExportControlerIT {

    @Autowired
    private MockMvc mvc;

    @Test
    void shoulExportHollowGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultRequest());
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
                        "    0.0325 993.7791\n")))
                .andExpect(content().string(endsWith(
                        "    2.1452 53.4958\n" +
                        "    2.1468 48.7756\n" +
                        "    2.1483 44.3861\n" +
                        "    2.1499 40.3031\n" +
                        "    2.1514 36.5037\n" +
                        "    2.1529 32.9662\n" +
                        "    2.1545 29.6699\n" +
                        "    2.156 0.0"
                )));
    }

    @Test
    void shoulExportHollowGrainToRASPFromIMPERIAL() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultRequestImperial());
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
        lowKNRequest.setPropellantId(KNSU.name());
        lowKNRequest.setChamberInnerDiameter(38);
        lowKNRequest.setChamberLength(500);
        lowKNRequest.setExtraConfig(getDefaultExtraConfiguration());

        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(lowKNRequest);
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

    @Test
    void shoulExportMoonBurnerGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultMoonBurnerGrainRequest());
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
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=meteor_motorTest_H100.eng"))
                .andExpect(content().string(startsWith(
                        "H100 72.0 500.0 P 0.160 3.200 METEOR\n" +
                                "    0.0 0.0\n" +
                                "    0.0045 69.7428\n" +
                                "    0.0068 96.2062\n" +
                                "    0.0089 111.2724\n" +
                                "    0.011 120.2585\n" +
                                "    0.0129 125.7701\n" +
                                "    0.0149 129.2345\n" +
                                "    0.0169 131.4731\n" +
                                "    0.0188 132.9703\n" +
                                "    0.0207 134.0166\n" +
                                "    0.0227 134.7873")))
                .andExpect(content().string(endsWith(
                        "    1.7889 7.8994\n" +
                        "    1.7927 7.5149\n" +
                        "    1.7965 7.1289\n" +
                        "    1.8003 6.733\n" +
                        "    1.8042 6.3198\n" +
                        "    1.8081 5.8821\n" +
                        "    1.812 5.3692\n" +
                        "    1.816 4.9173\n" +
                        "    1.82 4.2899\n" +
                        "    1.8241 3.5054"
                )));
    }

    @Test
    void shoulExportRodTubeGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultRodTubeGrainRequest());
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
                .andExpect(header().stringValues(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=meteor_motorTest_G350.eng"))
                .andExpect(content().string(startsWith(
                        "G350 72.0 500.0 P 0.120 3.200 METEOR\n" +
                                "    0.0 0.0\n" +
                                "    0.0015 40.239\n" +
                                "    0.0024 74.9608\n" +
                                "    0.0031 106.4147\n" +
                                "    0.0038 134.9067\n" +
                                "    0.0045 160.7585\n" +
                                "    0.0051 184.2586\n")))
                .andExpect(content().string(endsWith(
                        "    0.4254 15.2871\n" +
                        "    0.4257 14.0407\n" +
                        "    0.4261 12.8719\n" +
                        "    0.4265 11.7757\n" +
                        "    0.4268 10.7468\n" +
                        "    0.4272 9.7807\n" +
                        "    0.4276 0.0"
                )));
    }

    @Test
    void shoulExportCSlotGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultCSlotGrainRequest());
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
                .andExpect(header().stringValues(
                        HttpHeaders.CONTENT_DISPOSITION, "" +
                                "attachment;filename=meteor_motorTest_H74.eng"))
                .andExpect(content().string(startsWith(
                        "H74 72.0 500.0 P 0.149 3.200 METEOR\n" +
                                "    0.0 0.0\n" +
                                "    0.0051 93.0265\n" +
                                "    0.0075 128.8857\n" +
                                "    0.0097 149.4019\n" +
                                "    0.0119 161.6152\n" +
                                "    0.014 169.0198\n" +
                                "    0.016 173.5535\n" +
                                "    0.018 176.3453\n" +
                                "    0.0201 178.0696\n" +
                                "    0.0221 179.1359\n" +
                                "    0.0241 179.7948\n")))
                .andExpect(content().string(endsWith(
                        "    2.2041 6.2797\n" +
                        "    2.2085 5.9626\n" +
                        "    2.2129 5.631\n" +
                        "    2.2174 5.2795\n" +
                        "    2.222 4.8653\n" +
                        "    2.2266 4.494\n" +
                        "    2.2312 4.0387\n" +
                        "    2.2359 3.4256\n" +
                        "    2.2407 2.6033")));
    }

    @Test
    void shoulExportFinocylGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultFinocylRequest());
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
                .andExpect(header().stringValues(
                        HttpHeaders.CONTENT_DISPOSITION, "" +
                                "attachment;filename=meteor_motorTest_H216.eng"))
                .andExpect(content().string(startsWith(
                        "H216 72.0 500.0 P 0.147 3.200 METEOR\n" +
                                "    0.0 0.0\n" +
                                "    0.0106 352.4739\n" +
                                "    0.0141 380.592\n" +
                                "    0.0175 390.7799\n" +
                                "    0.0209 394.5356\n" +
                                "    0.0242 395.7935\n" +
                                "    0.0276 396.1696\n" +
                                "    0.031 396.1203\n" +
                                "    0.0343 395.9863\n")))
                .andExpect(content().string(endsWith(
                        "    0.7728 8.0218\n" +
                        "    0.7817 6.9954\n" +
                        "    0.7907 6.1056\n" +
                        "    0.7999 5.1873\n" +
                        "    0.8093 4.4436\n" +
                        "    0.8189 3.5331\n" +
                        "    0.8288 2.7169\n" +
                        "    0.8389 1.7842\n" +
                        "    0.8493 0.1956"
                )));
    }

    @Test
    void shoulExportEndBurnerGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultEndBurnerGrainRequest());
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
                .andExpect(header().stringValues(
                        HttpHeaders.CONTENT_DISPOSITION, "" +
                                "attachment;filename=meteor_motorTest_G9.eng"))
                .andExpect(content().string(startsWith(
                        "G9 72.0 500.0 P 0.088 3.200 METEOR\n" +
                                "    0.0 0.0\n" +
                                "    0.0183 33.9692\n" +
                                "    0.0269 2.1199\n" +
                                "    0.0429 28.7249\n" +
                                "    0.0519 4.2595\n" +
                                "    0.066 23.9542\n" +
                                "    0.0755 6.3107\n" +
                                "    0.0885 20.5953\n" +
                                "    0.0983 8.1847")))
        .andExpect(content().string(endsWith(
                "    9.9343 3.7457\n" +
                "    9.9488 3.2542\n" +
                "    9.9637 2.5517\n" +
                "    9.9792 2.0708\n" +
                "    9.9952 1.5964\n" +
                "    10.0119 1.1889\n" +
                "    10.029 0.7604\n" +
                "    10.0467 0.1647")));
    }

    @Test
    void shoulExportStarGrainToRASP() throws Exception {
        // GIVEN
        ExportRASPRequest exportRequest = new ExportRASPRequest();
        exportRequest.setComputationRequest(getDefaultStarGrainRequest());
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
                .andExpect(status().isBadRequest());
    }
}
