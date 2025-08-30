package com.financetracker.services;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.financetracker.customexceptions.ResourceNotFoundException;
import com.financetracker.dao.BudgetRepository;
import com.financetracker.dao.CategoryRepository;
import com.financetracker.dao.TransactionRepository;
import com.financetracker.dao.UserRepository;
import com.financetracker.dto.BudgetRequestDto;
import com.financetracker.dto.BudgetResponseDto;
import com.financetracker.dto.BudgetWithActualDto;
import com.financetracker.entities.Budget;
import com.financetracker.entities.Category;
import com.financetracker.entities.Transaction;
import com.financetracker.entities.User;
import com.financetracker.enums.TransactionType;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class BudgetServiceImpl implements BudgetService{

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

   
    @Override
    public BudgetResponseDto createOrUpdateBudget(String email, BudgetRequestDto dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findByNameAndType(dto.getCategoryName(), TransactionType.EXPENSE) // adjust type if needed
                .orElseThrow(() -> new ResourceNotFoundException("No expense category found by this name"));

        // Fetch existing or create
        Budget budget = budgetRepository.findByUserAndCategoryAndMonth(user, category, dto.getMonth())
                .orElseGet(() -> {
                    Budget b = new Budget();
                    b.setUser(user);
                    b.setCategory(category);
                    b.setMonth(dto.getMonth());
                    return b;
                });

        budget.setMonthlyLimit(dto.getMonthlyLimit());
        Budget saved = budgetRepository.save(budget);

        return new BudgetResponseDto(
                saved.getId(),
                saved.getCategory().getName(),
                saved.getMonth(),
                saved.getMonthlyLimit()
        );
    }

    @Override
    public Optional<Budget> getBudgetEntity(User user, Category category, java.time.YearMonth month) {
        return budgetRepository.findByUserAndCategoryAndMonth(user, category, month);
    }

    @Override
    public BudgetResponseDto getBudget(String email, String categoryName, java.time.YearMonth month) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findByNameAndType(categoryName, TransactionType.EXPENSE)
                .orElseThrow(() -> new ResourceNotFoundException("No expense category found by this name"));

        Budget budget = budgetRepository.findByUserAndCategoryAndMonth(user, category, month)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not set for given category/month"));

        return new BudgetResponseDto(
                budget.getId(),
                category.getName(),
                budget.getMonth(),
                budget.getMonthlyLimit()
        );
    }
    
    @Override
    public List<BudgetWithActualDto> getBudgetsWithActuals(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        List<Transaction> txns = transactionRepository.findByUserIdOrderByDateDesc(user.getId()).stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .toList();

        List<Budget> budgets = budgetRepository.findByUserId(user.getId());

     //  Map<categoryName|YearMonth, totalSpent>
        Map<String, BigDecimal> spendingMap = txns.stream()
            .collect(Collectors.groupingBy(
                txn -> txn.getCategory().getName() + "|" + YearMonth.from(txn.getDate()),
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
            ));

        return budgets.stream()
            .map(b -> {
                String key = b.getCategory().getName() + "|" + b.getMonth();
                BigDecimal actual = spendingMap.getOrDefault(key, BigDecimal.ZERO);

                return new BudgetWithActualDto(
                    b.getCategory().getName(),
                    b.getMonthlyLimit(),
                    actual,
                    b.getMonth() //  Include the month in the response
                );
            })
            .toList();

    }

}
