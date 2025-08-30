package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.financetracker.enums.TransactionType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString(exclude = {"category","goal"})
public class TransactionResponseDto extends BaseDto {
    private Long id;
    private BigDecimal amount;
    private LocalDate date;
    private String description;
    private TransactionType type;
    private String category;
    private String goal;
    
    private BigDecimal goalAmountApplied; // how much was credited to goal
    private BigDecimal balanceEffect; // how much actually went to user balance (positive for income, negative for expense)
    
    private String budgetAlert;
}

