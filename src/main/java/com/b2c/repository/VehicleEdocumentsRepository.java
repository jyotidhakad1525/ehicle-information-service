package com.b2c.repository;

import com.b2c.model.VehicleEdocuments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleEdocumentsRepository extends JpaRepository<VehicleEdocuments, Integer> {

}
