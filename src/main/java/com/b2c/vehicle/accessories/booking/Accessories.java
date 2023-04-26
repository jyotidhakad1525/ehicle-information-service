package com.b2c.vehicle.accessories.booking;

import com.b2c.model.AccessoriesBooking;
import com.b2c.model.VehicleDetails;
import com.b2c.vehicle.common.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Accessories {

    private AccessoriesBooking accessoriesBooking;
    @JsonInclude(Include.NON_NULL)
    private VehicleDetails vehicleDetails;

    @JsonInclude(Include.NON_NULL)
    private Person customerInfo;


}