package com.financetracker.entities;

import java.util.ArrayList;
import java.util.List;

import com.financetracker.enums.TransactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "categories")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = { "transactions", "budgets"}, callSuper = true)
public class Category extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., Food, Salary

    @Enumerated(EnumType.STRING)
    private TransactionType type; // INCOME or EXPENSE

   //  If you want reverse navigation:
     @OneToMany(mappedBy = "category")
     private List<Transaction> transactions = new ArrayList<>();

     @OneToMany(mappedBy = "category")
     private List<Budget> budgets = new ArrayList<>();


}
