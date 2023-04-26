package com.b2c.mapper;

import com.b2c.dto.CarBookingDTO;
import com.b2c.model.CarBooking;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarBookingMapper implements GenericMapper<CarBookingDTO, CarBooking> {

    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CarBooking convertFromDTOToEntity(CarBookingDTO carBookingDTO) throws JsonProcessingException {
        CarBooking carBooking = null;
        /*
         * String insurenceAddOnJson =
         * mapper.writeValueAsString(carBookingDTO.getInsurenceAddOn()); String
         * offersJason = mapper.writeValueAsString(carBookingDTO.getOffers());
         * CarBooking carBooking = (CarBooking) this.modelMapper.map(carBookingDTO,
         * CarBooking.class); carBooking.setInsurenceAddOn(insurenceAddOnJson);
         * carBooking.setOffers(offersJason);
         */
        return carBooking;
    }

    @Override
    public CarBookingDTO convertFromEntityToDTO(CarBooking carBooking) {

        CarBookingDTO carBookingDTO = modelMapper.map(carBooking, CarBookingDTO.class);

        return carBookingDTO;
    }

}
