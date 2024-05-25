package com.gifthub.server.User.DTO;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "google")
public class GoogleDTO {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
