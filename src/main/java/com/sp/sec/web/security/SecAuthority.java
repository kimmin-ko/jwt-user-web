package com.sp.sec.web.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class SecAuthority implements GrantedAuthority {

    private String authority;

}
