package com.b2c.model;

import com.b2c.vehicle.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DemoVehicleResponse extends BaseResponse {
    private List<DemoVehicle> vehicles;

}
