package com.financetracker.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopSpenderDto {
    private String username;
    private String email;
    private BigDecimal totalSpent;
}
