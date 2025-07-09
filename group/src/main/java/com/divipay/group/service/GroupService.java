package com.divipay.group.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;
import com.divipay.group.repository.IGroupRepository;

@Service
public class GroupService implements IGroupService {

	private IGroupRepository groupRepo;
	
	public GroupService(IGroupRepository groupRepo) {
		this.groupRepo = groupRepo;
	}
	
	@Override
	public Group getById(Long id,Long userId) {
		List<Long> members = getMembersList(id);
		
		if(!members.contains(userId)) {
			 throw new IllegalArgumentException("Unauthorized");
		}
		
		return findById(id);
		
	}
	
	private Group findById(Long id) {
		return groupRepo.findById(id).orElseThrow(()-> new
				IllegalArgumentException("Group not found"));
	}

	@Override
	public List<Group> findByOwnerId(Long id) {
		return groupRepo.findByOwnerId(id).orElse(Collections.emptyList());
	}
	
	@Override
	public List<Long> getMembersList(Long id){
		Group group = findById(id);
		
		List<Long> members = group.getMembers();
		
		members.add(group.getOwnerId());
		
		return members;
		
	}
	
	@Override
	public Long getOwner(Long id) {
		return findById(id).getOwnerId();
	}
	
	@Override
	public Group createGroup(Group group, boolean hasPaid) {
		
		if(!hasPaid && findByOwnerId(group.getOwnerId()).size() == 2) {
			throw new IllegalArgumentException("Only 2 groups per member allowed."
					+ " Upgrade to premium to create more.");
		}
		
		group.setMembers(Collections.emptyList());
		group.setCreatedAt(LocalDate.now());
		
		return groupRepo.save(group);
	}

	@Override
	public void deleteGroup(Long id,Long requestUserId) {

		if(id != requestUserId) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		if(!groupRepo.existsById(id)) {
			throw new IllegalArgumentException("Group not found");
		}
		
		groupRepo.deleteById(id);
		
	}

	@Override
	public Group updateGroup(UpdateGroupDto group,Long requestUserId) {
		
		if(group.id() != requestUserId) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		Group groupFromDb = findById(group.id());
		
		
		groupFromDb.setName(group.name());
		groupFromDb.setDescription(group.description());
		
		return groupRepo.save(groupFromDb);
	}

}
