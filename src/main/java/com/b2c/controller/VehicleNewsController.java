package com.b2c.controller;

import com.b2c.services.VehicleNewsService;
import com.b2c.vehicle.common.BaseFilter;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.news.VehicleNewsRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = {"vehicle"})
public class VehicleNewsController {

    private final VehicleNewsService helper;

    public VehicleNewsController(VehicleNewsService helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"news"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> vehicleNewsSave(@RequestBody VehicleNewsRequest request) {
        BaseResponse baseResponse = helper.save(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "news", method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> vehicleNewsUpdate(@RequestBody VehicleNewsRequest request) {
        BaseResponse baseResponse = helper.update(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "news/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getVehicleNews(@PathVariable int id) {
        return new ResponseEntity<>(helper.getNews(id), HttpStatus.OK);
    }

    @RequestMapping(value = "news", method = {RequestMethod.GET})
    public ResponseEntity<?> getVehicleNews(BaseFilter request) {
        return new ResponseEntity<>(helper.getNews(request), HttpStatus.OK);
    }

    @RequestMapping(value = "news/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<BaseResponse> vehicleNewsDelete(@PathVariable int id) {
        return new ResponseEntity<BaseResponse>(helper.delete(id), HttpStatus.OK);
    }


}
