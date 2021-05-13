package com.sp.sec.web.controller.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sp.sec.web.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserDto(@JsonProperty("id") Long id,
                   @JsonProperty("name") String name,
                   @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}