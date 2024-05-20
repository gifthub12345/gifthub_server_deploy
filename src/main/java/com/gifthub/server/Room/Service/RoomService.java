package com.gifthub.server.Room.Service;

import com.gifthub.server.Image.Repository.ImageRepository;
import com.gifthub.server.Room.DTO.RoomJoinDTO;
import com.gifthub.server.Room.Entity.RoomEntity;
import com.gifthub.server.Room.Repository.RoomRepository;
import com.gifthub.server.User.Entity.UserEntity;
import com.gifthub.server.User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    //이미 있는 코드일 때 에러 띄우기
    public Long createRoom(Long userId) {
//        byte[] array = new byte[10];
//        new Random().nextBytes(array);
//        String generatedCode = new String(array, Charset.forName("UTF-8"));
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 10; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        String generatedCode = sb.toString();

        if (roomRepository.findByCode(generatedCode) == null) {
            RoomEntity newRoom = RoomEntity.builder()
                    .code(generatedCode)
                    .build();
            roomRepository.save(newRoom);

            userRepository.findById(userId).ifPresent(user -> {
                user.updateRoom(newRoom);
                userRepository.save(user);
            });

            return newRoom.getId();
        }
        else {
            return null;
        }
    }

    //없는 코드일때는 exceptionerror띄우기
    public Long enterRoom(Long userId, RoomJoinDTO roomJoinDTO) {
        String code = roomJoinDTO.getCode();
        RoomEntity roomEntity = roomRepository.findByCode(code);
        if (roomEntity != null) {
            Optional<UserEntity> byId = userRepository.findById(userId);
            if (byId.isPresent()) {
                UserEntity userEntity = byId.get();
                userEntity.updateRoom(roomEntity);
            }

            return roomEntity.getId();
        }
        else {
            return null;
        }
    }

    public void exitRoom(Long userId, Long roomId) {
        Optional<RoomEntity> room = roomRepository.findById(roomId);
        Optional<UserEntity> user = userRepository.findById(userId);

        if (room.isPresent() && user.isPresent()) {
            RoomEntity findRoom = room.get();
            UserEntity findUser = user.get();

            findUser.updateRoom(null);
            userRepository.save(findUser);

            imageRepository.deleteAllByUserId(findUser.getId());

            if (findRoom.getUsers().isEmpty()) {
                roomRepository.delete(findRoom);
            }
        }
    }

    public String shareRoomCode(Long roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElseThrow();
        return roomEntity.getCode();
    }
}
