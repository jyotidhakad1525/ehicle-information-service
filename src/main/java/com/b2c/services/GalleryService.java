package com.b2c.services;

import com.b2c.model.Gallery;
import com.b2c.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GalleryService {

    @Autowired
    private GalleryRepository galleryRepository;

    public Optional<Gallery> findById(Integer galleryId) {
        return galleryRepository.findById(galleryId);
    }

    public List<Gallery> findAll() {
        return galleryRepository.findAllByStatusTrue();
    }

    public List<Gallery> findAllByOrganizationId(String organizationId) {
        return galleryRepository.findAllByOrganizationIdAndStatusTrue(organizationId);
    }

    public List<Gallery> findAllByVehicleId(Integer vehicleId) {
        return galleryRepository.findAllByVehicleIdAndStatusTrue(vehicleId);
    }

    public Gallery saveGallery(Gallery gallery) {

        return galleryRepository.save(gallery);
    }

}
