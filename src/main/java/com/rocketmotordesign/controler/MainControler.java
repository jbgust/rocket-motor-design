package com.rocketmotordesign.controler;

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

//    @CrossOrigin(origins = "http://localhost:8080")
    @PostMapping("/compute")
    public ResponseEntity<ComputationResponse> compute(@RequestBody ComputationRequest request){
        JSRMResult result = new JSRMSimulation(toSolidRocketMotor(request)).run();
        return ResponseEntity.ok(toComputationResponse(result));
    }

    @GetMapping("/")
    public ResponseEntity<ComputationResponse> compute(){
        JSRMResult result = new JSRMSimulation(createMotorAsSRM_2014ExcelFile()).run();
        return ResponseEntity.ok(toComputationResponse(result));
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

    private ComputationResponse toComputationResponse(JSRMResult result) {
        return new ComputationResponse(result.getMaxThrustInNewton(), result.getTotalImpulseInNewtonSecond(), result.getSpecificImpulseInSecond(), result.getMaxChamberPressureInMPa(),
                result.getThrustTimeInSecond(), result.getAverageThrustInNewton(), result.getMotorClassification(), result.getThrustResults(), result.getNozzle());
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
