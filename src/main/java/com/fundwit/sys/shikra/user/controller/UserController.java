package com.fundwit.sys.shikra.user.controller;

import com.fundwit.sys.shikra.authentication.LoginUser;
import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.pojo.RegisterRequest;
import com.fundwit.sys.shikra.user.pojo.UserAccountInfo;
import com.fundwit.sys.shikra.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/self")
    public Mono<User> getUserSelf(){
        return ReactiveSecurityContextHolder.getContext()
                .map(sc-> sc!=null?sc.getAuthentication():null)
                .map(authentication -> {
                    if(authentication==null){
                        throw new AuthenticationServiceException("unauthenticated");
                    }
                    return ((LoginUser)authentication.getPrincipal()).getId();
                }).map(uid->userService.findUser(uid).block());
    }

    @GetMapping("/{userId}")
    public Mono<User> getUser(@PathVariable @NotEmpty Long userId){
        return userService.findUser(userId);
    }

    @GetMapping
    public Flux<User> listUsers(){
        return userService.listUser();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody RegisterRequest info) {
        return userService.createUser(info);
    }

    @PutMapping("/{userId}")
    public Mono<User> updateUser(@PathVariable Long userId, @RequestBody UserAccountInfo info) {
        return userService.updateUser(userId, info);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable Long userId){
        return userService.deleteUser(userId);
    }
}
