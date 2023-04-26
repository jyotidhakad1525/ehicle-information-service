package com.b2c.controller;

import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.InsuranceMappingFilter;
import com.b2c.vehicle.helper.InsuranceVarientMappingHelper;
import com.b2c.vehicle.insurance.mapping.InsuranceVarientMappingRequest;
import com.b2c.vehicle.insurance.mapping.InsuranceVarientMappingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"vehicle"})
public class InsuranceVariantMappingController {


    private final InsuranceVarientMappingHelper helper;

    public InsuranceVariantMappingController(InsuranceVarientMappingHelper helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"insuranceMapping"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> insuranceMappingSave(@RequestBody InsuranceVarientMappingRequest request) {
        BaseResponse baseResponse = helper.insuranceVarientMappingSave(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceMapping", method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> insuranceMappingUpdate(@RequestBody InsuranceVarientMappingRequest request) {
        BaseResponse baseResponse = helper.insuranceVarientMappingUpdate(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceMapping/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getInsuranceMapping(@PathVariable int id) {
        InsuranceVarientMappingResponse response = helper.getInsuranceVarientMapping(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceMapping", method = {RequestMethod.GET})
    public ResponseEntity<?> getInsuranceMappings(InsuranceMappingFilter request) {
        InsuranceVarientMappingResponse response = helper.getInsuranceVarientMappings(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceMapping/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<BaseResponse> insuranceMappingDelete(@PathVariable int id) {
        BaseResponse baseResponse = helper.insuranceVarientMappingDelete(id);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }


}
