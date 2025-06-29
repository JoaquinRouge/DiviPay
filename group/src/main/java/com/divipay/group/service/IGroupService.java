package com.divipay.group.service;

import java.util.List;

import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;

public interface IGroupService {

	Group findById(Long id);
	List<Group> findByOwnerId(Long id);
	List<Long> getMembersList(Long id);
	Group createGroup(Group group,boolean hasPaid,Long requestUserId);
	void deleteGroup(Long id,Long requestUserId);
	Group updateGroup(UpdateGroupDto group,Long requestUserId);
}
