package com.financetracker.controllers;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.dto.BudgetRequestDto;
import com.financetracker.dto.BudgetResponseDto;
import com.financetracker.services.BudgetService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/budgets")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class BudgetController {

	private final BudgetService budgetService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping
	public BudgetResponseDto createOrUpdateBudget(
			@Valid @RequestBody BudgetRequestDto dto, Principal principal) {
		return budgetService.createOrUpdateBudget(principal.getName(), dto);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public BudgetResponseDto getBudget(Principal principal,
			@RequestParam String categoryName, @RequestParam String month // e.g., "2025-08"
	) {
		java.time.YearMonth ym = java.time.YearMonth.parse(month);
		return budgetService.getBudget(principal.getName(), categoryName, ym);
	}

	@GetMapping("/user-with-actuals")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getBudgetsWithActuals(Principal principal) {
		String email = principal.getName();
		return ResponseEntity.ok(budgetService.getBudgetsWithActuals(email));
	}

}
