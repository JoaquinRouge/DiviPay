package com.divipay.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.divipay.user.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long>{
	
	Optional<User> findByEmail(String email);
	Optional<List<User>> findByEmailContainingIgnoreCase(String emailFragment);
	boolean existsByEmail(String email);
	
}
