package com.rocketmotordesign.security.jwt;

public class MeteorTokenPayload {
    private final boolean donator;

    public MeteorTokenPayload(boolean donator) {
        this.donator = donator;
    }

    public boolean isDonator() {
        return donator;
    }
}
