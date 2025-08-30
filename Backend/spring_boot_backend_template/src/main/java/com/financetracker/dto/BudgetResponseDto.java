// BudgetResponseDto.java
package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BudgetResponseDto {
	private Long id;
	private String categoryName;
	private YearMonth month;
	private BigDecimal monthlyLimit;

}