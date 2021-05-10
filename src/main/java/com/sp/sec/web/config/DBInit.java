package com.sp.sec.web.config;

import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class DBInit {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        User user1 = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("1234"))
                .name("user")
                .build();

        userRepository.save(user1);
    }

}
