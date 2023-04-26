package com.b2c.vehicle.usedvehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class BrandInfo {
    private Integer id;
    private String name;
    private String image_Url;
    private String vehicleType;


}