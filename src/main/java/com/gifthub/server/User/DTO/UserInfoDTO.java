package com.gifthub.server.User.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoDTO {
    private String email;

    @Builder
    public UserInfoDTO(String email) {
        this.email = email;
    }
}
