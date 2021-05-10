package com.sp.sec.web.security;

import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class SecUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(EntityNotFoundException::new);

        return SecUser.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .enabled(true)
                .build();
    }

}
