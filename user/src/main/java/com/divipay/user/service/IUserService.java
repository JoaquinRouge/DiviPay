package com.divipay.user.service;

import com.divipay.user.model.User;

public interface IUserService {

	User findById(Long id);
	User findByEmail(String email);
	User createUser(User user);
	void disableUser(Long id);
	User updateUser(User user);
	
}
