package com.sp.sec.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sp.sec.web.entity.base.BaseTimeEntity;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Getter
@Entity
@ToString(exclude = {"authorities"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @Embedded
    @JsonIgnore
    private final Authorities authorities = new Authorities();

    private boolean enabled;

    @Builder
    private User(String email, String password, String name, boolean enabled) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.enabled = enabled;
    }

    public static User create(String email, String password, String name, Authority... authorities) {
        User user = User.builder()
                .email(email)
                .password(password)
                .name(name)
                .enabled(true)
                .build();

        user.authorities.addAll(authorities, user);

        return user;
    }

    public Set<GrantedAuthority> getGrantedAuthorities() {
        return authorities.getGrantedAuthorities();
    }
}
