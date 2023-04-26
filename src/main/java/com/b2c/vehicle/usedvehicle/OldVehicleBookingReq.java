package com.b2c.vehicle.usedvehicle;

import com.b2c.vehicle.common.BaseRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Setter
@Getter
public class OldVehicleBookingReq extends BaseRequest implements Serializable {

    private static final long serialVersionUID = 6983849467846376083L;

    private OldVehicleBookingInfo oldVehicleBookingInfo;


}