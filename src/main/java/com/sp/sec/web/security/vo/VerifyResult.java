package com.sp.sec.web.security.vo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VerifyResult {
    private final String userId;
    private final boolean verify;

    private VerifyResult(String userId, boolean verify) {
        this.userId = userId;
        this.verify = verify;
    }

    public static VerifyResult successful(String userId) {
        return new VerifyResult(userId, true);
    }

    public static VerifyResult failure(String userId) {
        return new VerifyResult(userId, false);
    }
}
