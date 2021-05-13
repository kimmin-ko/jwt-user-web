package com.sp.sec.web.security.vo;

public class RefreshToken extends Token {

    private RefreshToken(String token, String aesSecretKey) {
        super(token, aesSecretKey);
    }

    public static RefreshToken generate(String token, String aesSecretKey) {
        return new RefreshToken(token, aesSecretKey);
    }
}