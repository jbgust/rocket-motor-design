package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.exception.InvalidMotorDesignException;
import com.github.jbgust.jsrm.application.exception.JSRMException;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ComputationResponse;
import com.rocketmotordesign.controler.dto.ErrorMessage;
import com.rocketmotordesign.controler.dto.ExtraConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainControler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainControler.class);

    @Value("${computation.response.limit.size}")
    private Integer moduloLimitSize;

    @PostMapping("/compute")
    public ResponseEntity compute(@RequestBody ComputationRequest request){
        try {
            LOGGER.info("METEOR[REQUEST|{}]", request.hashCode());
            JSRMConfig config = toJSRMConfig(request.getExtraConfig());
            JSRMResult result = new JSRMSimulation(toSolidRocketMotor(request)).run(config);
            return ResponseEntity.ok(toComputationResponse(result, config));
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

    private JSRMConfig toJSRMConfig(ExtraConfiguration extraConfig) {
        JSRMConfigBuilder jsrmConfigBuilder = new JSRMConfigBuilder()
                .withAmbiantPressureInMPa(extraConfig.getAmbiantPressureInMPa())
                .withCombustionEfficiencyRatio(extraConfig.getCombustionEfficiencyRatio())
                .withDensityRatio(extraConfig.getDensityRatio())
                .withErosiveBurningAreaRatioThreshold(extraConfig.getErosiveBurningAreaRatioThreshold())
                .withErosiveBurningVelocityCoefficient(extraConfig.getErosiveBurningVelocityCoefficient())
                .withNozzleEfficiency(extraConfig.getNozzleEfficiency())
                .withNozzleErosionInMillimeter(extraConfig.getNozzleErosionInMillimeter())
                .withOptimalNozzleDesign(extraConfig.isOptimalNozzleDesign());

                if(extraConfig.getNozzleExpansionRatio() != null){
                 jsrmConfigBuilder.withNozzleExpansionRatio(extraConfig.getNozzleExpansionRatio());
                }

        return jsrmConfigBuilder.createJSRMConfig();
    }

    private ComputationResponse toComputationResponse(JSRMResult result, JSRMConfig config) {
        return new ComputationResponse(result, config, moduloLimitSize);
    }

    private SolidRocketMotor toSolidRocketMotor(ComputationRequest request) {
        return new SolidRocketMotor(
                toPropellantGrain(request),
                toCombustionChamber(request),
                request.getThroatDiameter()
        );
    }

    private CombustionChamber toCombustionChamber(ComputationRequest request) {
        return new CombustionChamber(request.getChamberInnerDiameter(), request.getChamberLength());
    }

    private PropellantGrain toPropellantGrain(ComputationRequest request) {
        return new PropellantGrain(request.getPropellantType(), request.getOuterDiameter(), request.getCoreDiameter(), request.getSegmentLength(),request.getNumberOfSegment(), request.getOuterSurface(), request.getEndsSurface(), request.getCoreSurface());
    }
}
