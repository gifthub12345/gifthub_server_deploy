package com.gifthub.server.User.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDTO {
    private String sub;
    private String email;
    private String name;

    @Builder
    public UserInfoDTO(String sub, String email, String name) {
        this.sub = sub;
        this.email = email;
        this.name = name;
    }
}
