package com.sp.sec.web.security;

import com.sp.sec.web.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Getter
public class SecUser implements UserDetails {

    private final Long userId;
    private final Set<GrantedAuthority> authorities;
    private final String email;
    private final String password;
    private final boolean enabled;

    private SecUser(Long userId, Set<GrantedAuthority> authorities, String email, String password, boolean enabled) {
        this.userId = userId;
        this.authorities = authorities;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }

    public static SecUser createBy(User user) {
        return new SecUser(
                user.getId(),
                user.getGrantedAuthorities(),
                user.getEmail(),
                user.getPassword(),
                user.isEnabled()
        );
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
