package com.rocketmotordesign.controler;

import com.jsrm.application.JSRMSimulation;
import com.jsrm.application.result.JSRMResult;
import com.jsrm.application.result.ThrustResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.rocketmotordesign.controler.MainControler.createMotorAsSRM_2014ExcelFile;

@Controller
public class GraphiqueControler {

    @GetMapping("/test")
    public ModelAndView graphiques(ModelAndView modelAndView) {
        JSRMResult result = new JSRMSimulation(createMotorAsSRM_2014ExcelFile()).run();

        modelAndView.addObject("results",  reduce(result)).setViewName("graphiques");
        return modelAndView;
    }

    private List<ThrustResult> reduce(JSRMResult result) {
        AtomicInteger i = new AtomicInteger();
        return result.getThrustResults().stream()
                .filter(thrustResult -> i.getAndIncrement() % 10 == 0)
                .collect(Collectors.toList());
    }
}
