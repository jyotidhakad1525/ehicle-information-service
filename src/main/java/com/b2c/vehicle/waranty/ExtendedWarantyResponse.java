package com.b2c.vehicle.waranty;

import com.b2c.vehicle.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ExtendedWarantyResponse extends BaseResponse {
    @JsonInclude(Include.NON_NULL)
    private Waranty waranty;

    @JsonInclude(Include.NON_NULL)
    private List<Waranty> waranties;

}
