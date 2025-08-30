package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetWithActualDto {
    private String category;
    private BigDecimal limit;
    private BigDecimal actualSpent;
    private YearMonth month;   
}
