package com.b2c.services;

import com.b2c.model.VehicleEdocuments;
import com.b2c.repository.VehicleEdocumentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VehicleEdocumentsService {

    @Autowired
    private VehicleEdocumentsRepository vehicleEdocumentsRepository;

    public VehicleEdocuments create(VehicleEdocuments vehicleEdocuments) {

        return vehicleEdocumentsRepository.save(vehicleEdocuments);
    }

}
