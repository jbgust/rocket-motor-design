package com.rocketmotordesign.motor.repository;

import com.rocketmotordesign.motor.entity.Motor;
import com.rocketmotordesign.propellant.repository.MeteorPropellantVue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.UUID;

@RepositoryRestResource(excerptProjection = MotorVue.class)
public interface MotorRepository extends CrudRepository<Motor, UUID> {

    @Override
    @Query("select m from Motor m " +
            "join m.owner as o " +
            "where o.email = ?#{principal.username}")
    Iterable<Motor> findAll();
}
