package com.b2c.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.b2c.model.VehicleImage;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, Integer> {
	
	@Query(value = "SELECT * FROM vehicle_image_color WHERE varient_id = ?1", nativeQuery = true)
	List<VehicleImage> findByVarient_id(int varient_id);
	@Query(value = "SELECT * FROM vehicle_image_color WHERE varient_id = ?1", nativeQuery = true)
	Set<VehicleImage> findByVarientId(int varient_id);

	@Query(value = "SELECT * FROM `vehicle-management`.vehicle_image_color where vehicle_id =:vehicle_id and varient_id =:varient_id and color =:color", nativeQuery = true)
	Integer findIdByVariantModelColorName(int vehicle_id, int varient_id, String color);
}
