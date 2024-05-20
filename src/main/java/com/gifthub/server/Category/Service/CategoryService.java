package com.gifthub.server.Category.Service;

import com.gifthub.server.Category.DTO.CategoryDTO;
import com.gifthub.server.Category.Entity.CategoryEntity;
import com.gifthub.server.Category.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryEntity> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategoryDTO> CategoryDTOList(List<CategoryEntity> categoryEntityList) {
        List<CategoryDTO> categoryDtoList = new ArrayList<>();

        for(CategoryEntity categoryEntity : categoryEntityList) {
            CategoryDTO categoryDto = CategoryDTO.builder().title(categoryEntity.getTitle()).build();
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }
}
