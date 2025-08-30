package com.financetracker.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financetracker.dao.GoalRepository;
import com.financetracker.dao.UserRepository;
import com.financetracker.dto.GoalRequestDto;
import com.financetracker.dto.GoalResponseDto;
import com.financetracker.entities.Goal;
import com.financetracker.entities.User;
import com.financetracker.enums.GoalStatus;
import com.financetracker.customexceptions.ResourceNotFoundException;
import com.financetracker.customexceptions.BadRequestException;
import com.financetracker.services.GoalService;
import com.financetracker.utils.GoalMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public GoalResponseDto createGoal(GoalRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getTargetAmount().compareTo(dto.getTargetAmount().ZERO) <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        Goal goal = new Goal();
        goal.setTitle(dto.getTitle());
        goal.setGoalDescription(dto.getGoalDescription());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());
        goal.setUser(user);
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setCurrentAmount(goal.getCurrentAmount()); // zero by default

        Goal saved = goalRepository.save(goal);
        return GoalMapper.toDto(saved);
    }

    @Override
    public List<GoalResponseDto> listGoals(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        
        List<Goal> goals = goalRepository.findByUser(user);
       // List<Goal> goals = goalRepository.findByUserIdAndStatus(user.getId(), GoalStatus.ACTIVE);
        // Also include completed if you want; adapt as needed
        return goals.stream().map(GoalMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public GoalResponseDto getGoalById(Long goalId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to view this goal");
        }

        return GoalMapper.toDto(goal);
    }
    
    @Override
    @Transactional
    public GoalResponseDto updateGoal(Long goalId, GoalRequestDto dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to update this goal");
        }

        if (goal.getStatus() == GoalStatus.COMPLETED || goal.getStatus() == GoalStatus.CANCELLED) {
            throw new BadRequestException("Cannot update a completed or cancelled goal");
        }

        if (dto.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        goal.setTitle(dto.getTitle());
        goal.setGoalDescription(dto.getGoalDescription());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setDeadline(dto.getDeadline());

        Goal updated = goalRepository.save(goal);
        return GoalMapper.toDto(updated);
    }


    @Override
    @Transactional
    public void cancelGoal(Long goalId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Not authorized to cancel this goal");
        }

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel a completed goal");
        }

        goal.setStatus(GoalStatus.CANCELLED);
        goalRepository.save(goal);
    }
}
