package com.rocketmotordesign.admin;

public class StripeException extends Exception {

    public StripeException(Throwable cause) {
        super(cause);
    }

    public StripeException(String message) {
        super(message);
    }
}
