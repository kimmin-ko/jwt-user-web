package com.sp.sec.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.String.format;

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

        User admin = User.builder()
                .name("admin")
                .email("admin@test.com")
                .password(passwordEncoder.encode("1234"))
                .build();

        userRepository.save(user1);
        userRepository.save(admin);
    }

}