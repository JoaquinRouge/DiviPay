package com.divipay.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divipay.user.model.User;
import com.divipay.user.service.IUserService;
import com.divipay.user.utils.HmacVerifier;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final IUserService userService;
	private final HmacVerifier hmacVerifier;
	
	public UserController(IUserService userService,HmacVerifier hmacVerifier) {
		this.userService = userService;
		this.hmacVerifier = hmacVerifier;
	}
	
	@GetMapping("/prueba")
	public String prueba(
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader("X-Signature") String signature
		) {
		
		    boolean valid = hmacVerifier.verify(userId, email, hasPaid, signature);
		    if (!valid) {
		        return "firma inválida - acceso denegado";
		    }

		    return "acceso válido" + email + userId + hasPaid;
		}
	
	@GetMapping("/email/{email}")
	public ResponseEntity<?> findByEmail(@PathVariable String email){
		try {
			User user = userService.findByEmail(email);
			return ResponseEntity.status(HttpStatus.OK).body(user);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	 
	@PostMapping("/create")
	public ResponseEntity<?> createUser(@RequestBody User user){
		try {
			User created = userService.createUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(created);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PutMapping("/disable/{id}")
	public ResponseEntity<?> disableUser(@PathVariable Long id,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader("X-Signature") String signature){
		
		if(!hmacVerifier.verify(userId, email, hasPaid, signature) || userId != id) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {
			userService.disableUser(id);
			return ResponseEntity.status(HttpStatus.OK).build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PutMapping("/update")
	public ResponseEntity<?> updateUser(@RequestBody User user,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader("X-Signature") String signature){
		
		if(!hmacVerifier.verify(userId, email, hasPaid, signature) ||
				userId != user.getId()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {
			User updated = userService.updateUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(updated);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
}
