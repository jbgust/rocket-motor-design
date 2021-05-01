package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.SimulationFailedException;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.request.BasicComputationRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JSRMService {

    private final MeasureUnitService measureUnitService;
    private final ConfigRestricterService configRestricterService;
    private long maxSafeCorrection;

    public JSRMService(MeasureUnitService measureUnitService, ConfigRestricterService configRestricterService,
                       @Value("${computation.max.safe.correction:200}") long maxSafeCorrection) {
        this.measureUnitService = measureUnitService;
        this.configRestricterService = configRestricterService;
        this.maxSafeCorrection = maxSafeCorrection;
    }

    public SimulationResult runComputation(BasicComputationRequest request) throws UnauthorizedValueException {
       return runComputation(request, false);
    }

    public SimulationResult runComputation(BasicComputationRequest request, boolean safeKnRun) throws UnauthorizedValueException {
        MeasureUnit userUnits = request.getMeasureUnit();
        configRestricterService.applyRestriction(request);

        JSRMConfig customConfig = measureUnitService.toJSRMConfig(request.getExtraConfig(), userUnits, safeKnRun);

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        JSRMResult jsrmResult = new JSRMSimulation(solidRocketMotor).run(customConfig);
        if(jsrmResult.getNumberOfKNCorrection() > maxSafeCorrection) {
            throw new SimulationFailedException(new Exception("Safe correction exceeded"));
        }
        return new SimulationResult(jsrmResult, customConfig);
    }
}
