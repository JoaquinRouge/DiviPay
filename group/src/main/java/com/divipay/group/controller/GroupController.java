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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;
import com.divipay.group.service.IGroupService;


@RestController
@RequestMapping("/api/group")
public class GroupController {
	
	private IGroupService groupService;
	
	public GroupController(IGroupService groupService) {
		this.groupService = groupService;
	}
	
	@GetMapping("/{ownerId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> findByOwnerId(@PathVariable Long ownerId){
		try {
			List<Group> groups = groupService.findByOwnerId(ownerId);
			return ResponseEntity.status(HttpStatus.OK).body(groups);
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PostMapping("/")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> createGroup(@RequestBody Group group){
		try {
			boolean hasPaid = true;
			return ResponseEntity.status(HttpStatus.CREATED).body(
					groupService.createGroup(group, hasPaid));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@DeleteMapping("/{deleteId}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> deleteGroup(@PathVariable Long deleteId){
		try {
			groupService.deleteGroup(deleteId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
	@PutMapping("/update")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<?> updateGroup(@RequestBody UpdateGroupDto group){
		try {		
			return ResponseEntity.status(HttpStatus.CREATED).body(groupService.updateGroup(group));
		}catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
	
}
