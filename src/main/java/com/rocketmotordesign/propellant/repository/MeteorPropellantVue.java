package com.rocketmotordesign.propellant.repository;

import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import com.rocketmotordesign.service.MeasureUnit;
import org.springframework.data.rest.core.config.Projection;

import java.util.UUID;

@Projection(name = "meteorPropellantVue", types = {MeteorPropellant.class})
public interface MeteorPropellantVue {
    UUID getId();

    String getName();

    String getDescription();

    String getJson();

    MeasureUnit getUnit();
}
