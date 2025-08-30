package com.financetracker.services;

import java.util.List;

import com.financetracker.dto.CategorySpendDto;
import com.financetracker.dto.TopSpenderDto;
import com.financetracker.dto.TransactionStatsDto;

public interface AdminService {

	TransactionStatsDto getSystemTransactionSummary();
	
	List<TopSpenderDto> getTopSpenders(int limit);

	List<CategorySpendDto> getTopExpenseCategories(int limit);
	
}
