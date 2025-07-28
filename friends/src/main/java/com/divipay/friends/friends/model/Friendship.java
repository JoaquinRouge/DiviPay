package com.divipay.friends.friends.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "friendships", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "friendId"}))
public class Friendship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long friendId;
    private LocalDateTime createdAt;
    
    public Friendship() {
    	
    }

	public Friendship(Long id, Long userId, Long friendId) {
		super();
		this.id = id;
		this.userId = userId;
		this.friendId = friendId;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getFriendId() {
		return friendId;
	}

	public void setFriendId(Long friendId) {
		this.friendId = friendId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}

