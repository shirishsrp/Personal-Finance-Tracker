package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.financetracker.enums.GoalStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDto {
    private Long id;
    private String title;
    private String goalDescription;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate deadline;
    private GoalStatus status;
    private double progressPercentage; // e.g., current/target * 100
}
