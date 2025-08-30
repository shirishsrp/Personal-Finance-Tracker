package com.financetracker.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.financetracker.dto.CategorySpendDto;
import com.financetracker.entities.Transaction;
import com.financetracker.enums.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("""
			    SELECT COALESCE(SUM(CASE WHEN t.type = 'INCOME' THEN t.amount ELSE 0 END),0)
			         - COALESCE(SUM(CASE WHEN t.type = 'EXPENSE' THEN t.amount ELSE 0 END),0)
			    FROM Transaction t
			    WHERE t.user.id = :userId
			""")
	BigDecimal calculateBalance(@Param("userId") Long userId);

	List<Transaction> findByUserIdOrderByDateDesc(Long userId);

	@Query("""
			    SELECT COALESCE(SUM(t.amount), 0)
			    FROM Transaction t
			    WHERE t.user.id = :userId
			      AND t.category.id = :categoryId
			      AND t.type = :type
			      AND t.date >= :start
			      AND t.date < :end
			""")
	Optional<BigDecimal> sumAmountByUserCategoryAndPeriod(@Param("userId") Long userId,
			@Param("categoryId") Long categoryId, @Param("type") TransactionType type, @Param("start") LocalDate start,
			@Param("end") LocalDate end);

	@Query("SELECT t.user.username, t.user.email, SUM(t.amount) " + "FROM Transaction t "
			+ "WHERE t.type = com.financetracker.enums.TransactionType.EXPENSE "
			+ "GROUP BY t.user.id, t.user.username, t.user.email " + "ORDER BY SUM(t.amount) DESC")
	List<Object[]> findTopSpenders(Pageable pageable);

	// OR helper method:
	default List<Object[]> findTopSpenders(int limit) {
		return findTopSpenders(PageRequest.of(0, limit));
	}

	@Query("SELECT new com.financetracker.dto.CategorySpendDto(t.category.name, SUM(t.amount)) "
			+ "FROM Transaction t WHERE t.type = com.financetracker.enums.TransactionType.EXPENSE "
			+ "GROUP BY t.category.name ORDER BY SUM(t.amount) DESC")
	List<CategorySpendDto> findTopExpenseCategories(Pageable pageable);

}
