package com.divipay.friends.friends.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.divipay.friends.friends.model.Friendship;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserIdAndFriendId(Long userId, Long friendId);
    List<Friendship> findByUserId(Long userId);
}