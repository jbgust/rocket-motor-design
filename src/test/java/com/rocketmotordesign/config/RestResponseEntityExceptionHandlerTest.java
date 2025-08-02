package com.rocketmotordesign.config;

import com.rocketmotordesign.security.services.BanUserException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

class RestResponseEntityExceptionHandlerTest {

    private RestResponseEntityExceptionHandler restResponseEntityExceptionHandler = new RestResponseEntityExceptionHandler();
    @Test
    void shouldReturnMessageOnUserBan() {
        // WHEN
        ResponseEntity responseEntity = restResponseEntityExceptionHandler.handleBanUser(new BanUserException());

        // THEN
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseEntity.getBody()).isEqualTo("You have been banned due to inappropriate behavior.");
    }

}
