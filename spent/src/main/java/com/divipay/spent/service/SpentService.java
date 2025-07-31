package com.divipay.spent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.divipay.spent.client.GroupClient;
import com.divipay.spent.dto.UpdateSpentDto;
import com.divipay.spent.model.Spent;
import com.divipay.spent.repository.ISpentRepository;

@Service
public class SpentService implements ISpentService {

	private ISpentRepository spentRepo;
	private GroupClient groupClient;
	
	public SpentService(ISpentRepository spentRepo,GroupClient groupClient) {
		this.spentRepo = spentRepo;
		this.groupClient = groupClient;
	}
	
	@Override
	public Spent findById(Long id) {
		return spentRepo.findById(id).orElseThrow(()->
				new IllegalArgumentException("Spent not found"));
	}

	@Override
	public List<Spent> findByUserId(Long id) {
		return spentRepo.findByUserId(id).orElseThrow(()->
		new IllegalArgumentException("Spent not found"));
	}

	@Override
	public List<Spent> findByGroupId(Long id) {
		return spentRepo.findByGroupId(id).orElseThrow(()->
		new IllegalArgumentException("Spent not found"));
	}

	@Override
	public double findTotal(Long groupId) {
		List<Spent> list = findByGroupId(groupId);
		
		double total = 0;
		
		for(Spent s : list) {
			total += s.getAmount();
		}
		
		return total;
	}
	
	@Override
	public Spent createSpent(Spent spent) {
		// Debo verificar que el usuario asignado al gasto entrante este en el grupo asignado al
		//gasto y que la lista de usuarios que comparten el gasto estan en el grupo asignado al gasto
		
		List<Long> members = groupClient.getMembersList(spent.getGroupId());
		
		if(!members.contains(spent.getUserId())) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		if(!members.containsAll(spent.getMembers())) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		if(spent.getMembers().isEmpty()) {
			spent.setMembers(members);
		}
		
		return spentRepo.save(spent);
	}

	@Override
	public void deleteSpent(Long id,Long requestUserId) {

		//Tengo que chequear que el usuario que hace la peticion para eliminar un gasto
		//es el dueño del grupo en el que esta el gasto
		
		if(!spentRepo.existsById(id)) {
			throw new IllegalArgumentException("Spent not found");
		}
		
		Spent spent = findById(id);
		
		Long owner = groupClient.getOwner(spent.getGroupId());
		
		if(requestUserId != owner) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		spentRepo.deleteById(id);
		
	}

	@Override
	public Spent updateSpent(UpdateSpentDto spent,Long requestUserId) {

		Spent spentFromDb = findById(spent.id());
		
		if(requestUserId != spentFromDb.getUserId()) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		spentFromDb.setAmount(spent.amount());
		spentFromDb.setDescription(spent.description());
		
		return spentRepo.save(spentFromDb);
	}

	@Override
	public void deleteAllSpents(Long groupId,Long requestUserId) {
		
		Long ownerId = groupClient.getOwner(groupId);
		
		if(!requestUserId.equals(ownerId)) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		List<Spent> spents = findByGroupId(groupId);
		
		if(spents.isEmpty()) {
			return;	
		}
		
		for(Spent spent : spents) {
			spentRepo.deleteById(spent.getId());
		}
		
	}

}
