package com.divipay.group.service;

import java.util.List;

import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;

public interface IGroupService {

	Group getById(Long id,Long userId);
	List<Group> findByUserId(Long id);
	List<Long> getMembersList(Long id);
	Long getOwner(Long id);
	Group createGroup(Group group,boolean hasPaid);
	void deleteGroup(Long id,Long requestUserId);
	Group updateGroup(UpdateGroupDto group,Long requestUserId);
	void addMembers(Long userId,List<Long> users,Long groupId);
}
