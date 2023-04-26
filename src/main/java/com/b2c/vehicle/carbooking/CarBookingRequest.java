package com.b2c.vehicle.carbooking;

import com.b2c.model.CarBooking;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CarBookingRequest {
    @JsonInclude(Include.NON_NULL)
    CarBooking booking;

}
