package com.b2c.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.b2c.model.VehicleVarient;

public interface VehicleVarientRepository
		extends CrudRepository<VehicleVarient, Integer>, JpaRepository<VehicleVarient, Integer> {

	  List<VehicleVarient> findByName(String varient);

	    VehicleVarient findByIdAndVehicleId(int id, int vehicleId);
	    
	    @Query(value = "SELECT * FROM vehicle_varient_new WHERE org_id = ?1 and name=?2 and vehicle_id=?3", nativeQuery = true)
		List<VehicleVarient> findByVariantNameModelId(int orgId, String name,Integer vehicleId);
	    
	    @Query(value = "SELECT * FROM vehicle_varient_new WHERE org_id = ?1 and name=?2", nativeQuery = true)
		List<VehicleVarient> findByVariantName(int orgId, String name);



}
