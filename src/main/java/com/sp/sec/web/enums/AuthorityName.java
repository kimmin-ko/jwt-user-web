package com.sp.sec.web.enums;

import lombok.Getter;

public enum AuthorityName {
    USER("USER"),
    ADMIN("ADMIN");

    @Getter
    private final String name;

    AuthorityName(String name) {
        this.name = name;
    }
}
