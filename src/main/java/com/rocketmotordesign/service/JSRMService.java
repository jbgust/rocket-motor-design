package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.rocketmotordesign.controler.request.BasicComputationRequest;
import org.springframework.stereotype.Service;

@Service
public class JSRMService {

    private final MeasureUnitService measureUnitService;
    private final ConfigRestricterService configRestricterService;

    public JSRMService(MeasureUnitService measureUnitService, ConfigRestricterService configRestricterService) {
        this.measureUnitService = measureUnitService;
        this.configRestricterService = configRestricterService;
    }

    public SimulationResult runComputation(BasicComputationRequest request) throws UnauthorizedValueException {
       return runComputation(request, false);
    }

    public SimulationResult runComputation(BasicComputationRequest request, boolean safeKnRun) throws UnauthorizedValueException {
        MeasureUnit userUnits = request.getMeasureUnit();
        configRestricterService.applyRestriction(request);

        JSRMConfig customConfig = measureUnitService.toJSRMConfig(request.getExtraConfig(), userUnits, safeKnRun);

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);

        return new SimulationResult(new JSRMSimulation(solidRocketMotor).run(customConfig), customConfig);
    }
}
