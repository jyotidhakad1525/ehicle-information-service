package com.b2c.controller;

import com.b2c.dto.VehicleDetailsDTO;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleDetailsReq;
import com.b2c.services.GalleryService;
import com.b2c.services.VehicleDetailsNService;
import com.b2c.services.VehicleEdocumentsService;
import com.b2c.util.AWSS3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vehicle_details")
@Tag(name = "Vehicle Details", description = "API to get Vehicle Details")
public class VehicleDetailsNController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleDetailsNController.class);
    private final VehicleDetailsNService vehicleService;

    private final AWSS3Service aWSS3Service;

    private final VehicleEdocumentsService vehicleEdocumentsService;

    private final GalleryService galleryService;

    public VehicleDetailsNController(VehicleDetailsNService vehicleService, AWSS3Service aWSS3Service,
                                     VehicleEdocumentsService vehicleEdocumentsService, GalleryService galleryService) {
        this.vehicleService = vehicleService;
        this.aWSS3Service = aWSS3Service;
        this.vehicleEdocumentsService = vehicleEdocumentsService;
        this.galleryService = galleryService;
    }

    @Operation(summary = "Get all Vehicle details", description = "Get all Vehicle details")
    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity<List<VehicleDetailsDTO>> getAllVehicles(@RequestParam("organizationId") int organizationId) {
        logger.info("getting all vehicles");
        List<VehicleDetailsDTO> vehicles = vehicleService.getAllVehicles(organizationId);
        return ResponseEntity.ok(vehicles);
    }
    @Operation(summary = "add  Vehicle details", description = "add new Vehicle details")
    @PostMapping(value = "/save", produces = "application/json")
    public ResponseEntity<VehicleDetails> saveVehiclesNe(@RequestBody VehicleDetailsReq vehicleDetailsReq) {
        logger.info("getting all vehicles");
        VehicleDetails vehicles = vehicleService.saveVehicleNew(vehicleDetailsReq);
        return ResponseEntity.ok(vehicles);
    }

}
