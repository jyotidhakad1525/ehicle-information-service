package com.b2c.repository;

import com.b2c.model.OldVehicleBooking;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OldVehicleBookingRepository extends CrudRepository<OldVehicleBooking, Integer>,
        JpaSpecificationExecutor<OldVehicleBooking> {

    @Query(value = "SELECT * FROM old_vehicle_booking WHERE branch_id = ?1 and brand_id = ?2 and customer_id = ?3 and" +
            " organization_id = ?4", nativeQuery = true)
    List<OldVehicleBooking> getCarBookingDetails(Integer branchId, Integer brandId, Integer customerId,
                                                 Integer organizationId);

    @Query(value = "SELECT * FROM old_vehicle_booking WHERE branch_id = ?1  and organization_id = ?2", nativeQuery =
            true)
    List<OldVehicleBooking> getCarBookingDetailsByBranchId(Integer branchId, Integer organizationId);

    @Query(value = "SELECT * FROM old_vehicle_booking WHERE  brand_id = ?1 and  organization_id = ?2", nativeQuery =
            true)
    List<OldVehicleBooking> getCarBookingDetailsByBrandId(Integer brandId, Integer organizationId);

    @Query(value = "SELECT * FROM old_vehicle_booking WHERE customer_id = ?1 and organization_id = ?2", nativeQuery =
            true)
    List<OldVehicleBooking> getCarBookingDetailsByCustomerId(Integer customerId, Integer organizationId);

}
