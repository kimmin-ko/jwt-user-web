package com.sp.sec.web.security.vo;

import com.sp.sec.web.util.AES256Util;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class Token {
    protected final String token;
    protected final String secretKey;

    protected Token(String token, String secretKey) {
        this.token = token;
        this.secretKey = secretKey;
    }

    public String getEncryptToken() {
        return AES256Util.encryptByKey(secretKey, token);
    }

    public String getDecryptToken() {
        return AES256Util.decryptByKey(secretKey, token);
    }

    public enum Type {
        ACCESS,
        REFRESH;
    }

}
