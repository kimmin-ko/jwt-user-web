package com.sp.sec.web.security.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.sec.web.entity.User;
import com.sp.sec.web.repository.UserRepository;
import com.sp.sec.web.security.SecUser;
import com.sp.sec.web.security.vo.*;
import com.sp.sec.web.util.JWTUtil;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Objects;

import static com.sp.sec.web.util.JWTUtil.*;

public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    public static final String LOGIN_TYPE = "loginType";

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final String secretKey;
    private final AuthenticationManager authenticationManager;

    public JWTLoginFilter(UserRepository userRepository,
                          JWTUtil jwtUtil,
                          ObjectMapper objectMapper,
                          String secretKey,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.secretKey = secretKey;
        this.authenticationManager = authenticationManager;

        setFilterProcessesUrl("/login");
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserLogin userLogin = objectMapper.readValue(request.getInputStream(), UserLogin.class);
        request.setAttribute(LOGIN_TYPE, userLogin.getType());

        // refresh token
        if (userLogin.isRefresh()) {
            String token = userLogin.getRefreshToken();
            requiredRefreshToken(token);

            VerifyResult verify = jwtUtil.verify(RefreshToken.convert(token, secretKey));
            validExpire(verify);

            Long userId = Long.parseLong(verify.getUserId());
            User user = userRepository.findById(userId)
                    .orElseThrow(EntityNotFoundException::new);

            SecUser secUser = new SecUser(user.getId(), user.getEmail(), user.getPassword(), user.isEnabled());

            return new UsernamePasswordAuthenticationToken(secUser, null, null);
        }

        // id password login
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userLogin.getUsername(),
                userLogin.getPassword(),
                null
        );

        return authenticationManager.authenticate(authentication);
    }

    private void validExpire(VerifyResult verify) {
        if (!verify.isValid()) {
            throw new TokenExpiredException("리프레시 토큰 만료");
        }
    }

    private void requiredRefreshToken(String token) {
        if (ObjectUtils.isEmpty(token)) {
            throw new IllegalArgumentException("리프레시 토큰이 필요함: " + token);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) {
        SecUser user = (SecUser) authResult.getPrincipal();

        AccessToken accessToken = AccessToken.generateFrom(jwtUtil.generate(user.getUserId(), Token.Type.ACCESS));
        response.addHeader(AUTH_HEADER, accessToken.getBearerToken(secretKey));

        if (isLogin((UserLogin.Type) request.getAttribute(LOGIN_TYPE))) {
            RefreshToken refreshToken = RefreshToken.generateEncryptFrom(jwtUtil.generate(user.getUserId(), Token.Type.REFRESH), secretKey);
            response.addHeader(REFRESH_HEADER, refreshToken.getToken());
        }
        request.removeAttribute(LOGIN_TYPE);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) {
        //TODO: 실패하면 어떻게 처리해줄건지?
        System.out.println("failed = " + failed.getMessage());
    }

    private boolean isLogin(UserLogin.Type loginType) {
        return Objects.isNull(loginType) || loginType.equals(UserLogin.Type.LOGIN);
    }

}