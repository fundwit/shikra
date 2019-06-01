/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.pojo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RegisterRequestValidPasswordConfirmValidator implements ConstraintValidator<RegisterRequestValidPasswordConfirm, RegisterRequest> {

    @Override
    public void initialize(RegisterRequestValidPasswordConfirm constraintAnnotation) {
    }

    @Override
    public boolean isValid(RegisterRequest value, ConstraintValidatorContext context) {
        // null is validated by @NotNull
        if(value == null){
            return true;
        }
        if(value.getPassword() == null){
            return value.getPasswordConfirm()==null;
        }
        return value.getPassword().equals(value.getPasswordConfirm());
    }
}