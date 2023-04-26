package com.b2c.controller;

import com.b2c.services.OldVehicleBookingService;
import com.b2c.vehicle.common.BaseFilter;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.usedvehicle.OldVehicleBookingInqRes;
import com.b2c.vehicle.usedvehicle.OldVehicleBookingReq;
import com.b2c.vehicle.usedvehicle.OldVehicleBookingRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Old Vehicle Booking Details", description = "API to get Old Vehicle Booking Details")
@RequestMapping("/api/oldVehicleBooking")
public class OldVehicleBookingController {

    private final OldVehicleBookingService oldVehicleBookingService;

    public OldVehicleBookingController(OldVehicleBookingService oldVehicleBookingService) {
        this.oldVehicleBookingService = oldVehicleBookingService;
    }

    @RequestMapping(value = {"saveOrUpdateDetails"}, method = {RequestMethod.POST})
    @Operation(summary = "Save Or Update Old vehicle details", description = "Save Or Update Old vehicle Booking " +
            "details")
    public ResponseEntity<BaseResponse> saveOldVehicleBooking(@RequestBody OldVehicleBookingReq oldVehicleBookingReq) {

        OldVehicleBookingRes oldVehicleBookingRes = oldVehicleBookingService.saveCarBooking(oldVehicleBookingReq);

        if (oldVehicleBookingRes == null
                || !HttpStatus.OK.toString().equalsIgnoreCase(oldVehicleBookingRes.getStatusCode())) {
            return new ResponseEntity<BaseResponse>(oldVehicleBookingRes, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<BaseResponse>(oldVehicleBookingRes, HttpStatus.OK);
    }

    @DeleteMapping("deleteById/{id}")
    @Operation(summary = "Delete Old vehicle details", description = "Delete Old vehicle Booking details")
    public ResponseEntity<BaseResponse> deleteOldVehicleBooking(@PathVariable String id) {

        BaseResponse baseResponse = oldVehicleBookingService.deleteOldVehicleBooking(id);

        if (baseResponse == null
                || !HttpStatus.OK.toString().equalsIgnoreCase(baseResponse.getStatusCode())) {
            return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "details", method = {RequestMethod.GET})
    public ResponseEntity<?> getcarBooking(BaseFilter request) {
        OldVehicleBookingInqRes response = oldVehicleBookingService.getOldCarBookingDetails(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
