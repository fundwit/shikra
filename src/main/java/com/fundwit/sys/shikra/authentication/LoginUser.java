/*
 Copyright (c) 2008-2019. Fundwit All Rights Reserved.
 */

package com.fundwit.sys.shikra.authentication;

import java.io.Serializable;

public class LoginUser implements Serializable {
    private Long id;
    private String username;
    private String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
