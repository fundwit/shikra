/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.pojo;

import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterRequestValidPasswordConfirm {
    String message() default "passwordConfirm and password must be equal";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}