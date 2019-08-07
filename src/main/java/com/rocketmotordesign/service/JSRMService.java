package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.request.ComputationRequest;
import com.rocketmotordesign.controler.response.ComputationResponse;
import com.rocketmotordesign.controler.response.GraphResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Service
public class JSRMService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSRMService.class);

    @Value("${computation.response.limit.size}")
    private Integer moduloLimitSize;

    private final MeasureUnitService measureUnitService;

    public JSRMService(MeasureUnitService measureUnitService) {
        this.measureUnitService = measureUnitService;
    }

    public ComputationResponse runComputation(ComputationRequest request) {
       return runComputation(request, false);
    }

    public ComputationResponse runComputation(ComputationRequest request, boolean safeKnRun) {
        MeasureUnit userUnits = request.getMeasureUnit();

        //evite de logger deux fois l'unité si safeKN
        if(!safeKnRun) {
            LOGGER.info("METEOR[UNITS|{}]", userUnits);
        }

        JSRMConfig customConfig = measureUnitService.toJSRMConfig(request.getExtraConfig(), userUnits, safeKnRun);

        //evite de logger deux fois l'unité si safeKN
        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);
        if(!safeKnRun) {
            Map<String, SolidPropellant> propellants = Stream.of(PropellantType.values())
                    .collect(toMap(Enum::name, Function.identity()));
            LOGGER.info("METEOR[PROPELLANT|{}]",propellants.containsKey(request.getPropellantType()) ? request.getPropellantType() : "CUSTOM");
        }
        JSRMResult jsrmResult = new JSRMSimulation(solidRocketMotor).run(customConfig);

        LOGGER.info("METEOR[MOTORCLASS|{}]", jsrmResult.getMotorClassification());
        return new ComputationResponse(
                measureUnitService.toPerformanceResult(jsrmResult, customConfig, userUnits),
                reduceGraphResults(jsrmResult, userUnits));
    }

    private List<GraphResult> reduceGraphResults(JSRMResult result, MeasureUnit userUnits) {
        AtomicInteger i = new AtomicInteger();
        return result.getMotorParameters().stream()
                .filter(thrustResult -> moduloLimitSize ==1 || i.getAndIncrement() % moduloLimitSize == 0)
                .map(motorParameters1 -> measureUnitService.toGraphResult(motorParameters1, userUnits))
                .collect(Collectors.toList());
    }
}
