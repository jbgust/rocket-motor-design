package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.GrainSurface;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.github.jbgust.jsrm.application.result.ThrustResult;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.PerformanceResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rocketmotordesign.controler.MainControler.createMotorAsSRM_2014ExcelFile;

@Controller
public class TestControler {

    @GetMapping("/test")
    public ModelAndView graphiques(ModelAndView modelAndView) {
        SolidRocketMotor motor = createMotorAsSRM_2014ExcelFile();
        return getModelAndView(modelAndView, motor, new JSRMConfigBuilder().withNozzleExpansionRatio(8).createJSRMConfig());
    }

    @PostMapping
    public ModelAndView post(@ModelAttribute("request") ComputationRequest request, ModelAndView modelAndView){
        SolidRocketMotor motor = toSolidRocketMotor(request);
        return getModelAndView(modelAndView, motor, new JSRMConfigBuilder().createJSRMConfig());
    }

    private ModelAndView getModelAndView(ModelAndView modelAndView, SolidRocketMotor motor, JSRMConfig jsrmConfig) {
        String error=null;
        JSRMResult result = null;
        PerformanceResult performance = null;
        try {
            result = new JSRMSimulation(motor).run(jsrmConfig);
            performance = new PerformanceResult(result, jsrmConfig);
        } catch (Exception e) {
            error=e.getMessage()+ " => "+e.getCause().getMessage();
        }

        modelAndView
                .addObject("results", result != null ? result.getThrustResults() : null)
                .addObject("performance", performance)
                .addObject("error", error)
                .addObject("request", toComputationRequest(motor) )
                .addObject("surfaces", Stream.of(GrainSurface.values()).map(grainSurface -> new IdName(grainSurface.name(), grainSurface.name())).collect(Collectors.toList()))
                .addObject("propellants", Stream.of(PropellantType.values()).map(grainSurface -> new IdName(grainSurface.name(), grainSurface.name())).collect(Collectors.toList()))
                .setViewName("graphiques");
        return modelAndView;
    }

    private SolidRocketMotor toSolidRocketMotor(ComputationRequest request) {
        PropellantGrain propellantGrain = new PropellantGrain(request.getPropellantType(), request.getOuterDiameter(), request.getCoreDiameter(), request.getSegmentLength()
                , request.getNumberOfSegment(), request.getOuterSurface(), request.getEndsSurface(), request.getCoreSurface());
        return new SolidRocketMotor(propellantGrain, new CombustionChamber(request.getChamberInnerDiameter(), request.getChamberLength()), request.getThroatDiameter());
    }

    private ComputationRequest toComputationRequest(SolidRocketMotor solidRocketMotor) {

        PropellantGrain propellantGrain = solidRocketMotor.getPropellantGrain();
        return new ComputationRequest(solidRocketMotor.getThroatDiameterInMillimeter(), propellantGrain.getOuterDiameter(), propellantGrain.getCoreDiameter(), propellantGrain.getSegmentLength()
        , propellantGrain.getNumberOfSegment(), propellantGrain.getOuterSurface(), propellantGrain.getEndsSurface(), propellantGrain.getCoreSurface(),
                (PropellantType) propellantGrain.getPropellant(),
                solidRocketMotor.getCombustionChamber().getChamberInnerDiameterInMillimeter(),
                solidRocketMotor.getCombustionChamber().getChamberLengthInMillimeter());
    }

    private List<ThrustResult> reduce(JSRMResult result) {
        AtomicInteger i = new AtomicInteger();
        return result.getThrustResults().stream()
                .filter(thrustResult -> i.getAndIncrement() % 10 == 0)
                .collect(Collectors.toList());
    }

    public class IdName {
        public String id;
        public String name;

        public IdName(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
