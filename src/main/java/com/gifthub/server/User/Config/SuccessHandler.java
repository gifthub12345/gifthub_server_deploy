package com.gifthub.server.User.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifthub.server.User.DTO.CustomOAuth2User;
import com.gifthub.server.User.Jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String identifier = customOAuth2User.getIdentifier();
        String providerAccessToken = customOAuth2User.getAccessToken();

        String accessToken = jwtTokenProvider.createAccessToken(identifier);
        String refreshToken = jwtTokenProvider.createRefreshToken(identifier);

        response.setHeader("Authorization", accessToken);
        response.setHeader("RefreshToken", refreshToken);

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("Authorization", accessToken);
        tokenResponse.put("ProviderAccessToken", providerAccessToken);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));

    }
}
