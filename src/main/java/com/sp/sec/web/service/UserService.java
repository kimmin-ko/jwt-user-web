package com.sp.sec.web.service;

import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByIdWithAuthorities(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        // Authorities LAZY Loading
        user.getGrantedAuthorities();

        return Optional.of(user);
    }

}
