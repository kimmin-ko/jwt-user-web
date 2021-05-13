package com.sp.sec.web.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Authority(Role role) {
        this.role = role;
    }

    //-- 비즈니스 메서드 --//
    public void changeUser(User user) {
        this.user = user;
    }

    public enum Role {
        ROLE_USER,
        ROLE_ADMIN;
    }
}