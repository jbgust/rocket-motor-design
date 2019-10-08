package com.rocketmotordesign.controler.request;

public class ExportRASPRequest {

    private String projectName;
    private ComputationRequest computationRequest;
    private double motorDiameter;
    private double motorLength;
    private double motorWeight;
    private String delay;

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public ComputationRequest getComputationRequest() {
        return computationRequest;
    }

    public void setComputationRequest(ComputationRequest computationRequest) {
        this.computationRequest = computationRequest;
    }

    public double getMotorDiameter() {
        return motorDiameter;
    }

    public void setMotorDiameter(double motorDiameter) {
        this.motorDiameter = motorDiameter;
    }

    public double getMotorLength() {
        return motorLength;
    }

    public void setMotorLength(double motorLength) {
        this.motorLength = motorLength;
    }

    public double getMotorWeight() {
        return motorWeight;
    }

    public void setMotorWeight(double motorWeight) {
        this.motorWeight = motorWeight;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
