package com.financetracker.services;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.financetracker.dao.UserRepository;
import com.financetracker.dto.ApiResponse;
import com.financetracker.dto.UserReqDTO;
import com.financetracker.dto.UserStatsDto;
import com.financetracker.entities.User;
import com.financetracker.enums.Role;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;

@Service
@Transactional
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse signUp(UserReqDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            return new ApiResponse("Email already registered");
        }
        //the builder pattern needs @AllArgsCtor and @Builder in User
		/*
		 * User user = User.builder() .username(dto.getUsername())
		 * .email(dto.getEmail()) .password(passwordEncoder.encode(dto.getPassword()))
		 * .role(Role.USER) .balance(java.math.BigDecimal.ZERO) .build();
		 * userRepository.save(user);
		 */
        
        
        // map DTO to entity
        User user = modelMapper.map(dto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // if your DTO has a role, keep it; otherwise default to USER
        if (user.getRole() == null) {
            user.setRole(Role.USER); // assuming Role enum with USER, ADMIN
        }
        user.setBalance(BigDecimal.ZERO);
        User persisted = userRepository.save(user);
        return new ApiResponse("User registered with ID " + persisted.getId());
    }
    
    @Override
    public UserStatsDto getUserStats() {
        long totalUsers = userRepository.count();
        long totalAdmins = userRepository.findAll()
                              .stream()
                              .filter(user -> user.getRole() == Role.ADMIN)
                              .count();
        long totalStandardUsers = totalUsers - totalAdmins;

        return new UserStatsDto(totalUsers, totalAdmins, totalStandardUsers);
    }

}
