package com.rocketmotordesign.config;

import com.rocketmotordesign.security.services.BanUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
  extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    protected ResponseEntity handleConflict(DataIntegrityViolationException exception) {
        logger.warn("DataIntegrityViolationException", exception);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(value = NullPointerException.class)
    protected ResponseEntity handleConflict(NullPointerException exception) {
        if(exception.getMessage().contains("Error processing request"))
        {
            logger.warn("Error processing request", exception);
        } else {
            logger.error("Nul pointer exception", exception);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @ExceptionHandler(value = BanUserException.class)
    protected ResponseEntity handleBanUser(BanUserException exception) {
        logger.info("BanUserException", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exception.getMessage());
    }
}
