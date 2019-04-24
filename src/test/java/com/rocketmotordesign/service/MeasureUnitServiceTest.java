package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.utils.TestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@Import(MeasureUnitService.class)
public class MeasureUnitServiceTest {

    @Autowired
    private MeasureUnitService measureUnitService;

    @Test
    public void shouldConvertToImperialUnit() {

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(TestHelper.getDefaultRequestImperial());
        ComputationRequest defaultRequestSIUnit = TestHelper.getDefaultRequest();

        assertThat(solidRocketMotor.getThroatDiameterInMillimeter()).isEqualTo(defaultRequestSIUnit.getThroatDiameter());
    }
}