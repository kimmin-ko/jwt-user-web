package com.sp.sec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.entity.Authority;
import com.sp.sec.web.entity.User;
import com.sp.sec.web.properties.AuthProperties;
import com.sp.sec.web.repository.UserRepository;
import com.sp.sec.web.security.vo.RefreshToken;
import com.sp.sec.web.security.vo.Token;
import com.sp.sec.web.security.vo.UserLogin;
import com.sp.sec.web.util.AES256Util;
import com.sp.sec.web.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.sp.sec.web.util.JWTUtil.*;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTLoginFilterTest {

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthProperties authProperties;

    @Autowired
    JWTUtil jwtUtil;

    RestTemplate restTemplate = new RestTemplate();

    final String user1Email = "user1@test.com";

    URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteByEmail(user1Email);

        User user1 = User.create("user1@test.com", passwordEncoder.encode("1234"), "user1", new Authority(Authority.Role.ROLE_USER));

        userRepository.save(user1);
    }

    @Test
    @DisplayName("1. JWT 로 로그인을 시도한다.")
    void test_1() throws URISyntaxException {
        UserLogin login = UserLogin.builder()
                .username("user1@test.com")
                .password("1234")
                .type(UserLogin.Type.LOGIN)
                .build();

        HttpEntity<UserLogin> body = new HttpEntity<>(login);

        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);

        String authHeader = response.getHeaders().get(AUTH_HEADER).get(0);
        String refreshHeader = response.getHeaders().get(REFRESH_HEADER).get(0);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authHeader).isNotNull();
        assertThat(authHeader).startsWith(BEARER);
        assertThat(refreshHeader).isNotNull();
    }

    @Test
    @DisplayName("2. refresh token 으로 access token 을 재 발급한다.")
    void refresh_token_login() throws URISyntaxException {
        // given
        String token = AES256Util.encryptBy(authProperties.getSecretKey(), jwtUtil.generate(1L, Token.Type.REFRESH));

        UserLogin refreshToken = UserLogin.builder()
                .refreshToken(token)
                .type(UserLogin.Type.REFRESH)
                .build();

        // when
        HttpEntity<UserLogin> body = new HttpEntity<>(refreshToken);

        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);

        String authHeader = response.getHeaders().get(AUTH_HEADER).get(0);
        List<String> strings = response.getHeaders().get(REFRESH_HEADER);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authHeader).isNotNull();
        assertThat(authHeader).startsWith(BEARER);
        assertThat(strings).isNull();
    }

}