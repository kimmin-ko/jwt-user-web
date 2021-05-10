package com.sp.sec.web.security.vo;

import lombok.*;

@Data
@NoArgsConstructor
public class UserLogin {
    private String username;
    private String password;

    @Builder
    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }
}