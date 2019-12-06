package com.rocketmotordesign.controler.request;

public class ExportRASPRequest {

    private String projectName;
    private HollowComputationRequest hollowComputationRequest;
    private double motorDiameter;
    private double motorLength;
    private double motorWeight;
    private String delay;
    private boolean safeKN;

    public boolean isSafeKN() {
        return safeKN;
    }

    public void setSafeKN(boolean safeKN) {
        this.safeKN = safeKN;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public HollowComputationRequest getHollowComputationRequest() {
        return hollowComputationRequest;
    }

    public void setHollowComputationRequest(HollowComputationRequest hollowComputationRequest) {
        this.hollowComputationRequest = hollowComputationRequest;
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
