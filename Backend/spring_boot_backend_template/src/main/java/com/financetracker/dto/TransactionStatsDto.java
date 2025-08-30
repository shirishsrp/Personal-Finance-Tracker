package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionStatsDto {
    private long totalTransactions;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private double avgTransactionsPerUser;
}
