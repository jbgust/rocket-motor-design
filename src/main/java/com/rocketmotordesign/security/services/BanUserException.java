package com.rocketmotordesign.security.services;

public class BanUserException extends RuntimeException {
    public BanUserException() {
        super("You have been banned due to inappropriate behavior.");
    }
}
