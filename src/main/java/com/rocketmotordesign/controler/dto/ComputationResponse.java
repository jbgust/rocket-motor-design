package com.rocketmotordesign.controler.dto;

import com.github.jbgust.jsrm.application.result.MotorClassification;
import com.github.jbgust.jsrm.application.result.Nozzle;
import com.github.jbgust.jsrm.application.result.ThrustResult;

import java.util.List;

public class ComputationResponse {

    private final double maxThrustInNewton;
    private final double totalImpulseInNewtonSecond;
    private final double specificImpulseInSecond;
    private final double maxChamberPressureInMPa;
    private final double thrustTimeInSecond;
    private final long averageThrustInNewton;
    private final MotorClassification motorClassification;
    private final List<ThrustResult> thrustResults;
    private final Nozzle nozzle;

    public ComputationResponse(double maxThrustInNewton, double totalImpulseInNewtonSecond, double specificImpulseInSecond, double maxChamberPressureInMPa, double thrustTimeInSecond, long averageThrustInNewton, MotorClassification motorClassification, List<ThrustResult> thrustResults, Nozzle nozzle) {
        this.maxThrustInNewton = maxThrustInNewton;
        this.totalImpulseInNewtonSecond = totalImpulseInNewtonSecond;
        this.specificImpulseInSecond = specificImpulseInSecond;
        this.maxChamberPressureInMPa = maxChamberPressureInMPa;
        this.thrustTimeInSecond = thrustTimeInSecond;
        this.averageThrustInNewton = averageThrustInNewton;
        this.motorClassification = motorClassification;
        this.thrustResults = thrustResults;
        this.nozzle = nozzle;
    }

    public double getMaxThrustInNewton() {
        return maxThrustInNewton;
    }

    public double getTotalImpulseInNewtonSecond() {
        return totalImpulseInNewtonSecond;
    }

    public double getSpecificImpulseInSecond() {
        return specificImpulseInSecond;
    }

    public double getMaxChamberPressureInMPa() {
        return maxChamberPressureInMPa;
    }

    public double getThrustTimeInSecond() {
        return thrustTimeInSecond;
    }

    public long getAverageThrustInNewton() {
        return averageThrustInNewton;
    }

    public MotorClassification getMotorClassification() {
        return motorClassification;
    }

    public List<ThrustResult> getThrustResults() {
        return thrustResults;
    }

    public Nozzle getNozzle() {
        return nozzle;
    }
}
