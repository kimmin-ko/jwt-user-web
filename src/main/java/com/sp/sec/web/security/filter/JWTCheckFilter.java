package com.sp.sec.web.security.filter;

import com.sp.sec.web.entity.User;
import com.sp.sec.web.properties.AuthProperties;
import com.sp.sec.web.security.SecUser;
import com.sp.sec.web.security.vo.AccessToken;
import com.sp.sec.web.security.vo.VerifyResult;
import com.sp.sec.web.service.UserService;
import com.sp.sec.web.util.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static com.sp.sec.web.util.JWTUtil.AUTH_HEADER;

public class JWTCheckFilter extends BasicAuthenticationFilter {

    private final JWTUtil jwtUtil;
    private final UserService userService;
    private final AuthProperties authProperties;

    public JWTCheckFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, UserService userService, AuthProperties authProperties) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authProperties = authProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        Optional<AccessToken> accessToken = AccessToken.convert(request.getHeader(AUTH_HEADER), authProperties.getSecretKey());

        if (accessToken.isEmpty()) {
            chain.doFilter(request, response);
            return;
        }

        VerifyResult verify = jwtUtil.verify(accessToken.get());

        if (verify.isValid()) {
            //TODO : user caching
            User user = userService.findByIdWithAuthorities(Long.parseLong(verify.getUserId()))
                    .orElseThrow(EntityNotFoundException::new);

            SecUser principle = SecUser.createFrom(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principle,
                    null,
                    principle.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }

}