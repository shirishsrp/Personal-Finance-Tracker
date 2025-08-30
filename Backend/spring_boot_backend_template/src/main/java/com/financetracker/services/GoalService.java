package com.financetracker.services;

import java.util.List;

import com.financetracker.dto.GoalRequestDto;
import com.financetracker.dto.GoalResponseDto;

public interface GoalService {
	GoalResponseDto createGoal(GoalRequestDto dto, String userEmail);

	List<GoalResponseDto> listGoals(String userEmail);

	GoalResponseDto getGoalById(Long goalId, String userEmail);

	GoalResponseDto updateGoal(Long goalId, GoalRequestDto dto, String userEmail);

	void cancelGoal(Long goalId, String userEmail);
}
