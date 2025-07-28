package com.divipay.group.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.divipay.group.configuration.FeignClientConfiguration;


@FeignClient(name = "FRIENDS-SERVICE",configuration = FeignClientConfiguration.class)
public interface FriendsClient {
	@GetMapping("/api/friends/{userId}/is-friend-with/{otherUserId}")
	boolean areFriends(@PathVariable Long userId, @PathVariable Long otherUserId);
}
