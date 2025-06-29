package com.divipay.spent.service;

import java.util.List;

import com.divipay.spent.model.Spent;

public interface ISpentService {

	Spent findById(Long id);
	List<Spent> findByUserId(Long id);
	List<Spent> findByGroupId(Long id);
	Spent createSpent(Spent spent);
	void deleteSpent(Long id);
	Spent updateSpent(Spent spent);
	
}
