package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.financetracker.enums.TransactionType;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class TransactionRequestDto extends BaseDto {
	
	@NotNull
    private BigDecimal amount;
    
    @NotNull
    private LocalDate date;
    
    private String description;
    
    @NotNull
    private TransactionType type;
    
    @NotNull
    private Long categoryId;
    
    private Long goalId; // optional
    
    private BigDecimal goalAmount;
}
