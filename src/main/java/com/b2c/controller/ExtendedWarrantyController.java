package com.b2c.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.b2c.model.ExtendedWaranty;
import com.b2c.repository.ExtendedWarantyRepository;
import com.b2c.vehicle.common.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.b2c.model.BulkUploadResponse;
import com.b2c.vehicle.common.BaseResponse;
import com.b2c.vehicle.common.WarantyFilter;
import com.b2c.vehicle.helper.ExtendedWarrantyHelper;
import com.b2c.vehicle.waranty.ExtendedWarantyRequest;
import com.b2c.vehicle.waranty.ExtendedWarantyResponse;

@RestController
@RequestMapping(value = {"vehecle"})
public class ExtendedWarrantyController {


    private final ExtendedWarrantyHelper helper;

    @Autowired
    ExtendedWarantyRepository extendedWarantyRepository;
   
    public ExtendedWarrantyController(ExtendedWarrantyHelper helper) {
        this.helper = helper;
    }

    @RequestMapping(value = {"extendedWarranty"}, method = {RequestMethod.POST})
    public ResponseEntity<BaseResponse> extendedWarantySave(@RequestBody ExtendedWarantyRequest request) {

        BaseResponse baseResponse;
        Set<ExtendedWaranty> waranties = extendedWarantyRepository.findByVariantModelOrgId(String.valueOf(request.getExtendedWaranty().getOrganization_id()),
                request.getExtendedWaranty().getVehicle_id().intValue(), request.getExtendedWaranty().getVarient_id());

        if(waranties!=null && waranties.size()>0){
            baseResponse =  Utils.failureResponse("Record is already created for selected model and variant");
            return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
        } else {
            baseResponse = helper.extendedWarantySave(request);
        }


//        BaseResponse baseResponse = helper.extendedWarantySave(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "extendedWarranty", method = {RequestMethod.PUT})
    public ResponseEntity<BaseResponse> extendedWarantyUpdate(@RequestBody ExtendedWarantyRequest request) {


        BaseResponse baseResponse;
        Set<ExtendedWaranty> waranties = extendedWarantyRepository.findByVariantModelOrgId(String.valueOf(request.getExtendedWaranty().getOrganization_id()),
                request.getExtendedWaranty().getVehicle_id().intValue(), request.getExtendedWaranty().getVarient_id());

        List<ExtendedWaranty> warrantyList = new ArrayList<ExtendedWaranty>(waranties);

        if(warrantyList!=null && warrantyList.size()>0){
            if(warrantyList.size()==1 && Objects.equals(warrantyList.get(0).getId(), request.getExtendedWaranty().getId())) {
                baseResponse = helper.extendedWarantyUpdate(request);
            }else {
                baseResponse =  Utils.failureResponse("Record is already created for selected model and variant");
                return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.BAD_REQUEST);
            }

        } else {
            baseResponse = helper.extendedWarantyUpdate(request);
        }


//        BaseResponse baseResponse = helper.extendedWarantyUpdate(request);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "extendedWarranty/{id}", method = {RequestMethod.GET})
    public ResponseEntity<?> getextendedWaranty(@PathVariable int id) {
        ExtendedWarantyResponse response = helper.getextendedWaranty(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "extendedWarranty", method = {RequestMethod.GET})
    public ResponseEntity<?> getextendedWarantys(WarantyFilter request) {
        ExtendedWarantyResponse response = helper.getextendedWarantys(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "extendedWarranty/{id}", method = {RequestMethod.DELETE})
    public ResponseEntity<BaseResponse> extendedWarantyDelete(@PathVariable int id) {
        BaseResponse baseResponse = helper.extendedWarantyDelete(id);
        return new ResponseEntity<BaseResponse>(baseResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping(value = "/uploadBulkUpload/{uploadname}/{pageId}/{userId}")
    public ResponseEntity<?> createAutBulkUpload(HttpServletRequest request,
                                                 @PathVariable(name="uploadname") String name,
                                                 @PathVariable(name="userId") int userId,
                                                 @PathVariable(name="pageId") int pageId,
                                                 @RequestParam("file") MultipartFile bulkExcel) throws IOException {
        try {
            if (name.equals("extendedWarranty")) {
                BulkUploadResponse bulkUploadResponse = helper.bulkUploadWarranty(bulkExcel, userId);
                return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
            } else if(name.equals("demoVehicle")){
                BulkUploadResponse bulkUploadResponse = helper.bulkUploadWDemoVehicle(bulkExcel, request.getHeader("Authorization"),userId);
                return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
            }else if(name.equals("rulesconfiguration")){
                if(pageId !=0){
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadRulesConfiguration(bulkExcel, request.getHeader("Authorization"),userId,pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }else if(name.equals("source")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadSource(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }else if(name.equals("subsource")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadSubSource(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }else if(name.equals("enquirysegment")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadEnquirySegment(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }else if(name.equals("evaluationparameters")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadEvalutionParemeters(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }else if(name.equals("customertype")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadCustomerType(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            } else if(name.equals("complaintfactor")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadComplaintFactor(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }else if(name.equals("bankfinancier")){
                if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploadBankFinancier(bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            else {
                return new ResponseEntity<>("Something went wrong", HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @CrossOrigin
    @PostMapping(value = "/uploadBulkUpload2/{uploadname}/{pageId}/{userId}")
    public ResponseEntity<?> createAutBulkUpload2(HttpServletRequest request,
                                                 @PathVariable(name="uploadname") String name,
                                                 @PathVariable(name="userId") int userId,
                                                 @PathVariable(name="pageId") int pageId,
                                                 @RequestParam("file") MultipartFile bulkExcel) throws IOException {
        try {
            switch(name) {
            case "insurenceCompanyName": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "deliveryCheckList": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "followupReason": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "otherMaker": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "otherModel": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "leadMgmt": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "unionTerrotry": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "insuranceList": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.processBulkExcelForInsuranceList(bulkExcel,userId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "insuranceAddOn": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.processBulkExcelForInsuranceAddOn(bulkExcel,userId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "modelAccessory": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "employee": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "oem": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "interStateTax": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "intraStateTax": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "subLostReasons": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "lostReasons": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "holidaysAddtion": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads(name,bulkExcel, request.getHeader("Authorization"), userId, pageId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            default :
            	BulkUploadResponse res = new BulkUploadResponse();
    			List<String> FailedRecords =new ArrayList<>();
    			String resonForFailure = "End Point not found";
    			FailedRecords.add(resonForFailure);
    			res.setFailedCount(0);
    			res.setFailedRecords(FailedRecords);
    			res.setSuccessCount(0);
    			res.setTotalCount(0);
    			return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }catch (Exception e){
        	BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }
    
    @CrossOrigin
    @PostMapping(value = "/uploadBulkUpload3/{uploadname}/{pageId}/{userId}/{modelId}")
    public ResponseEntity<?> createAutBulkUpload3(HttpServletRequest request,
                                                 @PathVariable(name="uploadname") String name,
                                                 @PathVariable(name="userId") int userId,
                                                 @PathVariable(name="pageId") int pageId,
                                                 @PathVariable(name="modelId") int modelId,
                                                 @RequestParam("file") MultipartFile bulkExcel) throws IOException {
        try {
            switch(name) {
            case "accessoryVarient": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.bulkUploads3(name,bulkExcel, request.getHeader("Authorization"), userId, pageId,modelId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "variants": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.processBulkExcelForVariants(bulkExcel,userId,modelId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            case "oem with model mapping": 
            {
            	if(pageId !=0) {
                    BulkUploadResponse bulkUploadResponse = helper.processBulkExcelForOemModelMapping(bulkExcel,userId,modelId);
                    return new ResponseEntity<>(bulkUploadResponse, HttpStatus.OK);
                }else{
                    return new ResponseEntity<>("Please add pageId", HttpStatus.OK);
                }
            }
            default :
            	BulkUploadResponse res = new BulkUploadResponse();
    			List<String> FailedRecords =new ArrayList<>();
    			String resonForFailure = "End Point not found";
    			FailedRecords.add(resonForFailure);
    			res.setFailedCount(0);
    			res.setFailedRecords(FailedRecords);
    			res.setSuccessCount(0);
    			res.setTotalCount(0);
    			return new ResponseEntity<>(res, HttpStatus.OK);
            }
        }catch (Exception e){
        	BulkUploadResponse res = new BulkUploadResponse();
			List<String> FailedRecords =new ArrayList<>();
			String resonForFailure = e.getMessage();
			FailedRecords.add(resonForFailure);
			res.setFailedCount(0);
			res.setFailedRecords(FailedRecords);
			res.setSuccessCount(0);
			res.setTotalCount(0);
			return new ResponseEntity<>(res, HttpStatus.OK);
        }
    }
}
