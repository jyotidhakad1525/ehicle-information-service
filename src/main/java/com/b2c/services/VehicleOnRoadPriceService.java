package com.b2c.services;

import com.b2c.model.VehicleOnRoadPrice;
import com.b2c.repository.VehicleOnPriceRepository;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleOnRoadPriceService {

    @Autowired
    private VehicleOnPriceRepository repository;

    public Optional<VehicleOnRoadPrice> getPriceDetails(Integer varient_id, Integer organization_id) {

        return repository.findByVehicleVarientId(varient_id, organization_id);
        // return repository.findByVehicleVarient_Id(varientId);
    }

    public BaseResponse saveVehicleOnRoadPrice(VehicleOnRoadPrice request) {
        VehicleOnRoadPrice onRoadPrice = repository.save(request);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(onRoadPrice.getId() + "");
        return successResponse;
    }

    public BaseResponse updateVehicleOnRoadPrice(VehicleOnRoadPrice request) {
        VehicleOnRoadPrice onRoadPrice = repository.save(request);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(onRoadPrice.getId() + "");
        return successResponse;
    }
}
