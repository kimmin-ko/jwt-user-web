package com.sp.sec.web.security.vo;

import com.sp.sec.web.util.AES256Util;
import org.springframework.util.Assert;

public class RefreshToken extends Token {

    private RefreshToken(String token, boolean isEncrypt) {
        super(token, isEncrypt);
    }

    public static RefreshToken generateEncryptFrom(String token, String secretKey) {
        return new RefreshToken(AES256Util.encryptBy(secretKey, token), true);
    }

    public static RefreshToken generateFrom(String token) {
        return new RefreshToken(token, false);
    }

    public static RefreshToken convert(String encryptToken, String secretKey) {
        Assert.notNull(encryptToken, "The encrypt token must not be null.");
        Assert.notNull(secretKey, "The secret key must not be null.");

        return new RefreshToken(AES256Util.decryptBy(secretKey, encryptToken), false);
    }
}