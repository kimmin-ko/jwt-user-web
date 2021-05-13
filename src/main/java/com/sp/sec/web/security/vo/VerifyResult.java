package com.sp.sec.web.security.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public class VerifyResult {
    private final String userId;
    private final boolean valid;

    private VerifyResult(String userId, boolean valid) {
        this.userId = userId;
        this.valid = valid;
    }

    public static VerifyResult successful(String userId) {
        return new VerifyResult(userId, true);
    }

    public static VerifyResult failure(String userId) {
        return new VerifyResult(userId, false);
    }
}
