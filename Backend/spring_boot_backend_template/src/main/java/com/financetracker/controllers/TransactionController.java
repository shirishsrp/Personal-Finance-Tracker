	package com.financetracker.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.financetracker.dto.TransactionRequestDto;
import com.financetracker.dto.TransactionResponseDto;
import com.financetracker.enums.TransactionType;
import com.financetracker.services.TransactionService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    

	/*
	 * public TransactionController(TransactionService transactionService, JwtUtil
	 * jwtUtil) { this.transactionService = transactionService; this.jwtUtil =
	 * jwtUtil; }
	 */

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addTransaction(
            @RequestBody @Valid TransactionRequestDto dto,
            Principal principal) {

    	String email = principal.getName();
        TransactionResponseDto response = transactionService.createTransaction(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMyTransactions(Principal principal) {

    	String email = principal.getName();
        List<TransactionResponseDto> list = transactionService.listUserTransactionsByEmail(email);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/balance")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getBalance(Principal principal) {

    	String email = principal.getName();
        BigDecimal balance = transactionService.getBalanceByEmail(email);
        return ResponseEntity.ok(Map.of("balance", balance));
    }
    
    @GetMapping("/summary")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getTxnSummary(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(transactionService.getUserSummaryByEmail(email));
    }

    @GetMapping("/spending-by-category")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getSpendByCategory(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(transactionService.getSpendByCategory(email));
    }
    
    @GetMapping("/history/filter")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getFilteredTransactions(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long goalId,
            Principal principal) {

        String email = principal.getName();
        List<TransactionResponseDto> filtered = transactionService
            .getFilteredTransactions(email, month, type, categoryId, goalId);
        
        return ResponseEntity.ok(filtered);
    }


}
