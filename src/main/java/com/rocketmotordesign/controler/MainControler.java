package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.exception.JSRMException;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ErrorMessage;
import com.rocketmotordesign.service.JSRMService;
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

    public MainControler(JSRMService jsrmService) {
        this.jsrmService = jsrmService;
    }

    @PostMapping("/compute")
    public ResponseEntity compute(@RequestBody ComputationRequest request){
        try {
            LOGGER.info("METEOR[REQUEST|{}]", request.hashCode());
            return ResponseEntity.ok(jsrmService.runComputation(request));
        } catch (JSRMException e) {
            LOGGER.warn("METEOR[FAILED|{}]", e.getClass().getSimpleName());
            if(e instanceof InvalidMotorDesignException){
                LOGGER.warn("InvalidMotorDesignException : {}", e.getMessage());
                LOGGER.debug("InvalidMotorDesignException suite : {}", e.getMessage());
                return ResponseEntity.badRequest().body(
                        new ErrorMessage(e.getMessage()));
            } else {
                LOGGER.error("Computation failed : \n\t{} \n\tCAUSE : {}", request.toString(), e.getCause().getMessage());
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getMessage()));
            }
        }catch (Exception e) {
            LOGGER.error("Unknown computation error", e);
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknown error, please contact us."));
        }
    }


}
