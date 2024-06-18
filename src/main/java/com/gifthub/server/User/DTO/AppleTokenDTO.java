package com.gifthub.server.User.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class AppleTokenDTO {
    private String access_token;
    private int expires_in;
    private String token_type;
    private String id_token;
    private String refresh_token;

    @Builder
    public AppleTokenDTO(String access_token, String token_type, String id_token, int expires_in, String refresh_token) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.id_token = id_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
    }
}
