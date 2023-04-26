package com.b2c.repository;

import com.b2c.model.DashboardImage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DashboardImagesRepository extends CrudRepository<DashboardImage, Integer> {

    List<DashboardImage> findAllByStatusTrue();

    List<DashboardImage> findAllByOrganizationIdAndStatusTrue(int organizationId);

    List<DashboardImage> findAllByVehicleIdAndStatusTrue(Integer vehicleId);

}
