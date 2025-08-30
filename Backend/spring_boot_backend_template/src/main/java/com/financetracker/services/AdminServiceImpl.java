package com.financetracker.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.financetracker.dao.TransactionRepository;
import com.financetracker.dao.UserRepository;
import com.financetracker.dto.CategorySpendDto;
import com.financetracker.dto.TopSpenderDto;
import com.financetracker.dto.TransactionStatsDto;
import com.financetracker.entities.Transaction;
import com.financetracker.enums.TransactionType;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public TransactionStatsDto getSystemTransactionSummary() {
        List<Transaction> allTxns = transactionRepository.findAll();

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction txn : allTxns) {
            if (txn.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(txn.getAmount());
            } else if (txn.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(txn.getAmount());
            }
        }

        long totalTxns = allTxns.size();
        long totalUsers = userRepository.count();

        double avgTxnPerUser = totalUsers > 0 ? (double) totalTxns / totalUsers : 0.0;

        return new TransactionStatsDto(
            totalTxns, totalIncome, totalExpense, avgTxnPerUser
        );
    }
    
    @Override
    public List<TopSpenderDto> getTopSpenders(int limit) {
        List<Object[]> results = transactionRepository.findTopSpenders(limit);
        return results.stream()
            .map(row -> new TopSpenderDto(
                (String) row[0],   // username
                (String) row[1],   // email
                (BigDecimal) row[2] // totalSpent
            ))
            .toList();
    }

    @Override
    public List<CategorySpendDto> getTopExpenseCategories(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return transactionRepository.findTopExpenseCategories(pageable);
    }

    
}
