package com.gifthub.server.User.DTO;

import lombok.Data;

@Data
public class AccessTokenOnlyDTO {
    private String accessToken;
    private String idToken;
}
