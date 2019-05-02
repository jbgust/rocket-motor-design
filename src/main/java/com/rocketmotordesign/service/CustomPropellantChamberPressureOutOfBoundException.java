package com.rocketmotordesign.service;

import com.github.jbgust.jsrm.application.exception.ChamberPressureOutOfBoundException;

public class CustomPropellantChamberPressureOutOfBoundException extends ChamberPressureOutOfBoundException {
    public CustomPropellantChamberPressureOutOfBoundException(String message) {
        super(message);
    }
}
