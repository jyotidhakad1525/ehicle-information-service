package com.b2c.vehicle.insurance.addon;

import com.b2c.vehicle.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InsuranceAddonResponse extends BaseResponse {
    @JsonInclude(Include.NON_NULL)
    private AddonInsurance insuranceAddon;
    @JsonInclude(Include.NON_NULL)
    private List<AddonInsurance> insuranceAddons;

}
