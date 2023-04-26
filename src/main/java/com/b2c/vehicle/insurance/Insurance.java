package com.b2c.vehicle.insurance;

import com.b2c.model.InsuranceDetails;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Insurance {
    private InsuranceDetails insuranceDetails;
    @JsonInclude(Include.NON_NULL)
    private VehicleBookinginfo vehicleDetails;
}