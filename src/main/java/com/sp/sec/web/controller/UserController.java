package com.sp.sec.web.controller;

import com.sp.sec.web.controller.dto.UserDto;
import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<UserDto> getUserList() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

}
