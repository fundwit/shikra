/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.user.service;

import com.fundwit.sys.shikra.authentication.LoginUser;
import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.pojo.RegisterRequest;
import com.fundwit.sys.shikra.user.pojo.UserAccountInfo;
import org.springframework.security.core.AuthenticationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Mono<LoginUser> authenticate(String username, String password) throws AuthenticationException;

    Mono<User> createUser(RegisterRequest registerRequest);

    Mono<User> findUser(Long userId);
    Flux<User> listUser();
    Mono<User> updateUser(Long userId, UserAccountInfo userInfo);
    Mono<Void> deleteUser(Long userId);
}
