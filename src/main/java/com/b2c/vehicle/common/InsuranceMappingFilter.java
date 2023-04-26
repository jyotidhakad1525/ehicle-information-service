package com.b2c.vehicle.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InsuranceMappingFilter {
    private Integer id;
    private Integer varient_id;
    private Integer insurence_id;
    private String status;
    private Integer offset;
    private Integer limit;

}
