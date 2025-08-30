package com.financetracker.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.financetracker.customexceptions.BadRequestException;
import com.financetracker.customexceptions.InsufficientBalanceException;
import com.financetracker.customexceptions.ResourceNotFoundException;
import com.financetracker.dao.CategoryRepository;
import com.financetracker.dao.GoalRepository;
import com.financetracker.dao.TransactionRepository;
import com.financetracker.dao.UserRepository;
import com.financetracker.dto.CategorySpendDto;
import com.financetracker.dto.TransactionRequestDto;
import com.financetracker.dto.TransactionResponseDto;
import com.financetracker.dto.TransactionSummaryDto;
import com.financetracker.entities.Budget;
import com.financetracker.entities.Category;
import com.financetracker.entities.Goal;
import com.financetracker.entities.Transaction;
import com.financetracker.entities.User;
import com.financetracker.enums.TransactionType;
import com.financetracker.utils.TransactionMapper;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final GoalRepository goalRepository;
	private final TransactionMapper mapper;
	private final BudgetService budgetService;

	@Override
	public TransactionResponseDto createTransaction(TransactionRequestDto dto, String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category not found"));
		if(category.getType()!= dto.getType()) {
			throw new BadRequestException(category.getName() +" is not an "+dto.getType()+" category" );
		}

		Goal goal = null;
		if (dto.getGoalId() != null) {
			goal = goalRepository.findById(dto.getGoalId())
					.orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
			if (!goal.getUser().getId().equals(user.getId())) {
				throw new BadRequestException("Goal does not belong to user");
			}
		}

		// Optional overdraft check for expense
		if (dto.getType() == TransactionType.EXPENSE) {
			BigDecimal currentBalance = user.getBalance();
			if (currentBalance.subtract(dto.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
				throw new InsufficientBalanceException("Insufficient balance for this expense");
			}
		}

		// Persist base transaction
		Transaction tx = new Transaction();
		tx.setAmount(dto.getAmount());
		tx.setDate(dto.getDate());
		tx.setDescription(dto.getDescription());
		tx.setType(dto.getType());
		tx.setUser(user);
		tx.setCategory(category);
		tx.setGoal(goal);
		transactionRepository.save(tx);

		// Handle goal and balance split (income case)
		BigDecimal goalContribution = handleGoalAndBalance(dto, user, goal);

		// Prepare response
		TransactionResponseDto response = mapper.toDto(tx);
		// populate split info
		BigDecimal balanceEffect = calculateBalanceEffect(dto, goalContribution);
		response.setGoalAmountApplied(goalContribution);
		response.setBalanceEffect(balanceEffect);

		// Budget alert only for expense
		if (dto.getType() == TransactionType.EXPENSE) {
			checkBudgetAlert(response, tx, user, category);
		}

		return response;
	}

	@Override
	public List<TransactionResponseDto> listUserTransactionsByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
		return listUserTransactions(user.getId());
	}

	public List<TransactionResponseDto> listUserTransactions(Long userId) {
		List<Transaction> list = transactionRepository.findByUserIdOrderByDateDesc(userId);
		return list.stream().map(mapper::toDto).toList();
	}

	@Override
	public BigDecimal getBalanceByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
		return getBalance(user.getId());
	}

	@Override
	public TransactionSummaryDto getUserSummaryByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		List<Transaction> txns = transactionRepository.findByUserIdOrderByDateDesc(user.getId());

		BigDecimal income = txns.stream().filter(t -> t.getType() == TransactionType.INCOME).map(Transaction::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		BigDecimal expense = txns.stream().filter(t -> t.getType() == TransactionType.EXPENSE)
				.map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		return new TransactionSummaryDto(income, expense, income.subtract(expense));
	}

	@Override
	public List<CategorySpendDto> getSpendByCategory(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

		List<Transaction> txns = transactionRepository.findByUserIdOrderByDateDesc(user.getId()).stream()
				.filter(t -> t.getType() == TransactionType.EXPENSE).toList();

		return txns.stream()
				.collect(Collectors.groupingBy(txn -> txn.getCategory().getName(),
						Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
				.entrySet().stream().map(e -> new CategorySpendDto(e.getKey(), e.getValue())).toList();
	}

	@Override
	public List<TransactionResponseDto> getFilteredTransactions(String email, String month, TransactionType type,
			Long categoryId, Long goalId) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// Start with all user's transactions

		/*
		 * 🔄 Notes: This approach filters in memory (after pulling all transactions for
		 * the user).
		 * It's fine for small to moderate transaction history.
		 * Later, for large datasets, consider moving filtering to JPQL/CriteriaQuery
		 * for performance + pagination.
		 * 
		 */
		List<Transaction> all = transactionRepository.findByUserIdOrderByDateDesc(user.getId());

		Stream<Transaction> stream = all.stream();

		// Filter by month if provided
		if (month != null && !month.isBlank()) {
			YearMonth ym = YearMonth.parse(month); // format "2025-08"
			LocalDate start = ym.atDay(1);
			LocalDate end = ym.plusMonths(1).atDay(1);
			stream = stream.filter(tx -> !tx.getDate().isBefore(start) && tx.getDate().isBefore(end));
		}

		// Filter by type
		if (type != null) {
			stream = stream.filter(tx -> tx.getType() == type);
		}

		// Filter by category
		if (categoryId != null) {
			stream = stream.filter(tx -> tx.getCategory() != null && tx.getCategory().getId().equals(categoryId));
		}

		// Filter by goal
		if (goalId != null) {
			stream = stream.filter(tx -> tx.getGoal() != null && tx.getGoal().getId().equals(goalId));
		}

		return stream.map(mapper::toDto).toList();
	}

	public BigDecimal getBalance(Long userId) {
		// Return cached balance
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		return user.getBalance();
	}

	private BigDecimal handleGoalAndBalance(TransactionRequestDto dto, User user, Goal goal) {
		BigDecimal goalContribution = BigDecimal.ZERO;

		if (goal != null && dto.getType() == TransactionType.INCOME) {
			// Validate goalAmount
			if (dto.getGoalAmount() == null || dto.getGoalAmount().compareTo(BigDecimal.ZERO) <= 0
					|| dto.getGoalAmount().compareTo(dto.getAmount()) > 0) {
				throw new BadRequestException("Invalid goal amount: must be > 0 and ≤ transaction amount");
			}
			goalContribution = dto.getGoalAmount();
			goal.addToCurrentAmount(goalContribution);
			goalRepository.save(goal);
		}

		// Update user balance
		if (dto.getType() == TransactionType.INCOME) {
			BigDecimal balancePortion = dto.getAmount().subtract(goalContribution);
			user.setBalance(user.getBalance().add(balancePortion));
		} else { // expense
			user.setBalance(user.getBalance().subtract(dto.getAmount()));
		}
		userRepository.save(user);

		return goalContribution;
	}

	private void checkBudgetAlert(TransactionResponseDto response, Transaction tx, User user, Category category) {
		YearMonth txnMonth = YearMonth.from(tx.getDate());
		LocalDate startOfMonth = txnMonth.atDay(1);
		LocalDate startOfNext = txnMonth.plusMonths(1).atDay(1);

		Optional<Budget> maybeBudget = budgetService.getBudgetEntity(user, category, txnMonth);
		if (maybeBudget.isPresent()) {
			Budget budget = maybeBudget.get();

			BigDecimal spentSoFar = transactionRepository.sumAmountByUserCategoryAndPeriod(user.getId(),
					category.getId(), TransactionType.EXPENSE, startOfMonth, startOfNext).orElse(BigDecimal.ZERO);

			if (spentSoFar.compareTo(budget.getMonthlyLimit()) > 0) {
				String alertMsg = String.format("You’ve exceeded your %s budget for %s. Limit: %s, Spent: %s",
						category.getName(), txnMonth, budget.getMonthlyLimit(), spentSoFar);
				response.setBudgetAlert(alertMsg);
			}
		}
	}

	private BigDecimal calculateBalanceEffect(TransactionRequestDto dto, BigDecimal goalContribution) {
		if (dto.getType() == TransactionType.INCOME) {
			return dto.getAmount().subtract(goalContribution);
		} else {
			return dto.getAmount().negate(); // full deduction
		}
	}

}
