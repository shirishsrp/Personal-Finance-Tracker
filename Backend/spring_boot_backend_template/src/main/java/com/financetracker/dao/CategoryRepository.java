package com.financetracker.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.financetracker.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	 Optional<Category> findByNameAndType(String name, com.financetracker.enums.TransactionType type);
}
