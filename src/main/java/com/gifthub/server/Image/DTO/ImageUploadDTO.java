package com.gifthub.server.Image.DTO;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUploadDTO {

    private Long category_id;
    private Long user_id;
    private Long room_id;
    private String barcode;
    private LocalDate expire;

    @Builder
    public ImageUploadDTO(Long category_id, Long user_id, Long room_id, String barcode, LocalDate expire) {
        this.category_id = category_id;
        this.user_id = user_id;
        this.room_id = room_id;
        this.expire = expire;
        this.barcode = barcode;
    }
}
