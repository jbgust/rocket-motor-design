package com.rocketmotordesign.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rocketmotordesign.controler.request.BasicComputationRequest;
import com.rocketmotordesign.controler.response.GraphResult;

@Service
public class ResultService {

    private Integer moduloLimitSize;
    private final MeasureUnitService measureUnitService;

    public ResultService(
            @Value("${computation.response.limit.size}") Integer moduloLimitSize, MeasureUnitService measureUnitService) {
        this.moduloLimitSize = moduloLimitSize;
        this.measureUnitService = measureUnitService;
    }

    public List<GraphResult> reduceGraphResults(SimulationResult result, MeasureUnit userUnits, BasicComputationRequest request) {
        boolean allResults = request.getExtraConfig().getNumberOfCalculationLine() != null;
        AtomicInteger i = new AtomicInteger();
        return result.getMotorParameters(request.isRemovePostBurnResult()).stream()
                .filter(thrustResult -> allResults || (moduloLimitSize == 1 || i.getAndIncrement() % moduloLimitSize == 0))
                .map(motorParameters1 -> measureUnitService.toGraphResult(motorParameters1, userUnits))
                .collect(Collectors.toList());
    }
}
