package com.b2c.controller;

import com.b2c.model.ServiceAppointment;
import com.b2c.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/serviceAppointment")
@Tag(name = "Vehicle service appointment API", description = "API to process service appointments")
public class ServiceAppointmentController {

    private final AppointmentService appointmentService;

    public ServiceAppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Book appointment", description = "Book appointment")
    public ResponseEntity<ServiceAppointment> saveAppointment(
            @RequestBody ServiceAppointment appointment) {
        return ResponseEntity.ok(appointmentService.saveAppointment(appointment));
    }

}
