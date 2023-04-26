package com.b2c.vehicle.news;

import com.b2c.model.VehicleNews;
import com.b2c.vehicle.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class VehicleNewsResponse extends BaseResponse {
    private VehicleNews news;
    private List<VehicleNews> vehicleNews;

}
