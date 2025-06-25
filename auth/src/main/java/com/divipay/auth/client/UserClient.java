package com.divipay.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.divipay.auth.dto.UserModel;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {
	
	@GetMapping("/api/user/email/{email}")
	public UserModel getUserByEmail(@PathVariable String email);
	
}
