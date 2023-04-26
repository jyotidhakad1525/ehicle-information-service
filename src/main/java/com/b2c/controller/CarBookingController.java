package com.b2c.controller;

import com.b2c.vehicle.carbooking.CarBookingRequest;
import com.b2c.vehicle.carbooking.CarBookingResponse;
import com.b2c.vehicle.common.BaseFilter;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.helper.CarBookingHelper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Car Booking information", description = "API to get Car Booking information")
@RestController
public class CarBookingController {

    private static final Logger logger = LoggerFactory.getLogger(CarBookingController.class);

    private final CarBookingHelper helper;

    public CarBookingController(CarBookingHelper helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"carBooking"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> saveCarBooking(@RequestBody CarBookingRequest request) {
        BaseResponse baseResponse = helper.saveCarBooking(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = {"carBooking"}, method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> updateCarBooking(@RequestBody CarBookingRequest request) {
        BaseResponse baseResponse = helper.updateCarBooking(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "carBooking/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getCarBooking(@PathVariable int id) {
        CarBookingResponse response = helper.getCarBooking(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "carBooking", method = {RequestMethod.GET})
    public ResponseEntity<?> getcarBooking(BaseFilter request) {
        CarBookingResponse response = helper.getCarBookings(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
