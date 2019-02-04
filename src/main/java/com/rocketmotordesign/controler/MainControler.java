package com.rocketmotordesign.controler;

import com.github.jbgust.jsrm.application.JSRMConfig;
import com.github.jbgust.jsrm.application.JSRMConfigBuilder;
import com.github.jbgust.jsrm.application.JSRMSimulation;
import com.github.jbgust.jsrm.application.motor.CombustionChamber;
import com.github.jbgust.jsrm.application.motor.SolidRocketMotor;
import com.github.jbgust.jsrm.application.motor.propellant.PropellantGrain;
import com.github.jbgust.jsrm.application.result.JSRMResult;
import com.rocketmotordesign.controler.dto.ComputationRequest;
import com.rocketmotordesign.controler.dto.ComputationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.EXPOSED;
import static com.github.jbgust.jsrm.application.motor.propellant.GrainSurface.INHIBITED;
import static com.github.jbgust.jsrm.application.motor.propellant.PropellantType.KNDX;

@RestController
public class MainControler {
    private static final JSRMConfig defaultConfig = new JSRMConfigBuilder().createJSRMConfig();

//    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/compute")
    public ResponseEntity<ComputationResponse> compute(@RequestBody ComputationRequest request){
        JSRMResult result = new JSRMSimulation(toSolidRocketMotor(request)).run();
        return ResponseEntity.ok(toComputationResponse(result, defaultConfig));
    }

    @GetMapping("/")
    public ResponseEntity<ComputationResponse> compute(){
        JSRMResult result = new JSRMSimulation(createMotorAsSRM_2014ExcelFile()).run();
        return ResponseEntity.ok(toComputationResponse(result, defaultConfig));
    }

    public static SolidRocketMotor createMotorAsSRM_2014ExcelFile() {
        double grainOuterDiameter = 69d;
        double grainCoreDiameter = 20d;
        double grainSegmentLength = 115d;
        double numberOfSegment = 4d;
        PropellantGrain propellantGrain = new PropellantGrain(KNDX, grainOuterDiameter, grainCoreDiameter,
                grainSegmentLength, numberOfSegment,
                INHIBITED, EXPOSED, EXPOSED);

        double chamberInnerDiameter = 75d;
        double chamberLength = 470d;
        CombustionChamber CombustionChamber = new CombustionChamber(chamberInnerDiameter, chamberLength);

        double throatDiameter = 17.3985248919802;

        return new SolidRocketMotor(propellantGrain, CombustionChamber, throatDiameter);
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
