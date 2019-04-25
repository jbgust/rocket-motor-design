package com.rocketmotordesign.service;

import com.rocketmotordesign.controler.dto.ComputationResponse;
import com.rocketmotordesign.utils.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@Import({JSRMService.class, MeasureUnitService.class})
@TestPropertySource(properties = "computation.response.limit.size=4")
public class JSRMServiceTest {

    @Autowired
    private JSRMService jsrmService;

    @Test
    public void shouldReduceResult() {

        ComputationResponse response = jsrmService.runComputation(TestHelper.getDefaultRequest());

        assertThat(response.getMotorParameters())
                .hasSize(221);
    }
}