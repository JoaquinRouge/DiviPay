package com.divipay.user.service;

import java.time.LocalDate;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.divipay.user.model.User;
import com.divipay.user.repository.IUserRepository;

@Service
public class UserService implements IUserService {

	private final IUserRepository userRepo;
	private final BCryptPasswordEncoder encoder;
	
	public UserService(IUserRepository userRepo,BCryptPasswordEncoder encoder) {
		this.userRepo = userRepo;
		this.encoder = encoder;
	}
	
	@Override
	public User findById(Long id) {
		return this.userRepo.findById(id).orElseThrow(()->
			new IllegalArgumentException("User not found for id " + id));
	}

	@Override
	public User findByEmail(String email) {
		return this.userRepo.findByEmail(email).orElseThrow(()->
		new IllegalArgumentException("User not found for email " + email));
	}

	@Override
	public User createUser(User user) {
		
		if(userRepo.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Email already taken");
		}
		
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setCreatedAt(LocalDate.now());
		user.setEnabled(true);
		user.setPassword(encode(user.getPassword()));
		user.setHasPaid(false);
		
		return userRepo.save(user);
	}

	@Override
	public void disableUser(Long id) {
		User user = findById(id);
		
		user.setEnabled(false);
		
		userRepo.save(user);
	}

	@Override
	public User updateUser(User user) {
		
		User userFromDb = findById(user.getId());
		
		if(userRepo.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("Email already taken");
		}
		
		userFromDb.setEmail(user.getEmail());
		userFromDb.setFullName(user.getFullName());
		
		return userRepo.save(userFromDb);
	}

	private String encode(String rawPassword) {
		return this.encoder.encode(rawPassword);
	}
	
}
