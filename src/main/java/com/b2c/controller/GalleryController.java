package com.b2c.controller;

import com.b2c.model.Gallery;
import com.b2c.services.GalleryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Gallery information", description = "API to get Gallery information")
@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

    private final GalleryService galleryService;

    public GalleryController(GalleryService galleryService) {
        this.galleryService = galleryService;
    }

    @GetMapping(value = "/{vehicleId}", produces = "application/json")
    public ResponseEntity<List<Gallery>> getImagesByVehicle(@PathVariable(name = "vehicleId") Integer vehicleId) {
        return ResponseEntity.ok(galleryService.findAllByVehicleId(vehicleId));
    }

    @GetMapping(value = "/all/{organizationId}", produces = "application/json")
    public ResponseEntity<List<Gallery>> getIAllmagesByOrganization(
            @PathVariable(name = "organizationId") String organizationId) {
        return ResponseEntity.ok(galleryService.findAllByOrganizationId(organizationId));
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<Gallery>> getAllImages() {
        return ResponseEntity.ok(galleryService.findAll());
    }

}
