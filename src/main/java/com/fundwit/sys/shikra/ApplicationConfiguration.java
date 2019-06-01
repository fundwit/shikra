package com.fundwit.sys.shikra;

import com.fundwit.sys.shikra.user.service.CaptchaService;
import com.fundwit.sys.shikra.user.service.CaptchaServiceImpl;
import com.fundwit.sys.shikra.user.service.EmailService;
import com.fundwit.sys.shikra.user.service.EmailServiceImpl;
import com.fundwit.sys.shikra.util.IdWorker;
import com.google.common.collect.ImmutableMap;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public IdWorker idWorker(){
        // 0:  1bit 符号位，不用
        // timestamp: 41bit, 69年
        // workerId
        //    dataCenterId 5bit
        //    workerId  5bit
        // sequence: 12bit 4095

        return new IdWorker(0, 0, 0);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new DelegatingPasswordEncoder("bcrypt", ImmutableMap.of("bcrypt", new BCryptPasswordEncoder()));
    }

    @Bean
    public CaptchaService captchaService(CacheManager cacheManager){
        return new CaptchaServiceImpl(cacheManager);
    }
    @Bean
    public EmailService emailService(JavaMailSender mailSender) {
        return new EmailServiceImpl(mailSender);
    }
}
