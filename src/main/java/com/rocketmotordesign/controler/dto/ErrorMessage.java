package com.rocketmotordesign.controler.dto;

public class ErrorMessage {
    private final String message;
    private final String detail;

    public ErrorMessage(String message, String detail) {
        this.message = message;
        this.detail = detail;
    }

    public ErrorMessage(String message) {
        this.message=message;
        this.detail=null;
    }

    public String getMessage() {
        return message;
    }

    public String getDetail() {
        return detail;
    }
}
