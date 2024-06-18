package com.gifthub.server.User.Controller;

import com.gifthub.server.User.DTO.*;
import com.gifthub.server.User.Service.OAuth2Service;
import com.gifthub.server.User.Service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final UserService userService;
    private final OAuth2Service oAuth2Service;

    @PostMapping("/login/google")
    public ResponseEntity<?> GoogleLogin(HttpServletResponse response, @RequestBody AccessTokenOnlyDTO token) throws IOException, ServletException {
//        String accessToken = userService.GoogleGetAccessToken(codeDTO).getAccess_token();
        String accessToken = token.getAccessToken();
        String idToken = token.getIdToken();
        SuccessHandlerDTO result = userService.getGoogleUserInfo(accessToken, idToken);

        response.setHeader("Authorization", result.getAccessToken());
        response.setHeader("RefreshToken", result.getRefreshToken());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/login/apple")
    public ResponseEntity<?> AppleLogin(HttpServletResponse response, @RequestBody AuthorizationCodeDTO codeDTO) throws Exception {
        AppleTokenDTO appleTokenDTO = userService.AppleGetAccessToken(codeDTO);
        String accessToken = appleTokenDTO.getAccess_token();
        String idToken = appleTokenDTO.getId_token();
        String refreshToken = appleTokenDTO.getRefresh_token();
        SuccessHandlerDTO result = userService.getAppleUserInfo(accessToken, idToken, refreshToken);

        response.setHeader("Authorization", result.getAccessToken());
        response.setHeader("RefreshToken", result.getRefreshToken());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<?> RevokeUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        userService.revoke(token);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
