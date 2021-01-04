package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.request.CustomPropellantRequest;
import com.rocketmotordesign.controler.request.HollowComputationRequest;
import com.rocketmotordesign.propellant.repository.MeteorPropellantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static com.rocketmotordesign.utils.TestHelper.customPropellantToMeteorPropellant;
import static com.rocketmotordesign.utils.TestHelper.getDefaultRequestImperial;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@Import({JSRMService.class, MeasureUnitService.class, ConfigRestricterService.class})
public class JSRMServiceTest {

    @Autowired
    private JSRMService jsrmService;

    @MockBean
    private MeteorPropellantRepository propellantRepository;

    @Test
    void shouldUseCustomPropellantViper() throws UnauthorizedValueException {
        // GIVEN
        CustomPropellantRequest customPropellant = new CustomPropellantRequest();
        customPropellant.setCstar(5468.4);
        customPropellant.setBurnRateCoefficient(0.0174);
        customPropellant.setPressureExponent(0.4285);
        customPropellant.setDensity(0.06);
        customPropellant.setK(1.2768);
        customPropellant.setMolarMass(45.0);

        UUID customPropellantId = UUID.randomUUID();
        given(propellantRepository.findById(customPropellantId))
                .willReturn(Optional.of(customPropellantToMeteorPropellant(customPropellant)));

        HollowComputationRequest defaultRequest = getDefaultRequestImperial();

        defaultRequest.setPropellantId("To be defined");
        defaultRequest.getExtraConfig().setNozzleExpansionRatio(8.0);
        defaultRequest.getExtraConfig().setNozzleEfficiency(0.85);
        defaultRequest.getExtraConfig().setOptimalNozzleDesign(false);
        defaultRequest.getExtraConfig().setCombustionEfficiencyRatio(1);
        defaultRequest.getExtraConfig().setDensityRatio(1);

        defaultRequest.setPropellantId(customPropellantId.toString());

        JSRMResult jsrmResult = jsrmService.runComputation(defaultRequest).getResult();
        assertThat(100*1878/jsrmResult.getAverageThrustInNewton())
                .describedAs("At least 98% matching burnsim result for viper propellant")
                .isGreaterThanOrEqualTo(98);
    }
}
