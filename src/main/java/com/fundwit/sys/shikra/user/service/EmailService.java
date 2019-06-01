/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.service;

public interface EmailService {
    void sendEmail(String recipients, String subject, String content);
}
