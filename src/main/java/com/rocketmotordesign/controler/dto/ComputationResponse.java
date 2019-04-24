package com.rocketmotordesign.controler.dto;

import java.util.List;

public class ComputationResponse {

    private final PerformanceResult performanceResult;
    private final List<GraphResult> motorParameters;

    public ComputationResponse(PerformanceResult performanceResult, List<GraphResult> motorParameters) {
        this.performanceResult = performanceResult;
        this.motorParameters = motorParameters;
    }

    public PerformanceResult getPerformanceResult() {
        return performanceResult;
    }

    public List<GraphResult> getMotorParameters() {
        return motorParameters;
    }
}
