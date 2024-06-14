package com.gifthub.server.Image.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageS3GetDTO {
    private String url;
    private Long id;


    @Builder
    public ImageS3GetDTO(String url, Long id) {
        this.id = id;
        this.url = url;
    }


}
