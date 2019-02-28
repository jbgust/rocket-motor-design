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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainControler {

    @PostMapping("/compute")
    public ResponseEntity compute(@RequestBody ComputationRequest request){
        try {
            JSRMConfig config = toJSRMConfig(request.getExtraConfig());
            JSRMResult result = new JSRMSimulation(toSolidRocketMotor(request)).run(config);
            return ResponseEntity.ok(toComputationResponse(result, config));
        } catch (JSRMException e) {
            if(e instanceof InvalidMotorDesignException){
                return ResponseEntity.badRequest().body(
                        new ErrorMessage(e.getMessage()));
            } else {
                return ResponseEntity.badRequest().body(
                        new ErrorMessage("METEOR can't run this computation due to the following error:", e.getCause().getMessage()));
            }
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ErrorMessage("Computation failed due to unknow error, please contact us."));
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
        return new ComputationResponse(result, config);
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
