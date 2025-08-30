package com.financetracker.services;

import java.math.BigDecimal;
import java.util.List;

import com.financetracker.dto.CategorySpendDto;
import com.financetracker.dto.TransactionRequestDto;
import com.financetracker.dto.TransactionResponseDto;
import com.financetracker.dto.TransactionSummaryDto;
import com.financetracker.enums.TransactionType;

public interface TransactionService {
	TransactionResponseDto createTransaction(TransactionRequestDto dto, String email);

	List<TransactionResponseDto> listUserTransactionsByEmail(String email);

	BigDecimal getBalanceByEmail(String email);
	
	TransactionSummaryDto getUserSummaryByEmail(String email);
	
	List<CategorySpendDto> getSpendByCategory(String email);

	List<TransactionResponseDto> getFilteredTransactions(String email, String month, TransactionType type, Long categoryId, Long goalId);

}

