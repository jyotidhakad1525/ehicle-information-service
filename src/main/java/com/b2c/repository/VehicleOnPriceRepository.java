package com.b2c.repository;

import com.b2c.model.VehicleOnRoadPrice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface VehicleOnPriceRepository extends CrudRepository<VehicleOnRoadPrice, Integer> {

    @Query(value = "SELECT * FROM vehicle_on_road_price WHERE varient_id = ?1 and organization_id = ?2", nativeQuery
            = true)
    Optional<VehicleOnRoadPrice> findByVehicleVarientId(Integer varientId, Integer orgnizationId);
}
