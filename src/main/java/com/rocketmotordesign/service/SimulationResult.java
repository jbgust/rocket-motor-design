package com.rocketmotordesign.service;

import java.util.List;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.MotorParameters;

public class SimulationResult {

    private final JSRMResult result;
    private final JSRMConfig config;

    public SimulationResult(JSRMResult result, JSRMConfig config) {
        this.result = result;
        this.config = config;
    }

    public JSRMResult getResult() {
        return result;
    }

    public List<MotorParameters> getMotorParameters(boolean removePostBurnResult) {
        if (removePostBurnResult) {
            return result.getMotorParameters().subList(0, config.getNumberLineDuringBurnCalculation());
        } else {
            return result.getMotorParameters();
        }
    }
}
