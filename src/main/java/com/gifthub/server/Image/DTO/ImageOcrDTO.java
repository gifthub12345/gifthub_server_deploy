package com.gifthub.server.Image.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageOcrDTO {
    private String barcode;
    private LocalDate expire;
    @Builder
    public ImageOcrDTO(String barcode, LocalDate expire) {
        this.barcode = barcode;
        this.expire = expire;
    }
}
