package com.sp.sec.web.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Getter
public class SecUser implements UserDetails {

    private final Long userId;
    private Set<SecAuthority> authorities;
    private final String email;
    private final String password;
    private final boolean enabled;

    @Builder
    public SecUser(Long userId, String email, String password, boolean enabled) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}
