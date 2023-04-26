package com.b2c.vehicle.carbooking;

import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleImage;
import com.b2c.model.VehicleVarient;
import com.b2c.vehicle.common.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VehicleBookinginfo {
    @JsonInclude(Include.NON_NULL)
    VehicleDetails vehicleDetails;
    @JsonInclude(Include.NON_NULL)
    VehicleVarient varient;
    @JsonInclude(Include.NON_NULL)
    VehicleImage colorInfo;
    @JsonInclude(Include.NON_NULL)
    private Person customerInfo;


}
