/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.service;


import com.fundwit.sys.shikra.exception.EmailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    private String emailFrom;


    public EmailServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
        this.emailFrom = "xracoon@qq.com";
    }

    @Override
    public void sendEmail(String recipients, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailFrom);
            helper.setTo(recipients.split(",;\\s+"));
            helper.setSubject(subject);
            helper.setText(content);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailSendException("邮件发送失败", e);
        }
    }
}
