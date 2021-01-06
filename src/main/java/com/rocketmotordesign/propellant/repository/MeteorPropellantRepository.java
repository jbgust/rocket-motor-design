package com.rocketmotordesign.propellant.repository;

import com.rocketmotordesign.motor.entity.Motor;
import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource(path = "propellants", excerptProjection = MeteorPropellantVue.class)
public interface MeteorPropellantRepository extends CrudRepository<MeteorPropellant, UUID> {

    @Override
    @Query("select p from MeteorPropellant p " +
            "join p.owner as o " +
            "where o.email = ?#{principal.username} " +
            "order by p.name asc")
    Iterable<MeteorPropellant> findAll();
}
