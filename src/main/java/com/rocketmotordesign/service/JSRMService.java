package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.request.ComputationRequest;
import org.springframework.stereotype.Service;

@Service
public class JSRMService {

    private final MeasureUnitService measureUnitService;

    public JSRMService(MeasureUnitService measureUnitService) {
        this.measureUnitService = measureUnitService;
    }

    public JSRMResult runComputation(ComputationRequest request) {
       return runComputation(request, false);
    }

    public JSRMResult runComputation(ComputationRequest request, boolean safeKnRun) {
        MeasureUnit userUnits = request.getMeasureUnit();

        JSRMConfig customConfig = measureUnitService.toJSRMConfig(request.getExtraConfig(), userUnits, safeKnRun);

        SolidRocketMotor solidRocketMotor = measureUnitService.toSolidRocketMotor(request);
        JSRMResult jsrmResult = new JSRMSimulation(solidRocketMotor).run(customConfig);

        return jsrmResult;
    }
}
