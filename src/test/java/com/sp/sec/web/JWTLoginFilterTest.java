package com.sp.sec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import com.sp.sec.web.security.vo.UserLogin;
import com.sp.sec.web.util.JWTUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
import static java.lang.String.*;
import static org.assertj.core.api.Assertions.*;

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

    RestTemplate restTemplate = new RestTemplate();

    URI uri(String path) throws URISyntaxException {
        return new URI(format("http://localhost:%d%s", port, path));
    }

    @BeforeEach
    void setUp() {
        User user1 = User.builder()
                .name("user1")
                .email("user1@test.com")
                .password(passwordEncoder.encode("1234"))
                .build();

        userRepository.save(user1);
    }

    @Test
    @DisplayName("1. JWT 로 로그인을 시도한다.")
    void test_1() throws URISyntaxException {

        UserLogin login = UserLogin.builder()
                .username("user1@test.com")
                .password("1234")
                .build();

        HttpEntity<UserLogin> body = new HttpEntity<>(login);

        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);

        List<String> headers = response.getHeaders().get(AUTHENTICATION);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(headers).isNotNull();
        assertThat(headers.get(0)).startsWith(BEARER);
    }

}