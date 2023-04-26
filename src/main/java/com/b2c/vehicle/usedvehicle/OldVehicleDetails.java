package com.b2c.vehicle.usedvehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class OldVehicleDetails {

    private List<BrandInfo> brand;
    private List<ModelInfo> model;


}