package com.financetracker.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.financetracker.dto.CategoryRequestDto;
import com.financetracker.dto.CategoryResponseDto;
import com.financetracker.services.CategoryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(categoryService.listAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@Valid @RequestBody CategoryRequestDto dto) {
        CategoryResponseDto created = categoryService.createCategory(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable @NotNull Long id,
                                                      @Valid @RequestBody CategoryRequestDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable @NotNull Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
