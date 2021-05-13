package com.sp.sec.web.security.vo;

import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

import static com.sp.sec.web.util.JWTUtil.BEARER;

public class AccessToken extends Token {

    private AccessToken(String token, String aesSecretKey) {
        super(token, aesSecretKey);
    }

    public static AccessToken generate(String token, String aesSecretKey) {
        return new AccessToken(token, aesSecretKey);
    }

    public static Optional<AccessToken> convert(String bearerToken, String aesSecretKey) {
        if(Objects.isNull(bearerToken) || !bearerToken.startsWith(BEARER))
            return Optional.empty();

        return Optional.of(new AccessToken(extract(bearerToken), aesSecretKey));
    }

    private static String extract(String token) {
        Assert.notNull(token, "The token must not be null.");
        return token.substring(BEARER.length());
    }
}