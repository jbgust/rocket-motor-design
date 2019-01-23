package com.rocketmotordesign.controler;

import com.jsrm.application.JSRMSimulation;
import com.jsrm.application.motor.MotorChamber;
import com.jsrm.application.motor.SolidRocketMotor;
import com.jsrm.application.motor.propellant.GrainSurface;
import com.jsrm.application.motor.propellant.PropellantGrain;
import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.result.ThrustResult;
import com.jsrm.infra.propellant.PropellantType;
import com.rocketmotordesign.controler.dto.ComputationRequest;
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
public class GraphiqueControler {

    @GetMapping("/test")
    public ModelAndView graphiques(ModelAndView modelAndView) {
        SolidRocketMotor motor = createMotorAsSRM_2014ExcelFile();
        return getModelAndView(modelAndView, motor);
    }

    @PostMapping
    public ModelAndView post(@ModelAttribute("request") ComputationRequest request, ModelAndView modelAndView){
        SolidRocketMotor motor = toSolidRocketMotor(request);
        return getModelAndView(modelAndView, motor);
    }

    private ModelAndView getModelAndView(ModelAndView modelAndView, SolidRocketMotor motor) {
        String error=null;
        JSRMResult result = null;
        try {
            result = new JSRMSimulation(motor).run();
        } catch (Exception e) {
            error=e.getMessage()+ " => "+e.getCause().getMessage();
        }

        modelAndView
                .addObject("results", result!=null?result.getThrustResults(): null)
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
        return new SolidRocketMotor(propellantGrain, new MotorChamber(request.getChamberInnerDiameter(), request.getChamberLength()), request.getThroatDiameter());
    }

    private ComputationRequest toComputationRequest(SolidRocketMotor solidRocketMotor) {

        PropellantGrain propellantGrain = solidRocketMotor.getPropellantGrain();
        return new ComputationRequest(solidRocketMotor.getThroatDiameter(), propellantGrain.getOuterDiameter(), propellantGrain.getCoreDiameter(), propellantGrain.getSegmentLength()
        , propellantGrain.getNumberOfSegment(), propellantGrain.getOuterSurface(), propellantGrain.getEndsSurface(), propellantGrain.getCoreSurface(),
                (PropellantType) propellantGrain.getPropellant(),
                solidRocketMotor.getMotorChamber().getChamberInnerDiameter(), solidRocketMotor.getMotorChamber().getChamberLength());
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
