package com.financetracker.controllers;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.GoalRequestDto;
import com.financetracker.dto.GoalResponseDto;
import com.financetracker.services.GoalService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createGoal(@RequestBody @Valid GoalRequestDto dto, Principal principal) {
        GoalResponseDto created = goalService.createGoal(dto, principal.getName());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<GoalResponseDto>> listGoals(Principal principal) {
        List<GoalResponseDto> goals = goalService.listGoals(principal.getName());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GoalResponseDto> getGoal(@PathVariable("id") Long id, Principal principal) {
        GoalResponseDto goal = goalService.getGoalById(id, principal.getName());
        return ResponseEntity.ok(goal);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelGoal(@PathVariable("id") Long id, Principal principal) {
        goalService.cancelGoal(id, principal.getName());
        return ResponseEntity.ok(new ApiResponse("Goal cancelled")); // reuse your ApiResponse DTO
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GoalResponseDto> updateGoal(
            @PathVariable("id") Long id,
            @Valid @RequestBody GoalRequestDto dto,
            Principal principal) {
        GoalResponseDto updated = goalService.updateGoal(id, dto, principal.getName());
        return ResponseEntity.ok(updated);
    }

}
