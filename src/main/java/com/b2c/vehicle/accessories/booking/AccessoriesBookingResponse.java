package com.b2c.vehicle.accessories.booking;

import com.b2c.vehicle.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class AccessoriesBookingResponse extends BaseResponse {
    @JsonInclude(Include.NON_NULL)
    private Accessories accessoriesInfo;
    @JsonInclude(Include.NON_NULL)
    private List<Accessories> accessories;

}
