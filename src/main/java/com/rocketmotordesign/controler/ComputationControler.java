package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.exception.JSRMException;
import com.github.jbgust.jsrm.application.exception.MotorClassificationOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.rocketmotordesign.controler.request.*;
import com.rocketmotordesign.controler.response.ComputationResponse;
import com.rocketmotordesign.controler.response.ErrorMessage;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@RestController()
@RequestMapping("compute")
public class ComputationControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputationControler.class);

    private final JSRMService jsrmService;
    private final MeasureUnitService measureUnitService;
    private final ResultService resultService;

    private boolean enableStarGrain;

    public ComputationControler(JSRMService jsrmService,
                                MeasureUnitService measureUnitService,
                                ResultService resultService, @Value("${computation.star.enable:false}") boolean enableStarGrain) {
        this.jsrmService = jsrmService;
        this.measureUnitService = measureUnitService;
        this.resultService = resultService;
        this.enableStarGrain = enableStarGrain;
    }

    @PostMapping("finocyl")
    public ResponseEntity computeFinocyl(@RequestBody FinocylComputationRequest request) {
        return computeRequest(request, true);
    }

    @PostMapping("star")
    public ResponseEntity computeStar(@RequestBody StarGrainComputationRequest request) {
        if(enableStarGrain) {
            return computeRequest(request, false);
        } else {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Star grain is no more available",
                            "Due to performance problem star grain are temporarily not available."));
        }

    }

    @PostMapping("moonburner")
    public ResponseEntity computeMoonBurner(@RequestBody MoonBurnerGrainComputationRequest request) {
        return computeRequest(request, true);
    }

    @PostMapping("cslot")
    public ResponseEntity computeCSlot(@RequestBody CSlotGrainComputationRequest request) {
        return computeRequest(request, true);
    }

    @PostMapping("rodtube")
    public ResponseEntity computeRodTube(@RequestBody RodTubeGrainComputationRequest request) {
        return computeRequest(request, true);
    }

    @PostMapping("endburner")
    public ResponseEntity computeEndBurner(@RequestBody EndBurnerGrainComputationRequest request) {
        return computeRequest(request, true);
    }

    @PostMapping
    public ResponseEntity computeHollowCylinderGrain(@RequestBody HollowComputationRequest request) {
        return computeRequest(request, true);
    }

    private ResponseEntity computeRequest(BasicComputationRequest request, boolean enableSafeKn) {
        try {
            ComputationResponse response = toComputationResponse(request, jsrmService.runComputation(request));
            logSuccess(request, response);
            return ResponseEntity.ok(response);
        } catch (JSRMException e) {
            LOGGER.warn("METEOR[FAILED|{}]", e.getClass().getSimpleName());
            if (e instanceof InvalidMotorDesignException) {
                LOGGER.warn("InvalidMotorDesignException : {}", e.getMessage());
                LOGGER.debug("InvalidMotorDesignException suite : {}", e.getMessage());
                return ResponseEntity.badRequest().body(
                        new ErrorMessage(e.getMessage()));
            } else if (e.getCause() != null && e.getCause().getCause() instanceof CustomPropellantChamberPressureOutOfBoundException) {
                LOGGER.warn("CustomPropellantChamberPressureOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getCause().getMessage()));
            } else if (e.getCause() != null && e.getCause() instanceof MotorClassificationOutOfBoundException) {
                LOGGER.warn("MotorClassificationOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getMessage()));
            } else if (enableSafeKn){
                LOGGER.warn("Computation failed, retry with low KN");
                LOGGER.warn("Computation failed request {} : {}", request.hashCode(), request.toString());
                return retryWithSafeKN(request);
            } else {
                LOGGER.warn("Computation failed, safe KN retry NOT ALLOWED");
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", "This often occurs when the ratio between the burning area and the throat area is too low. Try to increase your grain core diameter and/or decrease the throat diameter."));
            }
        } catch (BurnRateDataException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("METEOR can't run this computation due to the following error:", "Your burn rate data are invalid. " + e.getMessage()));
        } catch (UnauthorizedValueException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("METEOR can't run this computation due to the following error:",
                            e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unknown computation error with request : " + request.toString(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknown error, please contact us."));
        }
    }

    private void logSuccess(BasicComputationRequest request, ComputationResponse response) {
        LOGGER.info("METEOR[REQUEST|{}]", request.hashCode());

        LOGGER.info("METEOR[GRAIN|{}]", request.getGrainType());
        LOGGER.info("METEOR[CLIENT-ID|{}]", getUserId());
        LOGGER.info("METEOR[UNITS|{}]", request.getMeasureUnit());
        Map<String, SolidPropellant> propellants = Stream.of(PropellantType.values())
                .collect(toMap(Enum::name, Function.identity()));
        LOGGER.info("METEOR[PROPELLANT|{}]", propellants.containsKey(request.getPropellantId()) ? request.getPropellantId() : "CUSTOM");
        LOGGER.info("METEOR[MOTORCLASS|{}]", response.getPerformanceResult().getMotorDescription().substring(0, 1));
    }

    private String getUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            return ((User)principal).getId().toString();
        } else {
            return principal.toString();
        }
    }

    private ResponseEntity retryWithSafeKN(BasicComputationRequest request) {
        try {
            LOGGER.warn("METEOR[safeKN]");
            ComputationResponse response = toComputationResponse(request, jsrmService.runComputation(request, true));
            logSuccess(request, response);
            return ResponseEntity.ok(response);
        } catch (JSRMException e) {
            LOGGER.warn("METEOR[FAILED|{}]", e.getClass().getSimpleName());
            if (e.getCause() != null && e.getCause().getCause() instanceof CustomPropellantChamberPressureOutOfBoundException) {
                LOGGER.warn("CustomPropellantChamberPressureOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getCause().getMessage()));
            } else if (e.getCause() != null && e.getCause() instanceof MotorClassificationOutOfBoundException) {
                LOGGER.warn("MotorClassificationOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getMessage()));
            } else {
                LOGGER.warn("Computation failed : CAUSE : {}", e.getCause().getMessage());
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", "This often occurs when the ratio between the burning area and the throat area is too low. Try to increase your grain core diameter and/or decrease the throat diameter."));
            }
        } catch (Exception e) {
            LOGGER.error("Unknown computation error with request : " + request.toString(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknown error, please contact us."));
        }
    }

    private ComputationResponse toComputationResponse(BasicComputationRequest request, SimulationResult simulationResult) {
        MeasureUnit userUnits = request.getMeasureUnit();
        return new ComputationResponse(
                measureUnitService.toPerformanceResult(simulationResult.getResult(), request.getExtraConfig().isOptimalNozzleDesign(), userUnits),
                resultService.reduceGraphResults(simulationResult, userUnits, request));
    }

}
