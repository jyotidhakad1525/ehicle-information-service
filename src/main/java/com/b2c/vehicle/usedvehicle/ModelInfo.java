package com.b2c.vehicle.usedvehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class ModelInfo {

    private Integer id;
    private Integer brandId;
    private String type;
    private String name;
    private String image_Url;
    private Integer created_By;
    private Integer modified_By;
    private String created_Date;
    private String modified_Date;
    private String status;
}