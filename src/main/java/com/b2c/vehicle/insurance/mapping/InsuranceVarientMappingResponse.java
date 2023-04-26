package com.b2c.vehicle.insurance.mapping;

import com.b2c.model.InsuranceVarientMapping;
import com.b2c.vehicle.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class InsuranceVarientMappingResponse extends BaseResponse {

    private InsuranceVarientMapping insuranceMapping;
    private List<InsuranceVarientMapping> insuranceMappingss;

}
