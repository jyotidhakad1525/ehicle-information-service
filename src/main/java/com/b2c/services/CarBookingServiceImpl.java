package com.b2c.services;

import com.b2c.dto.CarBookingDTO;
import com.b2c.mapper.CarBookingMapper;
import com.b2c.model.CarBooking;
import com.b2c.repository.CarBookingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarBookingServiceImpl implements CarBookingService {

    @Autowired
    CarBookingRepository carBookingRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CarBookingMapper carBookingMapper;

    @Override
    public List<CarBookingDTO> getCarBooking(Integer organizationId, Integer customerId) {
        List<CarBooking> carBookings = carBookingRepository.findAllByOrganizationIdAndCustomerId(organizationId,
                customerId);

        if (Objects.nonNull(carBookings)) {

            return carBookings.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

        }
        return null;
    }

    private CarBookingDTO convertToDto(CarBooking carBooking) {
        return carBookingMapper.convertFromEntityToDTO(carBooking);
    }

    @Override
    public CarBookingDTO save(CarBookingDTO carBookingDTO) throws IOException {

        CarBooking carBooking = carBookingMapper.convertFromDTOToEntity(carBookingDTO);

        if (Objects.nonNull(carBooking)) {
            carBooking = carBookingRepository.save(carBooking);
        }

        return carBookingMapper.convertFromEntityToDTO(carBooking);
    }

}
