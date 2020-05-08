package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.request.ExportRASPRequest;
import com.rocketmotordesign.controler.response.ErrorMessage;
import com.rocketmotordesign.service.JSRMService;
import com.rocketmotordesign.service.MeasureUnit;
import com.rocketmotordesign.service.MeasureUnitService;
import com.rocketmotordesign.service.ResultService;
import com.rocketmotordesign.service.SimulationResult;
import com.rocketmotordesign.service.UnauthorizedValueException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tec.units.ri.unit.Units;

import java.util.concurrent.atomic.AtomicInteger;

import static com.rocketmotordesign.controler.response.GraphResult.toFrontendPrecision;
import static com.rocketmotordesign.controler.response.PerformanceResult.format;

@RestController()
@RequestMapping("export")
public class ExportControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportControler.class);

    private final JSRMService jsrmService;
    private final MeasureUnitService measureUnitService;
    private final ResultService resultService;

    private Integer moduloLimitSize;

    public ExportControler(JSRMService jsrmService,
                           MeasureUnitService measureUnitService,
                           ResultService resultService, @Value("${computation.response.limit.size}") Integer moduloLimitSize) {
        this.jsrmService = jsrmService;
        this.measureUnitService = measureUnitService;
        this.resultService = resultService;
        this.moduloLimitSize = moduloLimitSize;
    }

    @PostMapping(value = "/rasp")
    public ResponseEntity exportRASP(@RequestBody ExportRASPRequest request) {
        SimulationResult jsrmResult = null;
        try {
            jsrmResult = jsrmService.runComputation(request.getComputationRequest(), request.isSafeKN());
            StringBuilder response = new StringBuilder();
            MeasureUnit requestUnits = request.getComputationRequest().getMeasureUnit();
            String motorClass = jsrmResult.getResult().getMotorClassification() + String.valueOf(jsrmResult.getResult().getAverageThrustInNewton());
            response
                    .append(motorClass).append(" ")
                    .append(format(measureUnitService.convertLengthToJSRM(requestUnits.getLenghtUnit(), request.getMotorDiameter()), "%.1f")).append(" ")
                    .append(format(measureUnitService.convertLengthToJSRM(requestUnits.getLenghtUnit(), request.getMotorLength()), "%.1f")).append(" ")
                    .append(request.getDelay()).append(" ")
                    .append(format(jsrmResult.getResult().getGrainMassInKg(), "%.3f")).append(" ")
                    .append(format(measureUnitService.convertMass(requestUnits.getMassUnit(), Units.KILOGRAM, request.getMotorWeight()), "%.3f")).append(" ")
                    .append("METEOR");


            resultService.reduceGraphResults(jsrmResult, requestUnits, request.getComputationRequest())
                    .forEach(motorParameters -> response
                            .append("\n    ")
                            .append(toFrontendPrecision(motorParameters.getX()))
                            .append(" ").append(toFrontendPrecision(motorParameters.getY())));

            LOGGER.warn("METEOR[EXPORT]");
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=meteor_" + request.getProjectName() + "_" + motorClass + ".eng")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(response.toString());
        } catch (UnauthorizedValueException e) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("METEOR can't run this computation due to the following error:",
                            e.getMessage()));
        }
    }

}
