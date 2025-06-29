package com.divipay.group.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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


@RestController
@RequestMapping("/api/group")
public class GroupController {
	
	private HmacVerifier HmacVerifier;
	private IGroupService groupService;
	
	public GroupController(IGroupService groupService,HmacVerifier HmacVerifier) {
		this.groupService = groupService;
		this.HmacVerifier = HmacVerifier;
	}
	
	@GetMapping("/prueba")
	public String prueba(
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader(value = "X-Signature", required = true) String signature) {
		
		if(!this.HmacVerifier.verify(userId, email, hasPaid, signature)) {
			return "no";
		}
		
		if(userId != null && email != null && hasPaid) {
			return "si y esta todo";
		}
		
		return userId + email + hasPaid;
		
	}
	
	@GetMapping("/{id}/members")
	public ResponseEntity<?> getMembersList(@PathVariable Long id,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader(value = "X-Signature", required = true) String signature){
		
		if(!this.HmacVerifier.verify(userId, email, hasPaid, signature)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {
			return ResponseEntity.status(HttpStatus.OK).body(groupService.getMembersList(id));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		
	}
	
	@GetMapping("/owner/{ownerId}")
	public ResponseEntity<?> findByOwnerId(@PathVariable Long ownerId,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader(value = "X-Signature", required = true) String signature){
		
		if(!this.HmacVerifier.verify(userId, email, hasPaid, signature) || ownerId != userId) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {
			List<Group> groups = groupService.findByOwnerId(ownerId);
			return ResponseEntity.status(HttpStatus.OK).body(groups);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createGroup(@RequestBody Group group,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader(value = "X-Signature", required = true) String signature){
		
		if(!this.HmacVerifier.verify(userId, email, hasPaid, signature)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(
					groupService.createGroup(group, hasPaid, userId));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@DeleteMapping("/delete/{deleteId}")
	public ResponseEntity<?> deleteGroup(@PathVariable Long deleteId,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader(value = "X-Signature", required = true) String signature){
		
		if(this.HmacVerifier.verify(userId, email, hasPaid, signature)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {
			groupService.deleteGroup(deleteId,userId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PutMapping("/update")
	public ResponseEntity<?> updateGroup(@RequestBody UpdateGroupDto group,
		    @RequestHeader(value = "X-User-Id", required = true) Long userId,
		    @RequestHeader(value = "X-Email", required = true) String email,
		    @RequestHeader(value = "X-Has-Paid", required = true) boolean hasPaid,
		    @RequestHeader(value = "X-Signature", required = true) String signature){
		
		if(this.HmacVerifier.verify(userId, email, hasPaid, signature)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
		}
		
		try {		
			return ResponseEntity.status(HttpStatus.CREATED).body(groupService.updateGroup(group,
					userId));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
}
