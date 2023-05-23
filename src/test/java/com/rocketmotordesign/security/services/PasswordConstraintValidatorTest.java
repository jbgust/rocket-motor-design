package com.rocketmotordesign.security.services;

import com.rocketmotordesign.security.request.SignupRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordConstraintValidatorTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void doitIdentifierUnPasswordValide() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("toot@titi.fr");
        signupRequest.setPassword("Toto$it1");

        Set<ConstraintViolation<SignupRequest>> validate = validator.validate(signupRequest);
        assertThat(validate).hasSize(0);
    }

    @Test
    void doitIdentifierUnPasswordInvalide() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("toot@titi.fr");
        signupRequest.setPassword("oti t");

        Set<ConstraintViolation<SignupRequest>> validate = validator.validate(signupRequest);
        assertThat(validate).hasSize(1)
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("" +
                        "Password must be 8 or more characters in length.," +
                        "Password must contain 1 or more uppercase characters.," +
                        "Password must contain 1 or more digit characters.," +
                        "Password must contain 1 or more special characters.," +
                        "Password contains a whitespace character.");
    }
}
