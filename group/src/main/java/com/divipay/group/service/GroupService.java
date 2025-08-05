package com.divipay.group.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.divipay.group.client.FriendsClient;
import com.divipay.group.client.SpentClient;
import com.divipay.group.dto.UpdateGroupDto;
import com.divipay.group.model.Group;
import com.divipay.group.repository.IGroupRepository;

@Service
public class GroupService implements IGroupService {

	private IGroupRepository groupRepo;
	private FriendsClient friendsClient;
	private SpentClient	spentClient;
	
	public GroupService(IGroupRepository groupRepo, FriendsClient friendsClient,
			SpentClient spentClient) {
		this.groupRepo = groupRepo;
		this.friendsClient = friendsClient;
		this.spentClient = spentClient;
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
	public List<Group> findByUserId(Long id) {
		return groupRepo.findByOwnerIdOrMembersContains(id,id).orElse(Collections.emptyList());
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
		
		if(!hasPaid && findByUserId(group.getOwnerId()).size() == 2) {
			throw new IllegalArgumentException("Only 2 groups per member allowed."
					+ " Upgrade to premium to create more.");
		}
		
		group.setMembers(Collections.emptyList());
		group.setCreatedAt(LocalDate.now());
		
		return groupRepo.save(group);
	}

	@Override
	public void deleteGroup(Long id,Long requestUserId) {

		Group group = findById(id);
		
		if(group.getOwnerId() != requestUserId) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		if(!groupRepo.existsById(id)) {
			throw new IllegalArgumentException("Group not found");
		}
		
		spentClient.deleteSpents(id);
		groupRepo.deleteById(id);
		
	}

	@Override
	public Group updateGroup(UpdateGroupDto group,Long requestUserId) {
		
		Group groupFromDb = findById(group.id());
		
		if(groupFromDb.getOwnerId()!= requestUserId) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		groupFromDb.setName(group.name());
		groupFromDb.setDescription(group.description());
		
		return groupRepo.save(groupFromDb);
	}

		@Override
		public void addMembers(Long userId, List<Long> users, Long groupId) {
			
			Group group = findById(groupId);
			
			if(!userId.equals(group.getOwnerId())) {
				throw new IllegalArgumentException("Not allowed");
			}
			
			for(Long user : users) {
				if(!friendsClient.areFriends(userId, user)) {
					throw new IllegalArgumentException("Not allowed");
				}
				
				if(findByUserId(user).size() == 2) {
					throw new IllegalArgumentException("Someone has reached the limit of groups");
				}
			}
			
			
			List<Long> members = group.getMembers();
			
			Set<Long> existingMembers = new HashSet<>(members);
			
			for(Long user : users) {
				
				if(!existingMembers.contains(user)) {
					existingMembers.add(user);
				}

			}
			
			group.setMembers(new ArrayList<>(existingMembers));
			
			groupRepo.save(group);
		}

		@Override
		public void leaveGroup(Long groupId, Long userId) {
		    Group group = findById(groupId);
		    List<Long> members = new ArrayList<>(group.getMembers());

		    boolean isMember = members.contains(userId) || userId.equals(group.getOwnerId());
		    if (!isMember) {
		        throw new IllegalArgumentException("The user does not belong to this group");
		    }

		    if (userId.equals(group.getOwnerId())) {
		        if (members.isEmpty()) {
		        	spentClient.deleteSpents(groupId);
		            groupRepo.delete(group);
		            return;
		        }

		        Long newOwnerId = members.get(0);
		        group.setOwnerId(newOwnerId);
		        members.remove(newOwnerId);
		    } else {
		        members.remove(userId);
		    }

		    group.setMembers(members);
		    groupRepo.save(group);
		}


	
	}
