package com.gifthub.server.User.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthorizationCodeDTO {
    private String authCode;
}
