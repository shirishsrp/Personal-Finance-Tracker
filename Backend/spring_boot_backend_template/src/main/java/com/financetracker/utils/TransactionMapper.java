package com.financetracker.utils;

import org.springframework.stereotype.Component;

import com.financetracker.dto.TransactionResponseDto;
import com.financetracker.entities.Transaction;

@Component
public class TransactionMapper {
    public TransactionResponseDto toDto(Transaction tx) {
        TransactionResponseDto dto = new TransactionResponseDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setDate(tx.getDate());
        dto.setDescription(tx.getDescription());
        dto.setType(tx.getType());
        dto.setCategory(tx.getCategory() != null ? tx.getCategory().getName() : null);
        dto.setGoal(tx.getGoal() != null ? tx.getGoal().getTitle() : null);
       
        return dto;
    }
}

