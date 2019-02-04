package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.ThrustResult;

import java.util.List;

public class ComputationResponse {

    private final PerformanceResult performanceResult;
    private final List<ThrustResult> thrustResults;

    public ComputationResponse(JSRMResult jsrmResult, JSRMConfig jsrmConfig) {
       this.performanceResult = new PerformanceResult(jsrmResult, jsrmConfig);
        this.thrustResults = jsrmResult.getThrustResults();
    }

    public PerformanceResult getPerformanceResult() {
        return performanceResult;
    }

    public List<ThrustResult> getThrustResults() {
        return thrustResults;
    }
}
