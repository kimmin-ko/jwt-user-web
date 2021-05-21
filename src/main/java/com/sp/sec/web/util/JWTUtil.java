package com.sp.sec.web.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sp.sec.web.properties.AuthProperties;
import com.sp.sec.web.security.vo.Token;
import com.sp.sec.web.security.vo.VerifyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class JWTUtil {
    public static final String AUTH_HEADER = "Authentication";
    public static final String REFRESH_HEADER = "refresh-token";
    public static final String BEARER = "Bearer ";

    private final AuthProperties authProperties;
    private final Algorithm algorithm;

    public JWTUtil(AuthProperties authProperties) {
        this.authProperties = authProperties;
        this.algorithm = Algorithm.HMAC512(authProperties.getSecretSign());
    }

    public String generate(Long userId, Token.Type tokenType) {
        long lifeTime = tokenType.equals(Token.Type.ACCESS) ? authProperties.getTokenLifeTime() : authProperties.getTokenRefreshTime();

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim(CLAIM.EXPIRE.name, generateExpire(lifeTime))
                .withClaim(CLAIM.TOKEN_TYPE.name, tokenType.name())
                .sign(algorithm);
    }

    public VerifyResult verify(Token token) {
        if (token.isEncrypt()) {
            log.info("*** 암호화된 토큰이 JWTUtil.verify()로 들어왔습니다. ***");
            token.decryptBy(authProperties.getSecretKey());
        }

        try {
            DecodedJWT decode = JWT.require(algorithm).build().verify(token.getToken());
            return VerifyResult.successful(decode.getSubject());
        } catch (JWTVerificationException e) {
            DecodedJWT decode = JWT.decode(token.getToken());
            return VerifyResult.failure(decode.getSubject());
        }
    }

    private long generateExpire(long lifeTime) {
        return Instant.now().getEpochSecond() + lifeTime;
    }

    enum CLAIM {
        EXPIRE("exp"),
        TOKEN_TYPE("token_type");

        private final String name;

        CLAIM(String name) {
            this.name = name;
        }
    }
}
