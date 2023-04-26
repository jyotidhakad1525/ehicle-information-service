package com.b2c.repository;

import com.b2c.model.VehicleDetails;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface VehicleDetailsRepository extends CrudRepository<VehicleDetails, Integer> {


	@Query(value = "SELECT * FROM vehicle_details_new WHERE organization_id = :organizationId and status = 'Active'", nativeQuery = true)
    List<VehicleDetails> findAllById(int organizationId);
    
    @Query(value = "SELECT * FROM vehicle_details_new WHERE organization_id = ?1 and id =?2", nativeQuery = true)
    List<VehicleDetails> getVehicle(Integer organisationId, Integer id);
    
    @Query(value = "SELECT * FROM vehicle_details_new WHERE organization_id = ?1 and model =?2", nativeQuery = true)
    List<VehicleDetails> getVehiclesByName(Integer organisationId, String model);

    @Modifying
    @Transactional
    @Query(value = "UPDATE vehicle_details_new SET status = 'InActive' WHERE id = :vehicleId", nativeQuery = true)
    void deleteByvehicleId(int vehicleId);

    VehicleDetails findByVehicleIdAndOrganizationId(int id, int orgId);
    
    @Query(value = "SELECT * FROM vehicle_details_new WHERE organization_id = ?1 and model =?2", nativeQuery = true)
    List<VehicleDetails> getVehicleByModel(Integer organisationId, String model);
    
    @Query(value = "SELECT * FROM vehicle_details_new WHERE organization_id = ?1 and maker=?2", nativeQuery = true)
    List<VehicleDetails> getModelByMaker(Integer organisationId,String maker);

    @Query(value =  "select vehicle_varient_new.id from vehicle_details_new Inner join vehicle_varient_new On vehicle_varient_new.vehicle_id = vehicle_details_new.id where vehicle_details_new.model =:modelName and vehicle_varient_new.name =:variantName and vehicle_details_new.organization_id =:org_id",  nativeQuery = true)
    Integer getRecordByModelVariant(String modelName, String variantName, String org_id);
}
