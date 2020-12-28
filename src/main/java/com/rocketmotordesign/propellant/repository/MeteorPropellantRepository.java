package com.rocketmotordesign.propellant.repository;

import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "propellants", excerptProjection = MeteorPropellantVue.class)
public interface MeteorPropellantRepository extends CrudRepository<MeteorPropellant, UUID> {

}
