package com.financetracker.dto;

import com.financetracker.enums.TransactionType;

import lombok.Data;

@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private TransactionType type;
}
