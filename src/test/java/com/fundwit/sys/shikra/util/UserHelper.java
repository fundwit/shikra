package com.fundwit.sys.shikra.util;

import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.pojo.RegisterRequest;
import com.fundwit.sys.shikra.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import reactor.core.publisher.Mono;

@TestComponent
public class UserHelper {
    @Autowired
    private UserService userService;

    public Mono<User> registerUser(String username, String password) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setNickname(username);
        registerRequest.setPassword(password);
        registerRequest.setEmail(username+"@mock.mail.server");
        registerRequest.setPasswordConfirm(password);
        return userService.createUser(registerRequest);
    }
}
