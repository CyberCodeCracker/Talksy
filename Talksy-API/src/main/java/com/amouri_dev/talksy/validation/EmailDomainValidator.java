package com.amouri_dev.talksy.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailDomainValidator implements ConstraintValidator<NonDisposableEmail, String> {



    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return false;
    }
}
