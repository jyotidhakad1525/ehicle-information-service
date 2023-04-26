package com.b2c.vehicle.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WarantyFilter extends BaseFilter {
    private Integer vehicle_id;
    private Integer varient_id;

}
