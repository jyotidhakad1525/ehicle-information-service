package com.b2c.services;

import com.b2c.model.ServiceAppointment;
import com.b2c.repository.ServiceAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    @Autowired
    private ServiceAppointmentRepository repository;

    public ServiceAppointment saveAppointment(ServiceAppointment appointment) {
        return repository.save(appointment);
    }

}
