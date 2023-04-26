package com.b2c.services;

import com.b2c.dto.VehicleDetailsDTO;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleDetailsReq;

import java.util.List;

public interface VehicleDetailsNService {

    List<VehicleDetailsDTO> getAllVehicles(int organizationId);
    VehicleDetails saveVehicleNew(VehicleDetailsReq vecDetails);

}
