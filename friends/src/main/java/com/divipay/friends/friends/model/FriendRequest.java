package com.divipay.friends.friends.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "friend_requests", uniqueConstraints = @UniqueConstraint(columnNames = {"requesterId", "targetId"}))
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long requesterId;
    private Long targetId;
    private LocalDateTime createdAt;
    
    public FriendRequest() {
    	
    }

	public FriendRequest(Long id, Long requesterId, Long targetId) {
		super();
		this.id = id;
		this.requesterId = requesterId;
		this.targetId = targetId;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Long requesterId) {
		this.requesterId = requesterId;
	}

	public Long getTargetId() {
		return targetId;
	}

	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
}

