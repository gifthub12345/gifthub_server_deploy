package com.gifthub.server.User.Controller;

import com.gifthub.server.User.DTO.*;
import com.gifthub.server.User.Service.OAuth2Service;
import com.gifthub.server.User.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final UserService userService;
    private final OAuth2Service oAuth2Service;

    @PostMapping("/login/google")
    public ResponseEntity<?> GoogleLogin(HttpServletResponse response, @RequestBody AuthorizationCodeDTO codeDTO) throws IOException, ServletException {
        String accessToken = userService.GoogleGetAccessToken(codeDTO).getAccess_token();
        SuccessHandlerDTO result = userService.getGoogleUserInfo(accessToken);

        response.setHeader("Authorization", result.getAccessToken());
        response.setHeader("RefreshToken", result.getRefreshToken());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/login/apple")
    public ResponseEntity<?> AppleLogin(HttpServletResponse response, @RequestBody AuthorizationCodeDTO codeDTO) throws Exception {
        AccessTokenDTO accessTokenDTO = userService.AppleGetAccessToken(codeDTO);
        String accessToken = accessTokenDTO.getAccess_token();
        String idToken = accessTokenDTO.getId_token();
        SuccessHandlerDTO result = userService.getAppleUserInfo(accessToken, idToken);

        response.setHeader("Authorization", result.getAccessToken());
        response.setHeader("RefreshToken", result.getRefreshToken());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<?> RevokeUser(HttpServletRequest request, @RequestBody String accessToken) {
        String token = request.getHeader("Authorization");
        userService.revoke(token, accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
