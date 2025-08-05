package com.divipay.friends.friends.controller;

import com.divipay.friends.friends.model.FriendRequest;
import com.divipay.friends.friends.service.FriendService;
import com.divipay.friends.friends.utils.HmacVerifier;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final HmacVerifier hmacVerifier;

    public FriendController(FriendService friendService, HmacVerifier hmacVerifier) {
        this.friendService = friendService;
        this.hmacVerifier = hmacVerifier;
    }
    
    @Operation(
            summary = "Send a friend request",
            description = "Sends a friend request from the authenticated user to the target user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or already friends"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(
            @Parameter(description = "Target user ID", required = true) @RequestParam Long targetId,
            @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long requesterId,
            @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
            @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
            @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(requesterId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            friendService.sendFriendRequest(requesterId, targetId);
            return ResponseEntity.ok("Friend request sent.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Accept a friend request",
            description = "Accepts a friend request sent from requester to authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friend request accepted"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature"),
            @ApiResponse(responseCode = "404", description = "Friend request not found")
    })
    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriendRequest(
            @Parameter(description = "Requester user ID", required = true) @RequestParam Long requesterId,
            @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long targetId,
            @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
            @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
            @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(targetId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            friendService.acceptFriendRequest(requesterId, targetId);
            return ResponseEntity.ok("Friend request accepted.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(
            summary = "Get friends list",
            description = "Returns a list of friend IDs for the given user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @GetMapping()
    public ResponseEntity<?> getFriends(
            @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long authUserId,
            @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
            @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
            @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(authUserId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            List<Long> friends = friendService.getFriendIds(authUserId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            // En teoría no debería tirar excepciones, pero por si acaso:
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(
            summary = "Check if two users are friends",
            description = "Returns true if the two users are friends, false otherwise"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Friendship status retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @GetMapping("/{userId}/is-friend-with/{otherUserId}")
    public ResponseEntity<?> areFriends(
            @Parameter(description = "First user ID", required = true) @PathVariable Long userId,
            @Parameter(description = "Second user ID", required = true) @PathVariable Long otherUserId,
            @Parameter(description = "Authenticated user ID", required = true) @RequestHeader("X-User-Id") Long authUserId,
            @Parameter(description = "User email", required = true) @RequestHeader("X-Email") String email,
            @Parameter(description = "Has paid flag", required = true) @RequestHeader("X-Has-Paid") boolean hasPaid,
            @Parameter(description = "HMAC signature", required = true) @RequestHeader("X-Signature") String signature
    ) {
        if (!hmacVerifier.verify(authUserId, email, hasPaid, signature) ||
            !(authUserId.equals(userId) || authUserId.equals(otherUserId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            boolean friends = friendService.areFriends(userId, otherUserId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
    
    @Operation(
            summary = "Get received friend requests",
            description = "Returns the list of friend requests received by the specified user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Received friend requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/received-requests")
    public ResponseEntity<?> getReceivedRequests(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long authUserId,

            @Parameter(description = "User email", required = true)
            @RequestHeader("X-Email") String email,

            @Parameter(description = "Has paid flag", required = true)
            @RequestHeader("X-Has-Paid") boolean hasPaid,

            @Parameter(description = "HMAC signature", required = true)
            @RequestHeader("X-Signature") String signature) {

        if (!hmacVerifier.verify(authUserId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            List<FriendRequest> received = friendService.getReceivedRequests(authUserId);
            return ResponseEntity.ok(received);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @Operation(
            summary = "Delete a friend request",
            description = "Deletes a friend request by ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Friend request deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRequest(
            @Parameter(description = "Authenticated user ID", required = true)
            @RequestHeader("X-User-Id") Long authUserId,
            
            @Parameter(description = "Request id", required = true)
            @PathVariable("id") Long id,
            
            @Parameter(description = "User email", required = true)
            @RequestHeader("X-Email") String email,

            @Parameter(description = "Has paid flag", required = true)
            @RequestHeader("X-Has-Paid") boolean hasPaid,

            @Parameter(description = "HMAC signature", required = true)
            @RequestHeader("X-Signature") String signature) {

        if (!hmacVerifier.verify(authUserId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        
        try {
            friendService.deleteFriendRequest(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}


