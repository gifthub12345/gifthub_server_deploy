package com.gifthub.server.Category.DTO;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryDTO {
    private String title;

    @Builder
    public CategoryDTO(String title) {
        this.title = title;
    }
}
