package com.sp.sec.web.security.vo;

import com.sp.sec.web.util.AES256Util;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

@Getter
@ToString
public abstract class Token {
    private String token;
    private boolean isEncrypt;

    protected Token(String token, boolean isEncrypt) {
        this.token = token;
        this.isEncrypt = isEncrypt;
    }

    public void encryptBy(String secretKey) {
        Assert.notNull(secretKey, "The aes secret key must not be null.");

        if (!isEncrypt) {
            isEncrypt = true;
            token = AES256Util.encryptBy(secretKey, token);
        }
    }

    public void decryptBy(String secretKey) {
        Assert.notNull(secretKey, "The aes secret key must not be null.");

        if (isEncrypt) {
            isEncrypt = false;
            token = AES256Util.decryptBy(secretKey, token);
        }
    }

    public enum Type {
        ACCESS,
        REFRESH;
    }

}