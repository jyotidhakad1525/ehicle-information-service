package com.b2c.vehicle.waranty;

import com.b2c.model.ExtendedWaranty;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Waranty {

    private ExtendedWaranty waranty;
    @JsonInclude(Include.NON_NULL)
    private VehicleBookinginfo vehicleDetails;

}