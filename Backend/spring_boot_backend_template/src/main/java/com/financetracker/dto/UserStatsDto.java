package com.financetracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatsDto {
    private long totalUsers;
    private long totalAdmins;
    private long totalStandardUsers;
}
