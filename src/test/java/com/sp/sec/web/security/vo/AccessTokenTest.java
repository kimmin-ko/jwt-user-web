package com.sp.sec.web.security.vo;

import com.sp.sec.web.properties.AuthProperties;
import com.sp.sec.web.util.AES256Util;
import com.sp.sec.web.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccessTokenTest {

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    AuthProperties authProperties;

    String token;
    String secretKey;
    AccessToken accessToken;
    AccessToken encryptAccessToken;

    @BeforeEach
    void setUp() {
        token = jwtUtil.generate(1L, Token.Type.ACCESS);
        secretKey = authProperties.getSecretKey();

        accessToken = AccessToken.generateFrom(token);
        assertThat(accessToken.getToken()).isEqualTo(token);
        assertThat(accessToken.isEncrypt()).isFalse();

        encryptAccessToken = AccessToken.generateEncryptFrom(token, secretKey);
        assertThat(encryptAccessToken.getToken()).isNotEqualTo(token);
        assertThat(encryptAccessToken.isEncrypt()).isTrue();
    }

    @Test
    @DisplayName("토큰 복호화 하기")
    void decrypt_access_token() {
        // given
        // when
        encryptAccessToken.decryptBy(secretKey);

        // then
        assertThat(encryptAccessToken.getToken()).isEqualTo(token);
        assertThat(encryptAccessToken.isEncrypt()).isFalse();
    }

    @Test
    @DisplayName("암호화된 Bearer JWT 를 Access Token 으로 변환하기")
    void convert_bearer_jwt_to_access_token() {
        // given
        String bearerToken = JWTUtil.BEARER + AES256Util.encryptBy(secretKey, token);

        // when
        AccessToken convertAccessToken = AccessToken.convert(bearerToken, secretKey).get();

        // then
        assertThat(convertAccessToken.getToken()).isEqualTo(token);
        assertThat(convertAccessToken.isEncrypt()).isFalse();
    }

}

