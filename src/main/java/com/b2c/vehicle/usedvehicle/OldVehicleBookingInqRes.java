package com.b2c.vehicle.usedvehicle;

import com.b2c.vehicle.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;


@Setter
@Getter
public class OldVehicleBookingInqRes extends BaseResponse implements Serializable {

    private static final long serialVersionUID = 8778416923749883695L;

    private List<OldVehicleBookingInqInfo> oldVehicleBookingInfos;
}