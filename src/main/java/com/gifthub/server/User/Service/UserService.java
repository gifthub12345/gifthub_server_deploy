package com.gifthub.server.User.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifthub.server.Room.Entity.RoomEntity;
import com.gifthub.server.Room.Exception.RoomNotFoundException;
import com.gifthub.server.Room.Repository.RoomRepository;
import com.gifthub.server.User.DTO.*;
import com.gifthub.server.User.Entity.UserEntity;
import com.gifthub.server.User.Jwt.JwtTokenProvider;
import com.gifthub.server.User.Repository.UserRepository;
import com.gifthub.server.User.Util.CustomRequestEntityConverter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Transactional
@Component
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoomRepository roomRepository;
    private final CustomRequestEntityConverter converter;

    @Autowired
    private AppleDTO appleDTO;

    @Autowired
    private GoogleDTO googleDTO;

    public Long getUserId(String token) {
        String identifier = jwtTokenProvider.getIdentifierFromToken(token);
        if (identifier == null) {
            return null;
        }
        else {
            UserEntity user = userRepository.findByIdentifier(identifier);
            return user.getId();
        }
    }

    public List<UserInfoDTO> getUserList(Long roomId) {
        Optional<RoomEntity> room = roomRepository.findById(roomId);
        if (room.isPresent()) {
            List<UserInfoDTO> userInfoDTO = new ArrayList<>();
            RoomEntity roomEntity = room.get();

            for(UserEntity userEntity : roomEntity.getUsers()) {
                UserInfoDTO users = UserInfoDTO.builder()
                        .email(userEntity.getEmail())
                        .build();
                userInfoDTO.add(users);
            }

            return userInfoDTO;
        }
        else {
            throw new RoomNotFoundException();
        }
    }

    public void revoke(String jwtToken, AccessTokenDTO accessTokenDTO) {
        String accessToken = accessTokenDTO.getAccess_token();
        RestTemplate restTemplate = new RestTemplate();
        String identifier = jwtTokenProvider.getIdentifierFromToken(jwtToken);
        UserEntity userEntity = userRepository.findByIdentifier(identifier);

        if (identifier.contains("google")) {
            String revoke_url = "https://accounts.google.com/o/oauth2/revoke";

            LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("token", accessToken);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            restTemplate.postForEntity(revoke_url, httpEntity, String.class);

            userRepository.delete(userEntity);

        } else if (identifier.contains("apple")) {
            String revoke_url = "https://appleid.apple.com/auth/revoke";

            try {
                LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                params.add("client_id", appleDTO.getClientId());
                params.add("client_secret", converter.createClientSecret(appleDTO.getKeyId(), appleDTO.getClientId(), appleDTO.getTeamId()));
                params.add("token", accessToken);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

                restTemplate.postForEntity(revoke_url, httpEntity, String.class);

                userRepository.delete(userEntity);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public AccessTokenDTO GoogleGetAccessToken(AuthorizationCodeDTO codeDTO){
        String decodedCode = URLDecoder.decode(codeDTO.getAuthCode(), StandardCharsets.UTF_8);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String authUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", decodedCode);
        params.add("client_id", googleDTO.getClientId());
        params.add("client_secret", googleDTO.getClientSecret());
        params.add("redirect_uri", googleDTO.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(authUrl, httpEntity, AccessTokenDTO.class);
            return response.getBody();
//            return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, accessToken, null, null);
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Google Auth Token Error");
        }

    }

    public SuccessHandlerDTO getGoogleUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String infoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<UserInfoDTO> response = restTemplate.exchange(infoUrl, HttpMethod.GET, request, UserInfoDTO.class);
        UserInfoDTO info = response.getBody();
        String identifier = "google" + info.getSub();
        UserEntity existUser = userRepository.findByIdentifier(identifier);
        if (existUser == null) {
            UserEntity newUser = UserEntity.builder()
                    .identifier(identifier)
                    .name(info.getName())
                    .email(info.getEmail())
                    .build();
            userRepository.save(newUser);
        }
        else {
            existUser.update(info.getName(), info.getEmail());
            userRepository.save(existUser);
        }

        SuccessHandlerDTO successHandlerDTO = loginSuccessHandler(identifier, accessToken);
        return successHandlerDTO;

    }

    public AccessTokenDTO AppleGetAccessToken(AuthorizationCodeDTO codeDTO) throws IOException{
        String decodedCode = URLDecoder.decode(codeDTO.getAuthCode(), StandardCharsets.UTF_8);
        RestTemplate restTemplate = new RestTemplateBuilder().build();
        String authUrl = "https://appleid.apple.com/auth/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", decodedCode);
        params.add("client_id", appleDTO.getClientId());
        params.add("client_secret", converter.createClientSecret(appleDTO.getKeyId(), appleDTO.getClientId(), appleDTO.getTeamId()));
        params.add("redirect_uri", appleDTO.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<AccessTokenDTO> response = restTemplate.postForEntity(authUrl, httpEntity, AccessTokenDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException(e);
        }

    }


    public SuccessHandlerDTO getAppleUserInfo(String accessToken, String idToken) throws IOException, ServletException {
        Map<String, Object> userinfo = decodeJwtTokenPayload(idToken);
        UserInfoDTO info = UserInfoDTO.builder()
                .sub("apple" + userinfo.get("sub").toString())
                .name("tempName")
                .email(userinfo.get("email").toString())
                .build();

        UserEntity existUser = userRepository.findByIdentifier(info.getSub());

        if (existUser == null) {
            UserEntity newUser = UserEntity.builder()
                    .identifier(info.getSub())
                    .name(info.getName())
                    .email(info.getEmail())
                    .build();
            userRepository.save(newUser);
        }
        else {
            existUser.update(info.getName(), info.getEmail());
            userRepository.save(existUser);
        }

        SuccessHandlerDTO successHandlerDTO = loginSuccessHandler(info.getSub(), accessToken);
        return successHandlerDTO;

    }

    public Map<String, Object> decodeJwtTokenPayload(String jwtToken) {
        Map<String, Object> jwtClaims = new HashMap<>();
        try {
            String[] parts = jwtToken.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();

            byte[] decodedBytes = decoder.decode(parts[1].getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> map = mapper.readValue(decodedString, Map.class);
            jwtClaims.putAll(map);

        } catch (JsonProcessingException e) {
//        logger.error("decodeJwtToken: {}-{} / jwtToken : {}", e.getMessage(), e.getCause(), jwtToken);
        }
        return jwtClaims;
    }

    public SuccessHandlerDTO loginSuccessHandler(String identifier, String ProviderAccessToken) {
        UserEntity user = userRepository.findByIdentifier(identifier);
        Long room_id;

        if(user.getRoom() != null) {
            room_id = user.getRoom().getId();
        }
        else {
            room_id = null;
        }

        String accessToken = jwtTokenProvider.createAccessToken(identifier);
        String refreshToken = jwtTokenProvider.createRefreshToken(identifier);

        SuccessHandlerDTO result = SuccessHandlerDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .ProviderAccessToken(ProviderAccessToken)
                .room_id(room_id)
                .build();

        return result;
    }



}
