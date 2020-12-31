package com.rocketmotordesign.propellant.repository;

import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MeteorPropellantRepositoryIT {

    @Autowired
    private MeteorPropellantRepository propellantRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        propellantRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value = "test@meteor.fr", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldListUserMotor() {
        //GIVEN
        propellantRepository.save(new MeteorPropellant("propellant-1", "description", "json"));
        propellantRepository.save(new MeteorPropellant("propellant-2", "description", "json"));

        User anotherUser = userRepository.findByEmail("another-user@meteor.fr").get();
        MeteorPropellant meteorPropellant = propellantRepository.save(new MeteorPropellant("propellant-x", "description", "json"));
        meteorPropellant.setOwner(anotherUser);
        propellantRepository.save(meteorPropellant);

        //WHEN
        Iterable<MeteorPropellant> propellants = propellantRepository.findAll();

        //THEN
        assertThat(propellants)
                .extracting(MeteorPropellant::getName)
                .containsExactly(
                        "propellant-1",
                        "propellant-2"
                );
    }

    @Test
    @WithUserDetails(value = "test@meteor.fr", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldNotSave2MotorWithSameNameByUser() {
        //GIVEN
        propellantRepository.save(new MeteorPropellant("propellant-4", "description", "json"));
        assertThatThrownBy(() -> propellantRepository.save(new MeteorPropellant("propellant-4", "description", "json")))
                .isInstanceOf(DataIntegrityViolationException.class);

        User anotherUser = userRepository.findByEmail("another-user@meteor.fr").get();

        MeteorPropellant propellant = propellantRepository.save(new MeteorPropellant("propellant-2", "description", "json"));
        propellant.setOwner(anotherUser);
        propellant.setName("propellant-1");
        propellantRepository.save(propellant);
    }

}
