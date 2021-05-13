package com.sp.sec.web.security.vo;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
public class UserLogin {
    private String username;
    private String password;
    private RefreshToken refreshToken;
    private Type type = Type.LOGIN;

    @Builder
    private UserLogin(String username, String password, RefreshToken refreshToken, Type type) {
        this.username = username;
        this.password = password;
        this.refreshToken = refreshToken;
        this.type = type;
    }

    public boolean isLogin() {
        return this.type == Type.LOGIN;
    }

    public boolean isRefresh() {
        return this.type == Type.REFRESH;
    }

    public enum Type {
        LOGIN,
        REFRESH
    }
}
