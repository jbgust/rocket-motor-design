package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.result.JSRMResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ComputationResponse {

    private final PerformanceResult performanceResult;
    private final List<GraphResult> thrustResults;

    public ComputationResponse(JSRMResult jsrmResult, JSRMConfig jsrmConfig) {
       this.performanceResult = new PerformanceResult(jsrmResult, jsrmConfig);
        this.thrustResults = reduce(jsrmResult);
    }

    public PerformanceResult getPerformanceResult() {
        return performanceResult;
    }

    public List<GraphResult> getThrustResults() {
        return thrustResults;
    }

    private List<GraphResult> reduce(JSRMResult result) {
        AtomicInteger i = new AtomicInteger();
        return result.getThrustResults().stream()
//                .filter(thrustResult -> i.getAndIncrement() % 10 == 0)
                .map(GraphResult::new)
                .collect(Collectors.toList());
    }
}
