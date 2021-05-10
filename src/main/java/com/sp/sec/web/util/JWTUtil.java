package com.sp.sec.web.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sp.sec.web.security.vo.VerifyResult;

import java.time.Instant;

public class JWTUtil {

    public static final String AUTHENTICATION = "Authentication";
    public static final String BEARER = "Bearer ";

    private static final String SECRET = "hello";
    private static final Algorithm ALGORITHM = Algorithm.HMAC512(SECRET);
    private static final long LIFE_TIME = 30;

    public String generate(String userId) {
        return JWT.create()
                .withSubject(userId)
                .withClaim(CLAIM.EXPRIE.name, generateExpire())
                .sign(ALGORITHM);
    }

    public VerifyResult verify(String token) {
        try {
            DecodedJWT decode = JWT.require(ALGORITHM).build().verify(token);
            return VerifyResult.successful(decode.getSubject());
        } catch (JWTVerificationException e) {
            DecodedJWT decode = JWT.decode(token);
            return VerifyResult.failure(decode.getSubject());
        }
    }

    private long generateExpire() {
        return Instant.now().getEpochSecond() + LIFE_TIME;
    }

    enum CLAIM {
        EXPRIE("exp");

        private final String name;

        CLAIM(String name) {
            this.name = name;
        }
    }
}
