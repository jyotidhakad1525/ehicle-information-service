package com.b2c.repository;

import com.b2c.model.Gallery;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GalleryRepository extends CrudRepository<Gallery, Integer> {

    List<Gallery> findAllByStatusTrue();

    List<Gallery> findAllByOrganizationIdAndStatusTrue(String organizationId);

    List<Gallery> findAllByVehicleIdAndStatusTrue(Integer vehicleId);

}