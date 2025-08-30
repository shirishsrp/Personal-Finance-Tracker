package com.financetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String goalDescription;
    
    @NotNull
    private BigDecimal targetAmount;

    @NotNull
    @FutureOrPresent
    private LocalDate deadline;
}
