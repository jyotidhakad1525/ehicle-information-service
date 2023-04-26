package com.b2c.vehicle.insurance.addon;

import com.b2c.model.InsuranceAddOn;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddonInsurance {

    private InsuranceAddOn addOn;
    @JsonInclude(Include.NON_NULL)
    private VehicleBookinginfo vehicleDetails;


}