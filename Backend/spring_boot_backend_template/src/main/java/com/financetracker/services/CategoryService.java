package com.financetracker.services;

import java.util.List;

import com.financetracker.dto.CategoryRequestDto;
import com.financetracker.dto.CategoryResponseDto;

public interface CategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto dto);

    CategoryResponseDto getCategory(Long id);

    List<CategoryResponseDto> listAllCategories();

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto);

    void deleteCategory(Long id);
}
