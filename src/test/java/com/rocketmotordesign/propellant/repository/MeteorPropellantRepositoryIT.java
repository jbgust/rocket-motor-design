package com.rocketmotordesign.propellant.repository;

import com.rocketmotordesign.propellant.entity.MeteorPropellant;
import com.rocketmotordesign.security.models.User;
import com.rocketmotordesign.security.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.rocketmotordesign.service.MeasureUnit.SI;
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

    @Disabled("Due to fail because of H2 upgrade")
    @Test
    @WithUserDetails(value = "test@meteor.fr", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldListUserMotor() {
        //GIVEN
        propellantRepository.save(new MeteorPropellant("propellant-1", "description", "json", SI));
        propellantRepository.save(new MeteorPropellant("propellant-2", "description", "json", SI));

        User anotherUser = userRepository.findByEmail("another-user@meteor.fr").get();
        MeteorPropellant meteorPropellant = propellantRepository.save(new MeteorPropellant("propellant-x", "description", "json", SI));
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

    @Disabled("Due to fail because of H2 upgrade")
    @Test
    @WithUserDetails(value = "test@meteor.fr", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldNotSave2MotorWithSameNameByUser() {
        //GIVEN
        propellantRepository.save(new MeteorPropellant("propellant-4", "description", "json", SI));
        assertThatThrownBy(() -> propellantRepository.save(new MeteorPropellant("propellant-4", "description", "json", SI)))
                .isInstanceOf(DataIntegrityViolationException.class);

        User anotherUser = userRepository.findByEmail("another-user@meteor.fr").get();

        MeteorPropellant propellant = propellantRepository.save(new MeteorPropellant("propellant-2", "description", "json", SI));
        propellant.setOwner(anotherUser);
        propellant.setName("propellant-1");
        propellantRepository.save(propellant);
    }

}
