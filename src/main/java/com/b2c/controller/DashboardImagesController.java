package com.b2c.controller;

import com.b2c.model.DashboardImage;
import com.b2c.services.DashboardImagesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Dashboard images information", description = "API to get Dashboard images information")
@RestController
@RequestMapping("/api/dashboardImages")
public class DashboardImagesController {

    private final DashboardImagesService dashboardImagesService;

    public DashboardImagesController(DashboardImagesService dashboardImagesService) {
        this.dashboardImagesService = dashboardImagesService;
    }

    @GetMapping(value = "/{vehicleId}", produces = "application/json")
    public ResponseEntity<List<DashboardImage>> getImagesByVehicle(
            @PathVariable(name = "vehicleId") Integer vehicleId) {
        return ResponseEntity.ok(dashboardImagesService.findAllByVehicleId(vehicleId));
    }

    @GetMapping(value = "/all/{organizationId}", produces = "application/json")
    public ResponseEntity<List<DashboardImage>> getIAllmagesByOrganization(
            @PathVariable(name = "organizationId") int organizationId) {
        return ResponseEntity.ok(dashboardImagesService.findAllByOrganizationId(organizationId));
    }

    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<DashboardImage>> getAllImages() {
        return ResponseEntity.ok(dashboardImagesService.findAll());
    }

}
