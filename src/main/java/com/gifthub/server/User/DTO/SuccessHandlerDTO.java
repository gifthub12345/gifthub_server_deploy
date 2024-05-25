package com.gifthub.server.User.DTO;

import lombok.Builder;
import lombok.Data;

@Data
public class SuccessHandlerDTO {
    private String accessToken;
    private String refreshToken;
    private String ProviderAccessToken;
    private Long room_id;

    @Builder
    public SuccessHandlerDTO(String accessToken, String refreshToken, String ProviderAccessToken, Long room_id) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.ProviderAccessToken = ProviderAccessToken;
        this.room_id = room_id;
    }
}
