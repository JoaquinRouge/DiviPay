package com.divipay.spent.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.divipay.spent.configuration.FeignClientConfiguration;
import com.divipay.spent.dto.GroupOwnerDto;

	@FeignClient(name = "GROUP-SERVICE",configuration = FeignClientConfiguration.class)
	public interface GroupClient {
		
		// Returns the members list including the owner for the group with the given id
		@GetMapping("/api/group/{id}/members")
		public List<Long> getMembersList(@PathVariable Long id);
		
		// Returns the owner id for the group with the given id
		@GetMapping("/api/group/{id}/owner")
		public Long getOwner(@PathVariable Long id);
	}
