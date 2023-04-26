package com.b2c.repository;

import com.b2c.model.CarBooking;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CarBookingRepository extends CrudRepository<CarBooking, Integer>,
        JpaSpecificationExecutor<CarBooking> {

    List<CarBooking> findAllByOrganizationIdAndCustomerId(Integer organizationId, Integer customerId);

}
