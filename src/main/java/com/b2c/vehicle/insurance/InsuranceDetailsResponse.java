package com.b2c.vehicle.insurance;

import com.b2c.model.InsuranceDetails;
import com.b2c.vehicle.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InsuranceDetailsResponse extends BaseResponse {
    @JsonInclude(Include.NON_NULL)
    private InsuranceDetails insurance;
    @JsonInclude(Include.NON_NULL)
    private List<InsuranceDetails> insurances;

}
