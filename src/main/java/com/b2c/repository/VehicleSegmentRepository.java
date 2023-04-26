package com.b2c.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.b2c.model.VehicleSegment;

@Repository
public interface VehicleSegmentRepository extends CrudRepository<VehicleSegment, Integer> {

	@Query(value = "SELECT * FROM vehicle_segment WHERE org_id = ?1", nativeQuery = true)
    List<VehicleSegment> findAllById(int organizationId);
}
