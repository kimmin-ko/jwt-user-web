package com.sp.sec.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.controller.dto.UserDto;
import com.sp.sec.web.entity.Authority;
import com.sp.sec.web.entity.User;
import com.sp.sec.web.properties.AuthProperties;
import com.sp.sec.web.repository.UserRepository;
import com.sp.sec.web.security.vo.AccessToken;
import com.sp.sec.web.security.vo.UserLogin;
import com.sp.sec.web.util.RestResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sp.sec.web.util.JWTUtil.AUTH_HEADER;
import static com.sp.sec.web.util.JWTUtil.BEARER;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {

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

    RestTemplate restTemplate = new RestTemplate();

    final String user1Email = "user1@test.com";
    final String adminEmail = "admin@test.com";

    URI uri(String path) {
        try {
            return new URI(format("http://localhost:%d%s", port, path));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    AccessToken getToken(String username, String password) {
        UserLogin login = UserLogin.builder()
                .username(username)
                .password(password)
                .build();

        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);

        return AccessToken.convert(response.getHeaders().get(AUTH_HEADER).get(0), authProperties.getSecretKey()).get();
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteByEmail(user1Email);
        userRepository.deleteByEmail(adminEmail);

        User user1 = User.create(user1Email, passwordEncoder.encode("1234"), "user1", new Authority(Authority.Role.ROLE_USER));
        User admin = User.create(adminEmail, passwordEncoder.encode("1234"), "admin", new Authority(Authority.Role.ROLE_ADMIN));

        userRepository.save(user1);
        userRepository.save(admin);
    }

    @Test
    @DisplayName("1. admin 유저는 userList 를 가져올 수 있다.")
    void test() throws JsonProcessingException {
        AccessToken accessToken = getToken(adminEmail, "1234");

        ResponseEntity<String> response = restTemplate.exchange(
                uri("/api/users"),
                HttpMethod.GET,
                getTokenEntity(accessToken),
                String.class
        );

        String content = "{\"content\": " + response.getBody() + ", "
                + "\"number\": " + 0 + ", "
                + "\"size\": " + 10 + ", "
                + "\"totalElements\": " + 2
                + "}";

        RestResponsePage<UserDto> page = objectMapper.readValue(content, new TypeReference<RestResponsePage<UserDto>>() {
        });

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().stream()
                .map(UserDto::getName)
                .collect(Collectors.toSet())
                .containsAll(Set.of("user1", "admin"))).isTrue();
    }

    private HttpEntity<Void> getTokenEntity(AccessToken token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER, token.getBearerToken(authProperties.getSecretKey()));
        return new HttpEntity<>(headers);
    }

}