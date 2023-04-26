package com.b2c.controller;

import com.b2c.vehicle.accessories.booking.AccessoriesBookingRequest;
import com.b2c.vehicle.accessories.booking.AccessoriesBookingResponse;
import com.b2c.vehicle.common.BaseFilter;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.helper.AccessoriesBookingHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"vehicle"})
public class AccessoriesBookingController {


    private final AccessoriesBookingHelper helper;

    public AccessoriesBookingController(AccessoriesBookingHelper helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"accessoriesBooking"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> accessoriesBookingSave(@RequestBody AccessoriesBookingRequest request) {
        BaseResponse baseResponse = helper.accessoriesBookingSave(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "accessoriesBooking", method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> accessoriesBookingUpdate(@RequestBody AccessoriesBookingRequest request) {
        BaseResponse baseResponse = helper.accessoriesBookingUpdate(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "accessoriesBooking/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getaccessoriesBooking(@PathVariable int id) {
        AccessoriesBookingResponse response = helper.getaccessoriesBooking(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "accessoriesBooking", method = {RequestMethod.GET})
    public ResponseEntity<?> getaccessoriesBookings(BaseFilter request) {
        AccessoriesBookingResponse response = helper.getaccessoriesBookings(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "accessoriesBooking/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<BaseResponse> accessoriesBookingDelete(@PathVariable int id) {
        BaseResponse baseResponse = helper.accessoriesBookingDelete(id);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }


}
