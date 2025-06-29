package com.divipay.spent.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.divipay.spent.configuration.FeignClientConfiguration;
import com.divipay.spent.dto.GroupDto;

	@FeignClient(name = "GROUP-SERVICE",configuration = FeignClientConfiguration.class)
	public interface GroupClient {
		
		@GetMapping("/api/group/{id}/members")
		public GroupDto getMembersList(@PathVariable Long id);
		
	}
