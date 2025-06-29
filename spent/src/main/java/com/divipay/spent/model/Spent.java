package com.divipay.spent.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "spents")
public class Spent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String description;
	private double amount;
	private Long userId;
	private Long groupId;
	@ElementCollection
	@CollectionTable(name = "spent_members", joinColumns = @JoinColumn(name = "spent_id"))
	@Column(name = "member_user_id")
	private List<Long> members;
	
	public Spent() {
		
	}

	public Spent(Long id, String description, double amount, Long userId, Long groupId, List<Long> members) {
		super();
		this.id = id;
		this.description = description;
		this.amount = amount;
		this.userId = userId;
		this.groupId = groupId;
		this.members = members;
	}

	public List<Long> getMembers() {
		return members;
	}

	public void setMembers(List<Long> members) {
		this.members = members;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
}
