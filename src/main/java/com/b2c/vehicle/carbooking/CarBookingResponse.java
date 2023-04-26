package com.b2c.vehicle.carbooking;

import com.b2c.dto.CarBookingDTO;
import com.b2c.vehicle.common.BaseResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CarBookingResponse extends BaseResponse {

    @JsonInclude(Include.NON_NULL)
    List<CarBookingDTO> bookings;
    @JsonInclude(Include.NON_NULL)
    CarBookingDTO booking;

}
