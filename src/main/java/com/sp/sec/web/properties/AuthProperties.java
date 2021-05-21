package com.sp.sec.web.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private JWT jwt;
    private Token token;

    public String getSecretSign() {
        return this.jwt.secretSign;
    }

    public Long getTokenLifeTime() {
        return this.jwt.tokenLifeTime;
    }

    public Long getTokenRefreshTime() {
        return this.jwt.tokenRefreshTime;
    }

    public String getSecretKey() {
        return this.token.secretKey;
    }

    @Data
    static class JWT {
        private String secretSign;
        private Long tokenLifeTime;
        private Long tokenRefreshTime;
    }

    @Data
    static class Token {
        private String secretKey;
    }
}