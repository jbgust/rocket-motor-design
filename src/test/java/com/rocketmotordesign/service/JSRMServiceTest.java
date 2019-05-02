package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ComputationResponse;
import com.rocketmotordesign.controler.dto.CustomPropellantRequest;
import com.rocketmotordesign.controler.dto.MeasureUnit;
import com.rocketmotordesign.utils.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.rocketmotordesign.utils.TestHelper.getDefaultRequest;
import static com.rocketmotordesign.utils.TestHelper.getDefaultRequestImperial;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Import({JSRMService.class, MeasureUnitService.class})
@TestPropertySource(properties = "computation.response.limit.size=4")
public class JSRMServiceTest {

    @Autowired
    private JSRMService jsrmService;

    @Autowired
    private MeasureUnitService measureUnitService;

    @Test
    public void shouldReduceResult() {

        ComputationResponse response = jsrmService.runComputation(TestHelper.getDefaultRequest());

        assertThat(response.getMotorParameters())
                .hasSize(221);
    }

    @Test
    public void shouldUseCustomPropellantViper() {
        // GIVEN
        ComputationRequest defaultRequest = getDefaultRequestImperial();
        //defaultRequest.setChamberLength(defaultRequest.getSegmentLength()*defaultRequest.getNumberOfSegment());
        //TODO voir la valeur ci-dessous
        defaultRequest.setPropellantType("To be defined");
        defaultRequest.getExtraConfig().setNozzleExpansionRatio(8.0);
        defaultRequest.getExtraConfig().setNozzleEfficiency(0.85);
        defaultRequest.getExtraConfig().setOptimalNozzleDesign(false);
        defaultRequest.getExtraConfig().setCombustionEfficiencyRatio(1);
        defaultRequest.getExtraConfig().setDensityRatio(1);


        //TODO : a convertir en SI, valeur en imperial pour le moment
        defaultRequest.setCustomPropellant(new CustomPropellantRequest(
                5468.4, 0.0174, 0.4285, 0.06, 1.2768, 45.0
        ));

        MeasureUnit userUnits = defaultRequest.getMeasureUnit();

        JSRMConfig customConfig = measureUnitService.toJSRMConfig(defaultRequest.getExtraConfig(), userUnits);
        JSRMResult jsrmResult = new JSRMSimulation(measureUnitService.toSolidRocketMotor(defaultRequest)).run(customConfig);
        assertThat(100*1878/jsrmResult.getAverageThrustInNewton()).isGreaterThan(95);
    }
}