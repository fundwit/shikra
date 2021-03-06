/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user;

import com.fundwit.sys.shikra.user.mock.MockCaptchaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestPropertySource;

@Configuration
@TestPropertySource(properties = {"application.test=true"})
public class TestConfiguration {
    @Bean
    @Primary
    public MockCaptchaServiceImpl mockCaptchaService(){
        return new MockCaptchaServiceImpl();
    }
}
