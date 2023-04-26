package com.b2c.services;

import com.b2c.model.DashboardImage;
import com.b2c.repository.DashboardImagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardImagesService {

    @Autowired
    private DashboardImagesRepository dashboardImagesRepository;

    public Optional<DashboardImage> findById(Integer dashboardImageId) {
        return dashboardImagesRepository.findById(dashboardImageId);
    }

    public List<DashboardImage> findAll() {
        return dashboardImagesRepository.findAllByStatusTrue();
    }

    public List<DashboardImage> findAllByOrganizationId(int organizationId) {
        return dashboardImagesRepository.findAllByOrganizationIdAndStatusTrue(organizationId);
    }

    public List<DashboardImage> findAllByVehicleId(Integer vehicleId) {
        return dashboardImagesRepository.findAllByVehicleIdAndStatusTrue(vehicleId);
    }

}
