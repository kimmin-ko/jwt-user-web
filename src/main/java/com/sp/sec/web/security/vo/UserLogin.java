package com.sp.sec.web.security.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class UserLogin {
    private String username;
    private String password;
    private String refreshToken;
    private Type type;

    @Builder
    private UserLogin(String username, String password, String refreshToken, Type type) {
        this.username = username;
        this.password = password;
        this.refreshToken = refreshToken;
        this.type = type;
    }

    public boolean isRefresh() {
        return this.type == Type.REFRESH;
    }

    public enum Type {
        LOGIN,
        REFRESH
    }
}
