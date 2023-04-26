package com.b2c.repository;

import com.b2c.model.AccessoriesBooking;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface AccessoriesBookingRepository extends CrudRepository<AccessoriesBooking, Integer>,
        JpaSpecificationExecutor<AccessoriesBooking> {


}
