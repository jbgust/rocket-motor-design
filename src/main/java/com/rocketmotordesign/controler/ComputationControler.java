package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.exception.JSRMException;
import com.github.jbgust.jsrm.application.exception.MotorClassificationOutOfBoundException;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantType;
import com.github.jbgust.jsrm.application.motor.propellant.SolidPropellant;
import com.rocketmotordesign.controler.request.*;
import com.rocketmotordesign.controler.response.ComputationResponse;
import com.rocketmotordesign.controler.response.ErrorMessage;
import com.rocketmotordesign.controler.response.GraphResult;
import com.rocketmotordesign.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@RestController()
@RequestMapping("compute")
public class ComputationControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputationControler.class);

    private final JSRMService jsrmService;
    private MeasureUnitService measureUnitService;
    private Integer moduloLimitSize;
    private Integer finocylLimit;
    private Integer starlLimit;

    public ComputationControler(JSRMService jsrmService,
                                MeasureUnitService measureUnitService,
                                @Value("${computation.response.limit.size}") Integer moduloLimitSize,
                                @Value("${computation.finocyl.limit.size:400}") Integer finocylLimit,
                                @Value("${computation.star.limit.size:400}") Integer starlLimit) {
        this.jsrmService = jsrmService;
        this.measureUnitService = measureUnitService;
        this.moduloLimitSize = moduloLimitSize;
        this.finocylLimit = finocylLimit;
        this.starlLimit = starlLimit;
    }

    @PostMapping("finocyl")
    public ResponseEntity computeFinocyl(@RequestBody FinocylComputationRequest request) {
        if(request.getExtraConfig().getNumberOfCalculationLine() == null){
            request.getExtraConfig().setNumberOfCalculationLine(finocylLimit);
        }
        return computeRequest(request, true);
    }

    @PostMapping("star")
    public ResponseEntity computeStar(@RequestBody StarGrainComputationRequest request) {
        if(request.getExtraConfig().getNumberOfCalculationLine() == null){
            request.getExtraConfig().setNumberOfCalculationLine(starlLimit);
        }
        return computeRequest(request, true);
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
        return computeRequest(request, false);
    }

    @PostMapping("endburner")
    public ResponseEntity computeEndBurner(@RequestBody EndBurnerGrainComputationRequest request) {
        return computeRequest(request, true);
    }

    @PostMapping
    public ResponseEntity computeHollowCylinderGrain(@RequestBody HollowComputationRequest request) {
        return computeRequest(request, false);
    }

    private ResponseEntity computeRequest(BasicComputationRequest request, boolean removePostBurnResult) {
        try {
            ComputationResponse response = toComputationResponse(request, jsrmService.runComputation(request), removePostBurnResult);
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
                LOGGER.error("CustomPropellantChamberPressureOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getCause().getMessage()));
            } else if (e.getCause() != null && e.getCause() instanceof MotorClassificationOutOfBoundException) {
                LOGGER.error("MotorClassificationOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getMessage()));
            } else {
                LOGGER.warn("Computation failed, retry with low KN");
                LOGGER.warn("Computation failed request {} : {}", request.hashCode(), request.toString());
                return retryWithSafeKN(request);
            }
        } catch (BurnRateDataException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("METEOR can't run this computation due to the following error:", "Your burn rate data are invalid. " + e.getMessage()));
        } catch (Exception e) {
            LOGGER.error("Unknown computation error with request : " + request.toString(), e);
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknown error, please contact us."));
        }
    }

    private void logSuccess(BasicComputationRequest request, ComputationResponse response) {
        LOGGER.info("METEOR[REQUEST|{}]", request.hashCode());

        LOGGER.info("METEOR[GRAIN|{}]", request.getGrainType());
        LOGGER.info("METEOR[CLIENT-ID|{}]", request.getComputationHash());
        LOGGER.info("METEOR[UNITS|{}]", request.getMeasureUnit());
        Map<String, SolidPropellant> propellants = Stream.of(PropellantType.values())
                .collect(toMap(Enum::name, Function.identity()));
        LOGGER.info("METEOR[PROPELLANT|{}]", propellants.containsKey(request.getPropellantType()) ? request.getPropellantType() : "CUSTOM");
        LOGGER.info("METEOR[MOTORCLASS|{}]", response.getPerformanceResult().getMotorDescription().substring(0, 1));
    }

    private ResponseEntity retryWithSafeKN(BasicComputationRequest request) {
        try {
            LOGGER.warn("METEOR[safeKN]");
            ComputationResponse response = toComputationResponse(request, jsrmService.runComputation(request, true), false);
            logSuccess(request, response);
            return ResponseEntity.ok(response);
        } catch (JSRMException e) {
            LOGGER.warn("METEOR[FAILED|{}]", e.getClass().getSimpleName());
            if (e.getCause() != null && e.getCause().getCause() instanceof CustomPropellantChamberPressureOutOfBoundException) {
                LOGGER.error("CustomPropellantChamberPressureOutOfBoundException : " + request.toString(), e);
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getCause().getMessage()));
            } else if (e.getCause() != null && e.getCause() instanceof MotorClassificationOutOfBoundException) {
                LOGGER.error("MotorClassificationOutOfBoundException : " + request.toString(), e);
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

    private ComputationResponse toComputationResponse(BasicComputationRequest request, SimulationResult simulationResult, boolean removePostBurnResult) {
        MeasureUnit userUnits = request.getMeasureUnit();
        boolean allResults = request.getExtraConfig().getNumberOfCalculationLine() != null;
        return new ComputationResponse(
                measureUnitService.toPerformanceResult(simulationResult.getResult(), request.getExtraConfig().isOptimalNozzleDesign(), userUnits),
                reduceGraphResults(simulationResult, userUnits, allResults, removePostBurnResult));
    }

    private List<GraphResult> reduceGraphResults(SimulationResult result, MeasureUnit userUnits, boolean allResults, boolean removePostBurnResult) {
        AtomicInteger i = new AtomicInteger();
        return result.getMotorParameters(removePostBurnResult).stream()
                .filter(thrustResult -> allResults || (moduloLimitSize == 1 || i.getAndIncrement() % moduloLimitSize == 0))
                .map(motorParameters1 -> measureUnitService.toGraphResult(motorParameters1, userUnits))
                .collect(Collectors.toList());
    }

}
