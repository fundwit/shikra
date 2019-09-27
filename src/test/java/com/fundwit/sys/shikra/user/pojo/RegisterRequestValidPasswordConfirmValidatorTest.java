package com.fundwit.sys.shikra.user.pojo;

import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class RegisterRequestValidPasswordConfirmValidatorTest {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void test(){
        RegisterRequestValidPasswordConfirmValidator validPasswordConfirmValidator = new RegisterRequestValidPasswordConfirmValidator();
        assertEquals(true, validPasswordConfirmValidator.isValid(null, null));

        RegisterRequest request = new RegisterRequest();
        // null, null
        assertEquals(true, validPasswordConfirmValidator.isValid(request, null));

        request.setPassword("password");
        request.setPasswordConfirm("password");
        assertEquals(true, validPasswordConfirmValidator.isValid(request, null));

        request.setPassword("password");
        request.setPasswordConfirm("otherPassword");
        assertEquals(false, validPasswordConfirmValidator.isValid(request, null));

        request.setPassword(null);
        request.setPasswordConfirm("otherPassword");
        assertEquals(false, validPasswordConfirmValidator.isValid(request, null));

        request.setPassword("password");
        request.setPasswordConfirm(null);
        assertEquals(false, validPasswordConfirmValidator.isValid(request, null));
    }

    @Test
    public void testValidatePass() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test-user");
        request.setNickname("Test-User");
        request.setEmail("test-user@test.com");
        request.setVerifyCode("1111");

        request.setPassword("password");
        request.setPasswordConfirm("password");

        Set<ConstraintViolation<RegisterRequest>> result = validator.validate(request);
        assertEquals(0, result.size());
    }

    @Test
    public void testValidateNotPass() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("test-user");
        request.setNickname("Test-User");
        request.setEmail("test-user@test.com");
        request.setVerifyCode("1111");

        request.setPassword("password");
        request.setPasswordConfirm("otherPassword");

        Set<ConstraintViolation<RegisterRequest>> result = validator.validate(request);
        assertEquals(1, result.size());
        assertEquals("passwordConfirm and password must be equal", result.iterator().next().getMessage());
    }
}