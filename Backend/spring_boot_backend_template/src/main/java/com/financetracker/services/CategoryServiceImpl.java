package com.financetracker.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financetracker.customexceptions.BadRequestException;
import com.financetracker.customexceptions.ResourceNotFoundException;
import com.financetracker.dao.CategoryRepository;
import com.financetracker.dto.CategoryRequestDto;
import com.financetracker.dto.CategoryResponseDto;
import com.financetracker.entities.Category;
import com.financetracker.utils.CategoryMapper;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        // optional uniqueness check: name+type
        if (categoryRepository.findByNameAndType(dto.getName(), dto.getType()).isPresent()) {
            throw new BadRequestException("Category already exists with name and type");
        }
        Category category = new Category();
        category.setName(dto.getName().trim().toLowerCase());
        category.setType(dto.getType());
        category = categoryRepository.save(category);
        return mapper.toDto(category);
    }

    @Override
    public CategoryResponseDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapper.toDto(category);
    }

    @Override
    public List<CategoryResponseDto> listAllCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        // if changing name/type, ensure no conflict
        if (!category.getName().equals(dto.getName()) || category.getType() != dto.getType()) {
            if (categoryRepository.findByNameAndType(dto.getName(), dto.getType()).isPresent()) {
                throw new BadRequestException("Another category exists with given name and type");
            }
        }
        category.setName(dto.getName().trim().toLowerCase());
        category.setType(dto.getType());
        category = categoryRepository.save(category);
        return mapper.toDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
