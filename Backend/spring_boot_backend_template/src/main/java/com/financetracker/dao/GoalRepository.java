package com.financetracker.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.financetracker.entities.Goal;
import com.financetracker.entities.User;
import com.financetracker.enums.GoalStatus;

public interface GoalRepository extends JpaRepository<Goal, Long> {

	List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);
	
	List<Goal> findByUser(User user);

	Optional<Goal> findByIdAndUserId(Long id, Long userId);

	@Query("SELECT g FROM Goal g JOIN FETCH g.user WHERE g.id = :goalId AND g.user.id = :userId")
	Optional<Goal> findByIdAndUserIdWithUser(@Param("goalId") Long goalId, @Param("userId") Long userId);

}
