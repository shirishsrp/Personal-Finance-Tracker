// BudgetRequestDto.java
package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BudgetRequestDto {
    @NotNull
    private String categoryName; // assuming categories are identified by name + type elsewhere

    @NotNull
    private YearMonth month;

    @NotNull @Positive
    private BigDecimal monthlyLimit;

}


