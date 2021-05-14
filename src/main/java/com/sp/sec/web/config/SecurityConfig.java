package com.sp.sec.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.properties.AuthProperties;
import com.sp.sec.web.security.filter.JWTCheckFilter;
import com.sp.sec.web.security.filter.JWTLoginFilter;
import com.sp.sec.web.service.UserService;
import com.sp.sec.web.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AuthProperties authProperties;

    private final UserService userService;
    private final UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JWTLoginFilter loginFilter = new JWTLoginFilter(jwtUtil, objectMapper, authProperties.getAesSecretKey(), authenticationManager());
        JWTCheckFilter checkFilter = new JWTCheckFilter(authenticationManager(), jwtUtil, userService, authProperties);

        http
                .csrf().disable()
                .addFilter(loginFilter)
                .addFilter(checkFilter);
    }
}
