package com.financetracker.utils;

import java.math.BigDecimal;

import com.financetracker.dto.GoalResponseDto;
import com.financetracker.entities.Goal;

public class GoalMapper {

    public static GoalResponseDto toDto(Goal goal) {
        double progress = 0.0;
        if (goal.getTargetAmount() != null && goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .doubleValue();
            if (progress > 100.0) progress = 100.0;
        }

        return new GoalResponseDto(
                goal.getId(),
                goal.getTitle(),
                goal.getGoalDescription(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getDeadline(),
                goal.getStatus(),
                progress
        );
    }
}
