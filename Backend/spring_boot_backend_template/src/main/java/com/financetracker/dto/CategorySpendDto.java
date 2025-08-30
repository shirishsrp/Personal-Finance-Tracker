package com.financetracker.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategorySpendDto {
    private String categoryName;
    private BigDecimal totalSpent;
}
