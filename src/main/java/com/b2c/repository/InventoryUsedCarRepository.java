package com.b2c.repository;

import com.b2c.model.InventoryUsedCar;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryUsedCarRepository
        extends CrudRepository<InventoryUsedCar, Long>, JpaSpecificationExecutor<InventoryUsedCar> {

    @Query(value = "SELECT * FROM inventory.inventory_used_car WHERE id = ?1 ", nativeQuery = true)
    Optional<InventoryUsedCar> findByUsedCarId(Long varientId);

}
