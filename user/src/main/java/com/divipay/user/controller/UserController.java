package com.divipay.user.controller;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final IUserService userService;
    private final HmacVerifier hmacVerifier;

    public UserController(IUserService userService, HmacVerifier hmacVerifier) {
        this.userService = userService;
        this.hmacVerifier = hmacVerifier;
    }

    @Operation(
            summary = "Find users by provided id list",
            description = "Returns the users associated with the given ids"
        )
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "400", description = "Invalid or non-existing id")
        })
    @PostMapping("/list")
    public ResponseEntity<?> findByIdList(
    		@Parameter(description = "List of ids", required = true)@RequestBody List<Long> list,
    		@Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long userId,
            @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
            @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
            @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature){
    	
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
    	
        try {
        	return ResponseEntity.status(HttpStatus.OK).body(userService.findListById(list));
        }catch(IllegalArgumentException e) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        
    }
    
    @Operation(
        summary = "Find user by email",
        description = "Returns the user associated with the given email"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "400", description = "Invalid or non-existing email")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(
        @Parameter(description = "Email to search") @PathVariable String email
    ) {
        try {
            User user = userService.findByEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Create a new user",
        description = "Registers a new user with the provided data"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
        @Parameter(description = "User to create", required = true) @RequestBody User user
    ) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Disable a user",
        description = "Disables a user if the signature is valid and the ID matches"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User disabled successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature or mismatched user ID"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @PutMapping("/disable/{id}")
    public ResponseEntity<?> disableUser(
        @Parameter(description = "User ID to disable") @PathVariable Long id,
        @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature) || userId != id) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            userService.disableUser(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Update a user",
        description = "Updates the information of an existing user if signature is valid and user ID matches"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User updated"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature or mismatched user ID"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
        @Parameter(description = "User to update", required = true) @RequestBody User user,
        @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature) || userId != user.getId()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            User updated = userService.updateUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

