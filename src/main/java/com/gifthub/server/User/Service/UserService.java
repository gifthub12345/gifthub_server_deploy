package com.gifthub.server.User.Service;

import com.gifthub.server.Room.Entity.RoomEntity;
import com.gifthub.server.Room.Exception.RoomNotFoundException;
import com.gifthub.server.Room.Repository.RoomRepository;
import com.gifthub.server.User.DTO.*;
import com.gifthub.server.User.Entity.UserEntity;
import com.gifthub.server.User.Jwt.JwtTokenProvider;
import com.gifthub.server.User.Repository.UserRepository;
import com.gifthub.server.User.Util.CustomRequestEntityConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoomRepository roomRepository;
    private final CustomRequestEntityConverter converter;

    @Autowired
    private AppleDTO appleDTO;

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
        String accessToken = accessTokenDTO.getAccessToken();
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

        }
        else if (identifier.contains("apple")) {
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
}
