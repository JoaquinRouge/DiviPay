package com.divipay.friends.friends.service;

import java.util.List;

import com.divipay.friends.friends.model.FriendRequest;

public interface IFriendsService {

    void sendFriendRequest(Long requesterId, Long targetId);

    void acceptFriendRequest(Long requesterId, Long targetId);

    List<Long> getFriendIds(Long userId);

    boolean areFriends(Long userId1, Long userId2);

    List<FriendRequest> getReceivedRequests(Long userId);
    
    void deleteFriendRequest(Long id);
}
