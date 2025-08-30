package com.financetracker.services;

import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.UserReqDTO;
import com.financetracker.dto.UserStatsDto;

public interface UserService {
    ApiResponse signUp(UserReqDTO dto);
    
    UserStatsDto getUserStats();
}
