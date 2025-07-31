package com.divipay.spent.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divipay.spent.dto.UpdateSpentDto;
import com.divipay.spent.model.Spent;
import com.divipay.spent.service.ISpentService;
import com.divipay.spent.utils.HmacVerifier;

import feign.FeignException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/spent")
public class SpentController {

    private HmacVerifier hmacVerifier;
    private ISpentService spentService;

    public SpentController(HmacVerifier hmacVerifier, ISpentService spentService) {
        this.hmacVerifier = hmacVerifier;
        this.spentService = spentService;
    }

    @Operation(
        summary = "Gets Spents for the given group id",
        description = "Returns all the spents of the group"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request successful"),
        @ApiResponse(responseCode = "401", description = "Invalid headers"),
        @ApiResponse(responseCode = "400", description = "Invalid group id")
    })
    @GetMapping("group/{id}")
    public ResponseEntity<?> findByGroupId(
        @Parameter(description = "Group id") @PathVariable Long id,
        @Parameter(description = "Request user id", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Indicates if the user has paid or not", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC sign given by the api-gateway", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            return ResponseEntity.status(HttpStatus.OK).body(spentService.findByGroupId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Creates a new Spent",
        description = "Creates a new spent with the given data"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Spent created successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid headers"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createSpent(
        @Parameter(description = "Spent to create", required = true) @RequestBody Spent spent,
        @Parameter(description = "Request user id", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Indicates if the user has paid or not", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC sign given by the api-gateway", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
        	
        	spent.setUserId(userId);
        	
            return ResponseEntity.status(HttpStatus.CREATED).body(spentService.createSpent(spent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Deletes a Spent by id",
        description = "Deletes the spent with the given id if it belongs to the user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Spent deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid headers"),
        @ApiResponse(responseCode = "400", description = "Invalid spent id")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSpent(
        @Parameter(description = "Spent id to delete") @PathVariable Long id,
        @Parameter(description = "Request user id", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Indicates if the user has paid or not", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC sign given by the api-gateway", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            spentService.deleteSpent(id, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (FeignException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Gets the total amount of a group"
        )
        @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok"),
            @ApiResponse(responseCode = "401", description = "Invalid headers"),
            @ApiResponse(responseCode = "400", description = "Invalid group id")
        })
    @GetMapping("/total/{groupId}")
    public ResponseEntity<?> getTotalFromGroupId(@PathVariable Long groupId,
    	        @Parameter(description = "Request user id", required = true) @RequestHeader("X-User-Id") Long userId,
    	        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
    	        @Parameter(description = "Indicates if the user has paid or not", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
    	        @Parameter(description = "HMAC sign given by the api-gateway", required = true) @RequestHeader("X-Signature") String signature){
    	
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        try {
            return ResponseEntity.status(HttpStatus.OK).body(spentService.findTotal(groupId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    	
    }
    
    @Operation(
        summary = "Updates an existing Spent",
        description = "Updates the spent data using the provided DTO"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Spent updated successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid headers"),
        @ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateSpent(
        @Parameter(description = "DTO with updated spent data", required = true) @RequestBody UpdateSpentDto spent,
        @Parameter(description = "Request user id", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Indicates if the user has paid or not", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC sign given by the api-gateway", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(spentService.updateSpent(spent, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    @Operation(
            summary = "Deletes all spents for the given group id",
            description = "Deletes all spents for the given group id"
        )
        @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Spents deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid headers"),
            @ApiResponse(responseCode = "400", description = "Error during deleting process")
        })
    @DeleteMapping("/delete/all/{id}")
    public ResponseEntity<?> deleteSpents(
    	@Parameter(description = "Group id", required = true) @PathVariable Long id,
        @Parameter(description = "Request user id", required = true) @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
        @Parameter(description = "Indicates if the user has paid or not", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC sign given by the api-gateway", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
        	spentService.deleteAllSpents(id, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

