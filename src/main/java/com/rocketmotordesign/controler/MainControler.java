package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.exception.JSRMException;
import com.github.jbgust.jsrm.application.exception.MotorClassificationOutOfBoundException;
import com.rocketmotordesign.controler.request.ComputationRequest;
import com.rocketmotordesign.controler.response.ErrorMessage;
import com.rocketmotordesign.service.BurnRateDataException;
import com.rocketmotordesign.service.CustomPropellantChamberPressureOutOfBoundException;
import com.rocketmotordesign.service.JSRMService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainControler.class);

    private final JSRMService jsrmService;
    private final Counter calculs;

    public MainControler(JSRMService jsrmService) {
        this.jsrmService = jsrmService;
        Metrics.addRegistry(new SimpleMeterRegistry());
        calculs = Metrics.counter("calculs");
    }

    @PostMapping("/compute")
    public ResponseEntity compute(@RequestBody ComputationRequest request){
        try {
            LOGGER.info("METEOR[REQUEST|{}]", request.hashCode());
            calculs.increment();
            System.err.println("\nreq = "+calculs.count());
            return ResponseEntity.ok(jsrmService.runComputation(request));
        } catch (JSRMException e) {
            LOGGER.warn("METEOR[FAILED|{}]", e.getClass().getSimpleName());
            if(e instanceof InvalidMotorDesignException){
                LOGGER.warn("InvalidMotorDesignException : {}", e.getMessage());
                LOGGER.debug("InvalidMotorDesignException suite : {}", e.getMessage());
                return ResponseEntity.badRequest().body(
                        new ErrorMessage(e.getMessage()));
            } else if (e.getCause() != null && e.getCause().getCause() instanceof CustomPropellantChamberPressureOutOfBoundException) {
                LOGGER.error("CustomPropellantChamberPressureOutOfBoundException : "+request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:",e.getCause().getCause().getMessage()));
            } else if (e.getCause() != null && e.getCause() instanceof MotorClassificationOutOfBoundException) {
                LOGGER.error("MotorClassificationOutOfBoundException : "+request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:",e.getCause().getMessage()));
            } else {
                LOGGER.warn("Computation failed, retry with low KN");
                return retryWithSafeKN(request);
            }
        } catch (BurnRateDataException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("METEOR can't run this computation due to the following error:", "Your burn rate data are invalid. "+e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unknown computation error with request : "+request.toString(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknown error, please contact us."));
        }
    }

    private ResponseEntity retryWithSafeKN(ComputationRequest request) {
        try {
            LOGGER.warn("METEOR[safeKN]");
            LOGGER.warn("Computation failed request {} : {}", request.hashCode(), request.toString());
            return ResponseEntity.ok(jsrmService.runComputation(request, true));
        } catch (JSRMException e) {
            LOGGER.warn("METEOR[FAILED|{}]", e.getClass().getSimpleName());
           if (e.getCause() != null && e.getCause().getCause() instanceof CustomPropellantChamberPressureOutOfBoundException) {
                LOGGER.error("CustomPropellantChamberPressureOutOfBoundException : "+request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:",e.getCause().getCause().getMessage()));
            } else if (e.getCause() != null && e.getCause() instanceof MotorClassificationOutOfBoundException) {
                LOGGER.error("MotorClassificationOutOfBoundException : "+request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:",e.getCause().getMessage()));
            } else {
                LOGGER.warn("Computation failed : CAUSE : {}", e.getCause().getMessage());
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", "This often occurs when the ratio between the burning area and the throat area is too low. Try to increase your grain core diameter and/or decrease the throat diameter."));
            }
        } catch (Exception e) {
            LOGGER.error("Unknown computation error with request : "+request.toString(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknown error, please contact us."));
        }
    }

}
