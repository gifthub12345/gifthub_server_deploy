package com.gifthub.server.User.Controller;

import com.gifthub.server.User.DTO.AccessTokenDTO;
import com.gifthub.server.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final UserService userService;

    @GetMapping("/login/google")
    public ResponseEntity<?> GoogleLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/oauth2/authorization/google"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @GetMapping("/login/apple")
    public ResponseEntity<?> AppleLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/oauth2/authorization/apple"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<?> RevokeUser(HttpServletRequest request, @RequestBody AccessTokenDTO accessToken) {
        String token = request.getHeader("Authorization");
        userService.revoke(token, accessToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
