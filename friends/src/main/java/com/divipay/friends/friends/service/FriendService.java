package com.divipay.friends.friends.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.divipay.friends.friends.model.FriendRequest;
import com.divipay.friends.friends.model.Friendship;
import com.divipay.friends.friends.repository.FriendRequestRepository;
import com.divipay.friends.friends.repository.FriendshipRepository;

@Service
public class FriendService implements IFriendsService{

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendService(FriendRequestRepository friendRequestRepository,
                         FriendshipRepository friendshipRepository) {
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
    }
    
    @Override
    public void sendFriendRequest(Long requesterId, Long targetId) {
        if (requesterId.equals(targetId)) {
            throw new IllegalArgumentException("You cant sent a friendship request to yourself.");
        }

        if (areFriends(requesterId, targetId)) {
            throw new IllegalStateException("Already friends.");
        }

        // Friend request already exists
        if (friendRequestRepository.existsByRequesterIdAndTargetId(requesterId, targetId)) {
            throw new IllegalStateException("The friend request was already sended.");
        }

        // Create request
        FriendRequest request = new FriendRequest();
        request.setCreatedAt(LocalDateTime.now());
        request.setRequesterId(requesterId);
        request.setTargetId(targetId);
        friendRequestRepository.save(request);
    }

    // Accept friendship request
    @Override
    public void acceptFriendRequest(Long requesterId, Long targetId) {
        // Check if request exists
        FriendRequest request = friendRequestRepository
                .findByRequesterIdAndTargetId(requesterId, targetId)
                .orElseThrow(() -> new NoSuchElementException("Solicitud no encontrada."));

        // Delete request
        friendRequestRepository.deleteById(request.getId());
        friendRequestRepository.findByRequesterIdAndTargetId(targetId, requesterId)
                .ifPresent(r -> friendRequestRepository.deleteById(r.getId()));

        // Create friendship
        Long userA = Math.min(requesterId, targetId);
        Long userB = Math.max(requesterId, targetId);

        if (!friendshipRepository.existsByUserIdAndFriendId(userA, userB)) {
            Friendship friendship = new Friendship();
            friendship.setCreatedAt(LocalDateTime.now());
            friendship.setUserId(userA);
            friendship.setFriendId(userB);
            friendshipRepository.save(friendship);
        }
    }

    // Obtain user friends list
    @Override
    public List<Long> getFriendIds(Long userId) {
        return friendshipRepository.findAll().stream()
                .filter(f -> f.getUserId().equals(userId) || f.getFriendId().equals(userId))
                .map(f -> f.getUserId().equals(userId) ? f.getFriendId() : f.getUserId())
                .toList();
    }

    // Check if given users are friends
    @Override
    public boolean areFriends(Long userId1, Long userId2) {
        Long a = Math.min(userId1, userId2);
        Long b = Math.max(userId1, userId2);
        return friendshipRepository.existsByUserIdAndFriendId(a, b);
    }
    
    @Override
    public List<FriendRequest> getReceivedRequests(Long userId) {
        return friendRequestRepository.findByTargetId(userId);
    }

	@Override
	public void deleteFriendRequest(Long id) {
		
		if(!friendRequestRepository.existsById(id)) {
			throw new IllegalArgumentException("Friend request not found");
		}
		
		friendRequestRepository.deleteById(id);
		
	}
}

