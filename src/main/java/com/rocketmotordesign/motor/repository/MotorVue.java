package com.rocketmotordesign.motor.repository;

import com.rocketmotordesign.motor.entity.Motor;
import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import org.springframework.data.rest.core.config.Projection;

import java.util.UUID;

@Projection(name = "motorVue", types = {Motor.class})
public interface MotorVue {
    UUID getId();

    String getName();

    String getDescription();

    String getJson();
}
