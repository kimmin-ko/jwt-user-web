package com.sp.sec.web.security.vo;

import com.sp.sec.web.util.AES256Util;
import com.sp.sec.web.util.JWTUtil;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

import static com.sp.sec.web.util.JWTUtil.BEARER;

public class AccessToken extends Token {

    private AccessToken(String token, boolean isEncrypt) {
        super(token, isEncrypt);
    }

    public static AccessToken generateEncryptFrom(String token, String secretKey) {
        return new AccessToken(AES256Util.encryptBy(secretKey, token), true);
    }

    public static AccessToken generateFrom(String token) {
        return new AccessToken(token, false);
    }

    public static Optional<AccessToken> convert(String bearerToken, String secretKey) {
        if(Objects.isNull(bearerToken) || !bearerToken.startsWith(BEARER)) {
            return Optional.empty();
        }

        AccessToken accessToken = new AccessToken(extract(bearerToken), true);
        accessToken.decryptBy(secretKey);
        return Optional.of(accessToken);
    }

    public String getBearerToken(String secretKey) {
        if(isEncrypt()) {
            return BEARER + getToken();
        }

        return BEARER + AES256Util.encryptBy(secretKey, getToken());
    }

    private static String extract(String token) {
        Assert.notNull(token, "The token must not be null.");

        return token.substring(BEARER.length());
    }
}
