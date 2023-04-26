package com.b2c.controller;

import com.b2c.model.InsuranceDetails;
import com.b2c.model.InsuranceVarientMapping;
import com.b2c.model.VehicleOnRoadPrice;
import com.b2c.services.InsuranceDetailService;
import com.b2c.services.VehicleOnRoadPriceService;
import com.b2c.vehicle.common.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@Tag(name = "Vehicle OnRoad Price Details", description = "API to get vehicle price details")
@RequestMapping("/api/vehicle_on_road_prices")
public class VehicleOnRoadPriceController {

    private final InsuranceDetailService insuranceDetailService;
    private final VehicleOnRoadPriceService priceService;

    public VehicleOnRoadPriceController(InsuranceDetailService insuranceDetailService,
                                        VehicleOnRoadPriceService priceService) {
        this.insuranceDetailService = insuranceDetailService;
        this.priceService = priceService;
    }

    @GetMapping("{varientId}/{organizationId}")
    public ResponseEntity<VehicleOnRoadPrice> getPriceDetails(@PathVariable Integer varientId,
                                                              @PathVariable Integer organizationId) {
        Optional<VehicleOnRoadPrice> priceDetailsOpt = priceService.getPriceDetails(varientId, organizationId);
        if (priceDetailsOpt.isPresent()) {
            VehicleOnRoadPrice vehicleOnRoadPrice = priceDetailsOpt.get();
            Set<InsuranceVarientMapping> insurance_vareint_mapping = vehicleOnRoadPrice.getInsurance_vareint_mapping();

            for (InsuranceVarientMapping insurance_data : insurance_vareint_mapping) {
                Optional<InsuranceDetails> insuranceDetails =
                        insuranceDetailService.getById(insurance_data.getInsurence_id());
                if (insuranceDetails.isPresent())
                    insurance_data.setPolicy_name(insuranceDetails.get().getPolicy_name());
            }
            vehicleOnRoadPrice.setInsurance_vareint_mapping(insurance_vareint_mapping);
            return ResponseEntity.ok(vehicleOnRoadPrice);


        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @RequestMapping(value = {"onRoadPrice"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> saveVehicleOnRoadPrice(@RequestBody VehicleOnRoadPrice request) {
        BaseResponse baseResponse = priceService.saveVehicleOnRoadPrice(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = {"onRoadPrice"}, method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> updateVehicleOnRoadPrice(@RequestBody VehicleOnRoadPrice request) {
        BaseResponse baseResponse = priceService.updateVehicleOnRoadPrice(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

}
