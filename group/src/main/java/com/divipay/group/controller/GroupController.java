package com.divipay.group.controller;

import java.util.List;

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

import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;
import com.divipay.group.service.IGroupService;
import com.divipay.group.utils.HmacVerifier;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/group")
@Tag(name = "Group Controller", description = "Operations related to group management")
public class GroupController {

    private final HmacVerifier hmacVerifier;
    private final IGroupService groupService;

    public GroupController(IGroupService groupService, HmacVerifier hmacVerifier) {
        this.groupService = groupService;
        this.hmacVerifier = hmacVerifier;
    }

    @Operation(
        summary = "Get group owner",
        description = "Returns the email of the user who owns the specified group"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner found"),
        @ApiResponse(responseCode = "400", description = "Invalid group id"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @GetMapping("/{id}/owner")
    public ResponseEntity<?> getOwner(
        @Parameter(description = "Group ID") @PathVariable Long id,
        @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email") @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag") @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature") @RequestHeader("X-Signature") String signature
    ) {
        if (!this.hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            return ResponseEntity.status(HttpStatus.OK).body(groupService.getOwner(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Get group members",
        description = "Returns a list of user IDs belonging to the specified group"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Members found"),
        @ApiResponse(responseCode = "400", description = "Invalid group id"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @GetMapping("/{id}/members")
    public ResponseEntity<?> getMembersList(
        @Parameter(description = "Group ID") @PathVariable Long id,
        @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email") @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag") @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature") @RequestHeader("X-Signature") String signature
    ) {
        if (!this.hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            return ResponseEntity.status(HttpStatus.OK).body(groupService.getMembersList(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Get groups by owner",
        description = "Returns all groups created by the specified owner"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Groups retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid owner ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature or mismatched ID")
    })
    @GetMapping("/owner")
    public ResponseEntity<?> findByOwnerId(
        @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email") @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag") @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature") @RequestHeader("X-Signature") String signature
    ) {
        if (!this.hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            List<Group> groups = groupService.findByOwnerId(userId);
            return ResponseEntity.status(HttpStatus.OK).body(groups);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Create a new group",
        description = "Creates a new group with the authenticated user as the owner"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Group created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createGroup(
        @Parameter(description = "Group to create") @RequestBody Group group,
        @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email") @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag") @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature") @RequestHeader("X-Signature") String signature
    ) {
        if (!this.hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                groupService.createGroup(group, hasPaid, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Delete a group",
        description = "Deletes a group by its ID, only if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Group deleted"),
        @ApiResponse(responseCode = "400", description = "Invalid group ID"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @DeleteMapping("/delete/{deleteId}")
    public ResponseEntity<?> deleteGroup(
        @Parameter(description = "Group ID to delete") @PathVariable Long deleteId,
        @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email") @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag") @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature") @RequestHeader("X-Signature") String signature
    ) {
        if (!this.hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            groupService.deleteGroup(deleteId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(
        summary = "Update a group",
        description = "Updates a group's data if it belongs to the authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Group updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - invalid signature")
    })
    @PutMapping("/update")
    public ResponseEntity<?> updateGroup(
        @Parameter(description = "Group DTO to update") @RequestBody UpdateGroupDto group,
        @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId,
        @Parameter(description = "User email") @RequestHeader("X-Email") String email,
        @Parameter(description = "Has paid flag") @RequestHeader("X-Has-Paid") boolean hasPaid,
        @Parameter(description = "HMAC signature") @RequestHeader("X-Signature") String signature
    ) {
        if (!this.hmacVerifier.verify(userId, email, hasPaid, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                groupService.updateGroup(group, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

