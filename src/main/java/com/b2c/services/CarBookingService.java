package com.b2c.services;

import com.b2c.dto.CarBookingDTO;

import java.io.IOException;
import java.util.List;

public interface CarBookingService {

    List<CarBookingDTO> getCarBooking(Integer organizationId, Integer customerId);

    CarBookingDTO save(CarBookingDTO carBookingDTO) throws IOException;

}
