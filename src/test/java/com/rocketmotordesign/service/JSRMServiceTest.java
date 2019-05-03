package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.request.ComputationRequest;
import com.rocketmotordesign.controler.response.ComputationResponse;
import com.rocketmotordesign.controler.request.CustomPropellantRequest;
import com.rocketmotordesign.utils.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setCstar(5468.4);
        customPropellant.setBurnRateCoefficient(0.0174);
        customPropellant.setPressureExponent(0.4285);
        customPropellant.setDensity(0.06);
        customPropellant.setK(1.2768);
        customPropellant.setMolarMass(45.0);

        ComputationRequest defaultRequest = getDefaultRequestImperial();

        defaultRequest.setPropellantType("To be defined");
        defaultRequest.getExtraConfig().setNozzleExpansionRatio(8.0);
        defaultRequest.getExtraConfig().setNozzleEfficiency(0.85);
        defaultRequest.getExtraConfig().setOptimalNozzleDesign(false);
        defaultRequest.getExtraConfig().setCombustionEfficiencyRatio(1);
        defaultRequest.getExtraConfig().setDensityRatio(1);
        defaultRequest.setCustomPropellant(customPropellant);

        MeasureUnit userUnits = defaultRequest.getMeasureUnit();

        JSRMConfig customConfig = measureUnitService.toJSRMConfig(defaultRequest.getExtraConfig(), userUnits);
        JSRMResult jsrmResult = new JSRMSimulation(measureUnitService.toSolidRocketMotor(defaultRequest)).run(customConfig);
        assertThat(100*1878/jsrmResult.getAverageThrustInNewton())
                .describedAs("At least 98% matching burnsim result for viper propellant")
                .isGreaterThanOrEqualTo(98);
    }
}