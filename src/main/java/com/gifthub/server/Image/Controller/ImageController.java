package com.gifthub.server.Image.Controller;

import com.gifthub.server.Image.DTO.ImageOcrDTO;
import com.gifthub.server.Image.DTO.ImageS3GetDTO;
import com.gifthub.server.Image.DTO.ImageUploadDTO;
import com.gifthub.server.Image.Service.ImageService;
import com.gifthub.server.Room.Repository.RoomRepository;
import com.gifthub.server.User.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;
    private final RoomRepository roomRepository;

    @GetMapping("/room/{room_id}/categories/{category_id}")
    public ResponseEntity<?> s3Get(@PathVariable("room_id") Long room_id, @PathVariable("category_id") Long category_id) throws IOException {
        List<ImageS3GetDTO> imagesFromS3 = imageService.getImagesFromS3(room_id, category_id);
        return new ResponseEntity<>(imagesFromS3, HttpStatus.OK);
    }

    // 이미지 업로드
    @PostMapping(path = "/room/{room_id}/categories/{category_id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image,
                                      @RequestPart ImageOcrDTO imageOcrDTO,
                                      @PathVariable("room_id") Long room_id,
                                      @PathVariable("category_id") Long category_id,
                                      HttpServletRequest request) throws IOException {

        String token = request.getHeader("Authorization");
        Long currentUserId = userService.getUserId(token);

        // s3 업로드
        ImageUploadDTO imageUploadDTO = ImageUploadDTO.builder()
                .room_id(room_id)
                .category_id(category_id)
                .user_id(currentUserId)
                .barcode(imageOcrDTO.getBarcode())
                .expire(imageOcrDTO.getExpire())
                .build();
        String imagePath = imageService.upload(image, imageUploadDTO);
        return new ResponseEntity<>(imagePath, HttpStatus.OK);
    }

    // 이미지 하나 가져오기
    @GetMapping("/room/{room_id}/categories/{category_id}/gifticons/{gifticon_id}")
    public ResponseEntity<?> s3OneGet(@PathVariable("gifticon_id")Long gifticon_id) throws IOException {
        ImageS3GetDTO oneImageFromS3 = imageService.getOneImageFromS3(gifticon_id);
        return new ResponseEntity<>(oneImageFromS3, HttpStatus.OK);
    }

    // 이미지 삭제
    @DeleteMapping("/room/{room_id}/categories/{category_id}/gifticons/{gifticon_id}")
    public ResponseEntity<?> s3Delete(@PathVariable("gifticon_id")Long gifticon_id) throws IOException {
        imageService.deleteImageFromS3(gifticon_id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
