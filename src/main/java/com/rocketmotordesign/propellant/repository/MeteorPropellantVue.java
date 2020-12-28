package com.rocketmotordesign.propellant.repository;

import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Projection(name = "meteorPropellantVue", types = {MeteorPropellant.class})
public interface MeteorPropellantVue {
    UUID getId();

    String getTitle();

    String getDescription();

    String getJson();
}
