package com.b2c.repository;

import com.b2c.model.VehicleNews;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface VehicleNewsRepository extends CrudRepository<VehicleNews, Integer>,
        JpaSpecificationExecutor<VehicleNews> {


}
