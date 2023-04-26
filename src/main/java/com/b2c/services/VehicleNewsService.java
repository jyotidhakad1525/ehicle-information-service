package com.b2c.services;

import com.b2c.model.VehicleNews;
import com.b2c.repository.VehicleNewsRepository;
import com.b2c.vehicle.common.*;
import com.b2c.vehicle.news.VehicleNewsRequest;
import com.b2c.vehicle.news.VehicleNewsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleNewsService {

    @Autowired
    VehicleNewsRepository repository;

    public BaseResponse save(VehicleNewsRequest request) {
        VehicleNews model = request.getNews();
        if (!Utils.isEmpty(model.getId())) {
            model.setCreatedDatetime(new Date());
        }
        model.setModifiedDatetime(new Date());
        VehicleNews dbModel = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(dbModel.getId() + "");
        return successResponse;

    }

    public BaseResponse update(VehicleNewsRequest request) {
        return save(request);
    }

    public VehicleNewsResponse getNews(int id) {
        VehicleNewsResponse response = new VehicleNewsResponse();
        Optional<VehicleNews> optional = repository.findById(id);
        if (!optional.isPresent()) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }
        VehicleNews dbModel = optional.get();
        response.setNews(dbModel);
        return Utils.constructSuccessResponse(response);
    }

    public BaseResponse delete(int id) {
        repository.deleteById(id);
        BaseResponse successResponse = Utils.SuccessResponse(id + " Deleted Successfully");
        return successResponse;
    }

    @SuppressWarnings("null")
    public VehicleNewsResponse getNews(BaseFilter request) {
        VehicleNewsResponse response = new VehicleNewsResponse();
        BigInteger orgId = request.getOrgId();
        BigInteger branch = request.getBranch();
        List<VehicleNews> dbresponse = null;
        Specification<VehicleNews> specification = null;

        if (Utils.isNotEmpty(orgId)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("orgId", orgId));
            } else {
                specification = specification.and(CustomSpecification.attribute("orgId", orgId));
            }
        }


        if (Utils.isNotEmpty(request.getId())) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("id", request.getId()));
            } else {
                specification = specification.and(CustomSpecification.attribute("id", request.getId()));
            }
        }

        if (Utils.isNotEmpty(branch)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("branchId", branch));
            } else {
                specification = specification.and(CustomSpecification.attribute("branchId", branch));
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
            Page<VehicleNews> page = repository.findAll(specification, pageable);
            dbresponse = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            dbresponse = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(dbresponse)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setVehicleNews(dbresponse);
        Utils.constructSuccessResponse(response);
        response.setCount(dbresponse.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }

}
