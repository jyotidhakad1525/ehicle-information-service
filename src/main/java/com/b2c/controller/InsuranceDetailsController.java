package com.b2c.controller;

import com.b2c.vehicle.common.BaseFilter;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.helper.InsuranceDetailsHelper;
import com.b2c.vehicle.insurance.InsuranceDetailsRequest;
import com.b2c.vehicle.insurance.InsuranceDetailsResponse;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = {"vehicle"})
public class InsuranceDetailsController {

    private final InsuranceDetailsHelper helper;

    public InsuranceDetailsController(InsuranceDetailsHelper helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"insuranceDetails"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> insuranceDetailsSave(@RequestBody InsuranceDetailsRequest request) {
        BaseResponse baseResponse = helper.insuranceDetailsSave(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceDetails", method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> insuranceDetailsUpdate(@RequestBody InsuranceDetailsRequest request) {
        BaseResponse baseResponse = helper.insuranceDetailsUpdate(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceDetails/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getinsuranceDetails(@PathVariable int id) {
        InsuranceDetailsResponse response = helper.getInsuranceDetail(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceDetails", method = {RequestMethod.GET})
    public ResponseEntity<?> getinsuranceDetailss(BaseFilter request) {
        InsuranceDetailsResponse response = helper.getInsuranceDetails(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceDetails/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<BaseResponse> insuranceDetailsDelete(@PathVariable int id) {
        BaseResponse baseResponse = helper.insuranceDetailsDelete(id);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }
}
