package com.b2c.vehicle.helper;

import com.b2c.model.BulkUploadResponse;
import com.b2c.model.DmsEmployee;
import com.b2c.model.InsuranceAddOn;
import com.b2c.model.InsuranceDetails;
import com.b2c.model.VehicleStatus;
import com.b2c.repository.InsuranceDetailRepository;
import com.b2c.vehicle.common.*;
import com.b2c.vehicle.exceptions.VehicleInsuranceException;
import com.b2c.vehicle.insurance.InsuranceDetailsRequest;
import com.b2c.vehicle.insurance.InsuranceDetailsResponse;
import com.b2c.vehicle.insurance.addon.InsuranceAddonRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Service
public class InsuranceDetailsHelper {

    @Autowired
    InsuranceDetailRepository repository;

    public BaseResponse insuranceDetailsSave(InsuranceDetailsRequest request) {
        InsuranceDetails model = request.getInsuranceDetails();
        InsuranceDetails entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public BaseResponse insuranceDetailsUpdate(InsuranceDetailsRequest request) {
        InsuranceDetails model = request.getInsuranceDetails();
        InsuranceDetails entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public InsuranceDetailsResponse getInsuranceDetail(int id) {
        InsuranceDetailsResponse response = new InsuranceDetailsResponse();
        InsuranceDetails entity = repository.findById(id).get();
        if (Utils.isEmpty(entity)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setInsurance(entity);
        return Utils.constructSuccessResponse(response);
    }

    public BaseResponse insuranceDetailsDelete(int id) {
        repository.deleteById(id);
        BaseResponse successResponse = Utils.SuccessResponse("Statutory " + id + " Deleted Successfully");
        return successResponse;
    }

    public InsuranceDetailsResponse getInsuranceDetails(BaseFilter request) {
        InsuranceDetailsResponse response = new InsuranceDetailsResponse();
        BigInteger orgId = request.getOrgId();
        BigInteger branch = request.getBranch();
        String customerId = request.getCustomerId();
        List<InsuranceDetails> insuranceDetails = null;
        Specification<InsuranceDetails> specification = null;
        if (Utils.isNotEmpty(orgId)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("organizationId", orgId));
            } else {
                specification = specification.and(CustomSpecification.attribute("organizationId", orgId));
            }
        }

        if (Utils.isNotEmpty(branch)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.hasBranch(branch));
            } else {
                specification = specification.and(CustomSpecification.hasBranch(branch));
            }
        }

        if (Utils.isNotEmpty(request.getId())) {
            CustomSpecification.attribute("id", request.getId());
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("id", request.getId()));
            } else {
                specification = specification.and(CustomSpecification.attribute("id", request.getId()));
            }
        }

        if (Utils.isNotEmpty(customerId)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("customerId", customerId));
            } else {
                specification = specification.and(CustomSpecification.attribute("customerId", customerId));
            }
        }


        if (Utils.isEmpty(specification)) {
            // throw exception
        }
        Sort sort = Sort.by("id").descending();
        long total = 0;
        if (Utils.isNotEmpty(request.getOffset())) {
            int perPage = Utils.isNotEmpty(request.getLimit()) ? request.getLimit() : 50;
            Pageable pageable = PageRequest.of(request.getOffset(), perPage, sort);
            Page<InsuranceDetails> page = repository.findAll(specification, pageable);
            insuranceDetails = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            insuranceDetails = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(insuranceDetails)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setInsurances(insuranceDetails);
        Utils.constructSuccessResponse(response);
        response.setCount(insuranceDetails.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }    
}
