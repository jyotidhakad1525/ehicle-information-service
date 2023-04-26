package com.b2c.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.b2c.model.Maker;

@Repository
public interface MakerRepository extends CrudRepository<Maker, Integer> {
	
	  @Query(value = "SELECT * FROM oem WHERE org_id = ?1", nativeQuery = true)
	    List<Maker> getAllMakers(Integer orgId);
	    
	
	

}
