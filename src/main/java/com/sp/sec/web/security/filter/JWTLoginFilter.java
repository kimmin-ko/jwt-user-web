package com.sp.sec.web.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.security.SecUser;
import com.sp.sec.web.security.vo.AccessToken;
import com.sp.sec.web.security.vo.RefreshToken;
import com.sp.sec.web.security.vo.UserLogin;
import com.sp.sec.web.util.JWTUtil;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.sp.sec.web.util.JWTUtil.*;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;

    public JWTLoginFilter(JWTUtil jwtUtil, ObjectMapper objectMapper, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl("/login");
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserLogin userLogin = objectMapper.readValue(request.getInputStream(), UserLogin.class);

        // refresh token
        if (userLogin.isRefresh()) {

        }

        // id password login
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userLogin.getUsername(),
                userLogin.getPassword(),
                null
        );

        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        SecUser user = (SecUser) authResult.getPrincipal();
        AccessToken accessToken = jwtUtil.generateAccessToken(user.getUserId());
        RefreshToken refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        response.addHeader(AUTH_HEADER, BEARER + accessToken.getEncryptToken());
        response.addHeader(REFRESH_HEADER, refreshToken.getEncryptToken());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        //TODO 실패하면 어떻게 처리해줄건지?
        System.out.println("failed = " + failed.getMessage());
    }

}