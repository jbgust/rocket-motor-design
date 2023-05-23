package com.rocketmotordesign.security.services;

import com.google.common.base.Joiner;
import org.passay.*;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	    @Override
	    public void initialize(ValidPassword arg0) {
	    }

	    @Override
	    public boolean isValid(String password, ConstraintValidatorContext context) {
	    	if(password == null) {
	    		return false;
			}
	        PasswordValidator validator = new PasswordValidator(Arrays.asList(
	           		new LengthRule(8, 30),
					new CharacterRule(EnglishCharacterData.UpperCase, 1),
					new CharacterRule(EnglishCharacterData.LowerCase, 1),
					new CharacterRule(EnglishCharacterData.Digit, 1),
					new CharacterRule(EnglishCharacterData.Special, 1),
	           		new WhitespaceRule()));

	        RuleResult result = validator.validate(new PasswordData(password));
	        if (result.isValid()) {
	            return true;
	        }
	        context.disableDefaultConstraintViolation();
	        context.buildConstraintViolationWithTemplate(
	          Joiner.on(",").join(validator.getMessages(result)))
	          .addConstraintViolation();
	        return false;
	    }
	}
