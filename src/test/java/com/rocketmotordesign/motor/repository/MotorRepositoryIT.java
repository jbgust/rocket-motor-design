package com.rocketmotordesign.motor.repository;

import com.rocketmotordesign.motor.entity.Motor;
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
class MotorRepositoryIT {

    @Autowired
    private MotorRepository motorRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        motorRepository.deleteAll();
    }

    @Test
    @WithUserDetails(value = "test@meteor.fr", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldListUserMotor() {
        //GIVEN
        motorRepository.save(new Motor("motor-1", "description", "json"));
        motorRepository.save(new Motor("motor-2", "description", "json"));

        User anotherUser = userRepository.findByEmail("another-user@meteor.fr").get();
        Motor motor = motorRepository.save(new Motor("motor-x", "description", "json"));
        motor.setOwner(anotherUser);
        motorRepository.save(motor);

        //WHEN
        Iterable<Motor> motors = motorRepository.findAll();

        //THEN
        assertThat(motors)
                .extracting(Motor::getName)
                .containsExactly(
                        "motor-1",
                        "motor-2"
                );
    }

    @Test
    @WithUserDetails(value = "test@meteor.fr", userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldNotSave2MotorWithSameNameByUser() {
        //GIVEN
        motorRepository.save(new Motor("motor-1", "description", "json"));
        assertThatThrownBy(() -> motorRepository.save(new Motor("motor-1", "description", "json")))
                .isInstanceOf(DataIntegrityViolationException.class);

        User anotherUser = userRepository.findByEmail("another-user@meteor.fr").get();

        Motor motor = motorRepository.save(new Motor("motor-2", "description", "json"));
        motor.setOwner(anotherUser);
        motor.setName("motor-1");
        motorRepository.save(motor);
    }
}
