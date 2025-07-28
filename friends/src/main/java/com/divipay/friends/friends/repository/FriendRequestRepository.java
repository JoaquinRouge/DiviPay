package com.divipay.friends.friends.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.divipay.friends.friends.model.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsByRequesterIdAndTargetId(Long requesterId, Long targetId);
    Optional<FriendRequest> findByRequesterIdAndTargetId(Long requesterId, Long targetId);
    List<FriendRequest> findByTargetId(Long targetId);
}
