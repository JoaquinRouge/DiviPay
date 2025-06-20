package com.divipay.group.service;

import java.util.List;

import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;

public interface IGroupService {

	Group findById(Long id);
	List<Group> findByOwnerId(Long id);
	Group createGroup(Group group,boolean hasPaid);
	void deleteGroup(Long id);
	Group updateGroup(UpdateGroupDto group);
}
