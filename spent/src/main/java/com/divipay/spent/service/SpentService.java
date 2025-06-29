package com.divipay.spent.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.divipay.spent.client.GroupClient;
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
	public Spent createSpent(Spent spent) {
		// Debo verificar que el usuario asignado al gasto entrante este en el grupo asignado al
		//gasto y que la lista de usuarios que comparten el gasto estan en el grupo asignado al gasto
		
		List<Long> members = groupClient.getMembersList(spent.getGroupId()).members();
		
		if(!members.contains(spent.getUserId())) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		if(!members.containsAll(spent.getMembers())) {
			throw new IllegalArgumentException("Unauthorized");
		}
		
		return spentRepo.save(spent);
	}

	@Override
	public void deleteSpent(Long id) {

		//Tengo que chequear que el usuario que hace la peticion para eliminar un gasto
		//esta en el grupo que se encuentra ese gasto
		
		if(!spentRepo.existsById(id)) {
			throw new IllegalArgumentException("Spent not found");
		}
		
		
		
	}

	@Override
	public Spent updateSpent(Spent spent) {
		// TODO Auto-generated method stub
		return null;
	}

}
