package com.financetracker.utils;

import org.springframework.stereotype.Component;

import com.financetracker.dto.CategoryResponseDto;
import com.financetracker.entities.Category;

@Component
public class CategoryMapper {

    public CategoryResponseDto toDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType());
        return dto;
    }
}
