package com.financetracker.customexceptions;

public class BudgetExceededException extends RuntimeException {
	public BudgetExceededException(String message) {
		super(message);
	}
}
