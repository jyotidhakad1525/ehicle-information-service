package com.b2c.vehicle.helper;

import com.b2c.model.InsuranceVarientMapping;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleImage;
import com.b2c.model.VehicleVarient;
import com.b2c.repository.InsuranceVarientMappingRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.repository.VehicleVarientRepository;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.b2c.vehicle.common.*;
import com.b2c.vehicle.insurance.mapping.InsuranceVarientMappingRequest;
import com.b2c.vehicle.insurance.mapping.InsuranceVarientMappingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class InsuranceVarientMappingHelper {

    @Autowired
    InsuranceVarientMappingRepository repository;
    @Autowired
    VehicleVarientRepository varientRepository;
    @Autowired
    private VehicleDetailsRepository vehicleRepository;

    public BaseResponse insuranceVarientMappingSave(InsuranceVarientMappingRequest request) {
        InsuranceVarientMapping model = request.getInsuranceMapping();
        InsuranceVarientMapping entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;

    }

    public BaseResponse insuranceVarientMappingUpdate(InsuranceVarientMappingRequest request) {
        InsuranceVarientMapping model = request.getInsuranceMapping();
        InsuranceVarientMapping entity = repository.save(model);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(entity.getId() + "");
        return successResponse;
    }

    public InsuranceVarientMappingResponse getInsuranceVarientMapping(int id) {
        InsuranceVarientMappingResponse response = new InsuranceVarientMappingResponse();
        InsuranceVarientMapping entity = repository.findById(id).get();
        if (Utils.isEmpty(entity)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setInsuranceMapping(entity);
        return Utils.constructSuccessResponse(response);
    }

    public BaseResponse insuranceVarientMappingDelete(int id) {
        repository.deleteById(id);
        BaseResponse successResponse = Utils.SuccessResponse("Statutory " + id + " Deleted Successfully");
        return successResponse;
    }

    @SuppressWarnings("null")
    public InsuranceVarientMappingResponse getInsuranceVarientMappings(InsuranceMappingFilter request) {
        InsuranceVarientMappingResponse response = new InsuranceVarientMappingResponse();
        Integer id = request.getId();
        Integer insurence_id = request.getInsurence_id();
        Integer varient_id = request.getVarient_id();
        String status = request.getStatus();
        List<InsuranceVarientMapping> insuranceMappings = null;
        Specification<InsuranceVarientMapping> specification = null;
        if (Utils.isNotEmpty(id)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("id", id));
            } else {
                specification = specification.and(CustomSpecification.attribute("id", id));
            }
        }


        if (Utils.isNotEmpty(insurence_id)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("insurence_id", insurence_id));
            } else {
                specification = specification.and(CustomSpecification.attribute("insurence_id", insurence_id));
            }
        }

        if (Utils.isNotEmpty(varient_id)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("varient_id", varient_id));
            } else {
                specification = specification.and(CustomSpecification.attribute("varient_id", varient_id));
            }
        }

        if (Utils.isNotEmpty(status)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("status", status));
            } else {
                specification = specification.and(CustomSpecification.attribute("status", status));
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
            Page<InsuranceVarientMapping> page = repository.findAll(specification, pageable);
            insuranceMappings = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            insuranceMappings = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(insuranceMappings)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        mappVehicleBookings(insuranceMappings);

        response.setInsuranceMappingss(insuranceMappings);
        Utils.constructSuccessResponse(response);
        response.setCount(insuranceMappings.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }

    private void mappVehicleBookings(List<InsuranceVarientMapping> mappings) {
        for (InsuranceVarientMapping mapping : mappings) {
            mapBooingInfo(mapping);
        }

    }

    private void mapBooingInfo(InsuranceVarientMapping mapping) {

        int varientId = mapping.getVarient_id();
        //BeanUtils.copyProperties(booking, bookingDTO.getAddOn());

        VehicleBookinginfo vehicleBookinginfo = new VehicleBookinginfo();


        Optional<VehicleVarient> vehicleVarientOptional = varientRepository.findById(varientId);
        if (vehicleVarientOptional.isPresent()) {

            VehicleVarient vehicleVarient = vehicleVarientOptional.get();
            Integer vehicleId = vehicleVarient.getVehicleId();
            Set<VehicleImage> vehicleImages = vehicleVarient.getVehicleImages();
            vehicleVarient.setVehicleImages(null);
            vehicleBookinginfo.setVarient(vehicleVarient);
            VehicleImage colorInfo = new VehicleImage();
            mapping.setVarientName(vehicleVarient.getName());
            mapping.setVehicleId(vehicleId);

            /*
             * for( VehicleImage image :vehicleImages) { if(
             * image.getVarient_id().intValue() == varientId &&
             * image.getVehicleId().intValue() == vehicleId) { colorInfo = image; break; } }
             */
            vehicleBookinginfo.setColorInfo(colorInfo);

            Optional<VehicleDetails> VehicleDetailsOptional = vehicleRepository.findById(vehicleId);
            if (VehicleDetailsOptional.isPresent()) {
                VehicleDetails vehicleDetails = VehicleDetailsOptional.get();
                mapping.setModel(vehicleDetails.getModel());
                vehicleDetails.setVarients(null);
                vehicleDetails.setVehicleEdocuments(null);
                vehicleDetails.setGallery(null);

                vehicleBookinginfo.setVehicleDetails(vehicleDetails);
            }

        }

    }


}
