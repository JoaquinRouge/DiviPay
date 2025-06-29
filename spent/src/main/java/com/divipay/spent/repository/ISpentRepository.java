package com.divipay.spent.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.divipay.spent.model.Spent;

@Repository
public interface ISpentRepository extends JpaRepository<Spent,Long>{

	Optional<List<Spent>> findByUserId(Long id);
	Optional<List<Spent>> findByGroupId(Long id);
	
}
