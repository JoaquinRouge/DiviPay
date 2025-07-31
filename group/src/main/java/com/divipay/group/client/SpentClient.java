package com.divipay.group.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.divipay.group.configuration.FeignClientConfiguration;

@FeignClient(name = "SPENT-SERVICE",configuration = FeignClientConfiguration.class)
public interface SpentClient {
	
	@DeleteMapping("/api/spent/delete/all/{id}")
	void deleteSpents(@PathVariable Long id);
}
