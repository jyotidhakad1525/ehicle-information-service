package com.b2c.repository;

import com.b2c.model.ExtendedWaranty;
import com.b2c.model.VehicleImage;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface ExtendedWarantyRepository extends CrudRepository<ExtendedWaranty, Integer>,
        JpaSpecificationExecutor<ExtendedWaranty> {

    @Query(value = " select * from extended_warranty where organization_id =:orgId  and vehicle_id =:vehicle_id and varient_id =:varient_id", nativeQuery = true)
    Set<ExtendedWaranty> findByVariantModelOrgId(String orgId, Integer vehicle_id, Integer varient_id);
}
