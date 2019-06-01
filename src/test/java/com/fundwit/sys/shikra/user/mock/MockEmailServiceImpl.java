/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.mock;


import com.fundwit.sys.shikra.user.service.EmailService;

public class MockEmailServiceImpl implements EmailService {

    public MockEmailServiceImpl(){
    }

    @Override
    public void sendEmail(String recipients, String subject, String content) {
       return;
    }
}
