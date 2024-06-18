package com.gifthub.server.User.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class GoogleTokenDTO {
    private String access_token;
    private String scope;
    private int expires_in;
    private String token_type;
    private String id_token;

    @Builder
    public GoogleTokenDTO(String access_token, String scope, String token_type, String id_token, int expires_in) {
        this.access_token = access_token;
        this.scope = scope;
        this.token_type = token_type;
        this.id_token = id_token;
        this.expires_in = expires_in;
    }
}
