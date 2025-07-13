package com.divipay.user.service;

import java.util.List;

import com.divipay.user.dto.UserDto;
import com.divipay.user.model.User;

public interface IUserService {

	User findById(Long id);
	List<UserDto> findListById(List<Long> list);
	User findByEmail(String email);
	User createUser(User user);
	void disableUser(Long id);
	User updateUser(User user);
	
}
