package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.result.JSRMResult;

public class JSRMResultConverter extends JSRMResult {

    public JSRMResultConverter(JSRMResult jsrmResult, MeasureUnit measureUnit) {
        super(jsrmResult.getMaxThrustInNewton(),
                jsrmResult.getTotalImpulseInNewtonSecond(),
                jsrmResult.getSpecificImpulseInSecond(),
                measureUnit.convertPressure(jsrmResult.getMaxChamberPressureInMPa()),
                jsrmResult.getAverageChamberPressureInMPa(),
                jsrmResult.getThrustTimeInSecond(),
                jsrmResult.getMotorClassification(),
                jsrmResult.getMotorParameters(),
                jsrmResult.getNozzle(),
                jsrmResult.getAverageThrustInNewton());
    }

}
