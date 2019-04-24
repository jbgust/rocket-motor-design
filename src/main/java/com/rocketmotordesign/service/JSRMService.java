package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
        JSRMConfig customConfig = toJSRMConfig(request.getExtraConfig());
        JSRMResult jsrmResult = new JSRMSimulation(measureUnitService.toSolidRocketMotor(request)).run(customConfig);

        MeasureUnit userUnits = request.getMeasureUnit();

        LOGGER.info("METEOR[MOTORCLASS|{}]", jsrmResult.getMotorClassification());
        return new ComputationResponse(
                measureUnitService.toPerformanceResult(jsrmResult, customConfig, userUnits),
                reduceGraphResults(jsrmResult, userUnits));
    }

    private JSRMConfig toJSRMConfig(ExtraConfiguration extraConfig) {
        JSRMConfigBuilder jsrmConfigBuilder = new JSRMConfigBuilder()
                .withAmbiantPressureInMPa(extraConfig.getAmbiantPressureInMPa())
                .withCombustionEfficiencyRatio(extraConfig.getCombustionEfficiencyRatio())
                .withDensityRatio(extraConfig.getDensityRatio())
                .withErosiveBurningAreaRatioThreshold(extraConfig.getErosiveBurningAreaRatioThreshold())
                .withErosiveBurningVelocityCoefficient(extraConfig.getErosiveBurningVelocityCoefficient())
                .withNozzleEfficiency(extraConfig.getNozzleEfficiency())
                .withNozzleErosionInMillimeter(extraConfig.getNozzleErosionInMillimeter())
                .withOptimalNozzleDesign(extraConfig.isOptimalNozzleDesign());

        if(extraConfig.getNozzleExpansionRatio() != null){
            jsrmConfigBuilder.withNozzleExpansionRatio(extraConfig.getNozzleExpansionRatio());
        }

        return jsrmConfigBuilder.createJSRMConfig();
    }

    private List<GraphResult> reduceGraphResults(JSRMResult result, MeasureUnit userUnits) {
        AtomicInteger i = new AtomicInteger();
        return result.getMotorParameters().stream()
                .filter(thrustResult -> moduloLimitSize ==1 || i.getAndIncrement() % moduloLimitSize == 0)
                .map(motorParameters1 -> measureUnitService.toGraphResult(motorParameters1, userUnits))
                .collect(Collectors.toList());
    }
}
