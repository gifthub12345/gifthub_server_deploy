package com.gifthub.server.User.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class UserOAuthDTO {
    private String identifier;
    private String name;
    private String accessToken;

    @Builder
    public UserOAuthDTO(String identifier, String name, String accessToken) {
        this.identifier = identifier;
        this.name = name;
        this.accessToken = accessToken;
    }
}
