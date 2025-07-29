package com.divipay.group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.divipay.group.model.Group;

	@Repository
	public interface IGroupRepository extends JpaRepository<Group, Long>{
		Optional<List<Group>> findByOwnerIdOrMembersContains(Long id,Long memberId);
	}
