package com.gifthub.server.Image.Service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.gifthub.server.Category.Entity.CategoryEntity;
import com.gifthub.server.Category.Repository.CategoryRepository;
import com.gifthub.server.Image.DTO.ImageS3GetDTO;
import com.gifthub.server.Image.DTO.ImageUploadDTO;
import com.gifthub.server.Image.Entity.ImageEntity;
import com.gifthub.server.Image.Exception.*;
import com.gifthub.server.Image.Repository.ImageRepository;
import com.gifthub.server.Room.Entity.RoomEntity;
import com.gifthub.server.Room.Repository.RoomRepository;
import com.gifthub.server.User.Entity.UserEntity;
import com.gifthub.server.User.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${s3.bucket-name}")
    private String bucket;

    public String upload(MultipartFile image, ImageUploadDTO imageUploadDTO) throws IOException {
        if(image.isEmpty() || Objects.isNull(image.getOriginalFilename())){
            throw new ImageEmptyException();
        }

        String s3Url = this.uploadImage(image);

        Optional<UserEntity> userById = userRepository.findById(imageUploadDTO.getUser_id());
        Optional<CategoryEntity> categoryById = categoryRepository.findById(imageUploadDTO.getCategory_id());
        Optional<RoomEntity> roomById = roomRepository.findById(imageUploadDTO.getRoom_id());

        UserEntity userEntity = userById.get();
        CategoryEntity categoryEntity = categoryById.get();
        RoomEntity roomEntity = roomById.get();

        List<ImageEntity> listByRoomIdAndCategoryId = imageRepository.findByRoomIdAndCategoryId(roomEntity.getId(), categoryEntity.getId());
        for(ImageEntity imageEntity : listByRoomIdAndCategoryId){
            if(bCryptPasswordEncoder.matches(imageUploadDTO.getBarcode(), imageEntity.getBarcode())){
                throw new ImageAlreadyExistException();
            }
        }

        ImageEntity imageEntity = ImageEntity.builder()
                .url(s3Url)
                .expire(imageUploadDTO.getExpire())
                .barcode(bCryptPasswordEncoder.encode(imageUploadDTO.getBarcode()))
                .category(categoryEntity)
                .user(userEntity)
                .room(roomEntity)
                .build();

        imageRepository.save(imageEntity);

        return s3Url;
    }

    private String uploadImage(MultipartFile image)  {
        this.validateImageExtension(image.getOriginalFilename());
        try{
            return this.uploadImageToS3(image);
        }catch (IOException e){
            throw new S3ImageUploadException();
        }
    }

    private String uploadImageToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String s3FileName = UUID.randomUUID().toString() + originalFilename;

        InputStream inputStream = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(inputStream);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("image/" + extension);
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, s3FileName, byteArrayInputStream, objectMetadata);
            amazonS3Client.putObject(putObjectRequest);

        }catch (S3ImageUploadException e){
            throw new S3ImageUploadException();
        }finally {
            byteArrayInputStream.close();
            inputStream.close();
        }
        return amazonS3Client.getUrl(bucket, s3FileName).toString();
    }

    private void validateImageExtension(String fileOriginName) {
        int lastDotIndex = fileOriginName.lastIndexOf(".");
        if(lastDotIndex == -1){
            throw new ExtensionNotFoundException();
        }
        String extension = fileOriginName.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList("jpg", "png", "jpeg", "gif");

        if(!allowedExtensionList.contains(extension)){
            throw new ExtensionUnsuitableException();
        }
    }

    public void deleteImageFromS3(Long gifticon_id) throws IOException {
        String key = getKeyFromGifitconId(gifticon_id);
        if (!key.isEmpty()) {
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, key));
            imageRepository.deleteById(Math.toIntExact(gifticon_id));
        } else {
            throw new S3UrlException();
        }
    }

    private String getKeyFromGifitconId(Long gifticon_id) throws UnsupportedEncodingException {
        Optional<ImageEntity> findImage = imageRepository.findById(Math.toIntExact(gifticon_id));
        if (findImage.isPresent()) {
            ImageEntity imageEntity = findImage.get();
            String decodingKey = URLDecoder.decode(imageEntity.getUrl(), "UTF-8");
            // URL에서 마지막 '/' 다음의 문자열부터 추출
            int lastSlashIndex = decodingKey.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < decodingKey.length() - 1) {
                return decodingKey.substring(lastSlashIndex + 1);
            } else {
                throw new S3UrlException();
            }
        } else {
            throw new S3ImageNotFoundException();
        }

    }


    public List<ImageS3GetDTO> getImagesFromS3(Long room_id, Long category_id) {
        List<ImageEntity> byRoomIdAndCategoryId = imageRepository.findByRoomIdAndCategoryId(room_id, category_id);
        if(byRoomIdAndCategoryId != null ){
            List<ImageEntity> sortedImage = byRoomIdAndCategoryId.stream()
                    .sorted(Comparator.comparing(ImageEntity::getExpire))
                    .collect(Collectors.toList());
            List<ImageS3GetDTO> allImagesFromS3 = getAllImagesFromS3(sortedImage);
            return allImagesFromS3;

        }
        return null;
    }


    public List<ImageS3GetDTO> getAllImagesFromS3(List<ImageEntity> s3List) {
        List<ImageS3GetDTO> imageS3GetDTOList = new ArrayList<>();
        for(ImageEntity imageEntity: s3List){
            ImageS3GetDTO imageS3GetDTO = ImageS3GetDTO.builder()
                    .id(imageEntity.getId())
                    .url(imageEntity.getUrl()).build();
            imageS3GetDTOList.add(imageS3GetDTO);
        }
        return imageS3GetDTOList;
    }


    public ImageS3GetDTO getOneImageFromS3(Long gifticon_id) {
        Optional<ImageEntity> getImage = imageRepository.findById(Math.toIntExact(gifticon_id));
        if(getImage.isPresent()){
            ImageEntity imageEntity = getImage.get();
            return ImageS3GetDTO.builder().url(imageEntity.getUrl()).build();
        }
        return null;
    }


}
