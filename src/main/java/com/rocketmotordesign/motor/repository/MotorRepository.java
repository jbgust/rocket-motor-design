package com.rocketmotordesign.motor.repository;

import com.rocketmotordesign.motor.entity.Motor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(excerptProjection = MotorVue.class)
public interface MotorRepository extends CrudRepository<Motor, UUID> {

}
