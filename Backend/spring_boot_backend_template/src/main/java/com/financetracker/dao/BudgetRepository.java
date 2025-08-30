package com.financetracker.dao;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financetracker.entities.Budget;
import com.financetracker.entities.Category;
import com.financetracker.entities.User;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByUserAndCategoryAndMonth(User user, Category category, YearMonth month);
    
    List<Budget> findByUserId(Long userId);

}
