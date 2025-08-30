package com.financetracker.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.dto.CategorySpendDto;
import com.financetracker.dto.TransactionStatsDto;
import com.financetracker.dto.UserStatsDto;
import com.financetracker.services.AdminService;
import com.financetracker.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/user-stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserStatsDto> getUserStats(Principal principal) {
        return ResponseEntity.ok(userService.getUserStats());
    }
    
    @GetMapping("/transaction-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionStatsDto> getTransactionSummary() {
        return ResponseEntity.ok(adminService.getSystemTransactionSummary());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/top-spenders")
    public ResponseEntity<?> getTopSpenders(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(adminService.getTopSpenders(limit));
    }

    @GetMapping("/top-expense-categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CategorySpendDto>> getTopExpenseCategories(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(adminService.getTopExpenseCategories(limit));
    }

}
