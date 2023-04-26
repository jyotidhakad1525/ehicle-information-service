package com.b2c.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.WarantyFilter;
import com.b2c.vehicle.helper.InsuranceAddOnHelper;
import com.b2c.vehicle.insurance.addon.InsuranceAddonRequest;
import com.b2c.vehicle.insurance.addon.InsuranceAddonResponse;

@RestController
@RequestMapping(value = {"vehicle"})
public class InsuranceAddOnController {


    private final InsuranceAddOnHelper helper;

    public InsuranceAddOnController(InsuranceAddOnHelper helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"insuranceAddOn"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> insuranceAddOnSave(@RequestBody InsuranceAddonRequest request) {
        BaseResponse baseResponse = helper.insuranceAddOnSave(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceAddOn", method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> insuranceAddOnUpdate(@RequestBody InsuranceAddonRequest request) {
        BaseResponse baseResponse = helper.insuranceAddOnUpdate(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceAddOn/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getinsuranceAddOn(@PathVariable int id) {
        InsuranceAddonResponse response = helper.getInsuranceAddOn(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceAddOn", method = {RequestMethod.GET})
    public ResponseEntity<?> getinsuranceAddOns(WarantyFilter request) {
        InsuranceAddonResponse response = helper.getInsuranceAddOns(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "insuranceAddOn/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<BaseResponse> insuranceAddOnDelete(@PathVariable int id) {
        BaseResponse baseResponse = helper.insuranceAddOnDelete(id);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }
}
