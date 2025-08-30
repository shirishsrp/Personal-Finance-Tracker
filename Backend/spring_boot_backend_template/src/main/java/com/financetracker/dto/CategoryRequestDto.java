package com.financetracker.dto;

import com.financetracker.enums.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequestDto {
    @NotBlank
    private String name;

    @NotNull
    private TransactionType type; // INCOME or EXPENSE
}

