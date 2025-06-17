package com.divipay.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divipay.user.model.User;
import com.divipay.user.service.IUserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final IUserService userService;
	
	public UserController(IUserService userService) {
		this.userService = userService;
	}
	
	@GetMapping("/{email}")
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> findByEmail(@PathVariable String email){
		try {
			User user = userService.findByEmail(email);
			return ResponseEntity.status(HttpStatus.OK).body(user);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping()
	@PreAuthorize("permitAll()")
	public ResponseEntity<?> createUser(@RequestBody User user){
		try {
			User created = userService.createUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PutMapping("/disable/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> disableUser(@PathVariable Long id){
		try {
			userService.disableUser(id);
			return ResponseEntity.status(HttpStatus.OK).build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PutMapping("/update")
	@PreAuthorize("isAuthenticated")
	public ResponseEntity<?> updateUser(@RequestBody User user){
		try {
			User updated = userService.updateUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(updated);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
}
