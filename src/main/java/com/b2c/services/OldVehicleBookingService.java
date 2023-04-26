package com.b2c.services;

import com.b2c.model.InventoryUsedCar;
import com.b2c.model.OldVehicleBooking;
import com.b2c.repository.InventoryUsedCarRepository;
import com.b2c.repository.OldVehicleBookingRepository;
import com.b2c.util.Constants;
import com.b2c.vehicle.common.*;
import com.b2c.vehicle.helper.CustomerInfoHelper;
import com.b2c.vehicle.usedvehicle.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OldVehicleBookingService {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    CustomerInfoHelper customerInfoHelper;
    @Autowired
    private OldVehicleBookingRepository oldVehicleBookingRepository;
    @Autowired
    private InventoryUsedCarRepository inventoryUsedCarRepository;
    @Value("${b2c.old.car.info.service}")
    private String oldCarInfoApi;

    /**
     * @param oldVehicleBookingReq
     * @return
     */
    public OldVehicleBookingRes saveCarBooking(@RequestBody OldVehicleBookingReq oldVehicleBookingReq) {

        OldVehicleBookingRes oldVehicleBookingRes = new OldVehicleBookingRes();

        OldVehicleBooking oldVehicleBooking = new OldVehicleBooking();

        BeanUtils.copyProperties(oldVehicleBookingReq.getOldVehicleBookingInfo(), oldVehicleBooking);

        oldVehicleBookingRepository.save(oldVehicleBooking);

        oldVehicleBookingRes.setStatus(Constants.SUCCESS);
        oldVehicleBookingRes.setStatusCode(HttpStatus.OK.toString());
        oldVehicleBookingRes.setStatusDescription("Successfully Created/Updated");

        return oldVehicleBookingRes;
    }

    /**
     * @param id
     * @return
     */
    public BaseResponse deleteOldVehicleBooking(String id) {

        BaseResponse baseResponse = new BaseResponse();

        Boolean isIdExists = oldVehicleBookingRepository.existsById(Integer.valueOf(id));

        if (isIdExists) {
            oldVehicleBookingRepository.deleteById(Integer.valueOf(id));
            baseResponse.setStatus(Constants.SUCCESS);
            baseResponse.setStatusCode(HttpStatus.OK.toString());
            baseResponse.setStatusDescription("Successfully Deleted");
        } else {
            baseResponse.setStatus(Constants.DATA_NOT_FOUND);
            baseResponse.setStatusCode(HttpStatus.NO_CONTENT.toString());
            baseResponse.setStatusDescription(Constants.ID_DOES_NOt_EXISTS);
        }
        return baseResponse;
    }

    /**
     * @param branchId
     * @param brandId
     * @param customerId
     * @param organizationId
     * @return
     */
    public OldVehicleBookingInqRes getOldCarBookingDetails(BaseFilter baseFilter) {

        BigInteger orgId = baseFilter.getOrgId();
        BigInteger branch = baseFilter.getBranch();
        List<OldVehicleBooking> oldVehicleBookings = null;
        Specification<OldVehicleBooking> specification = null;
        if (Utils.isNotEmpty(orgId)) {
            specification = Specification.where(CustomSpecification.hasOrgId(orgId));
        }

        if (Utils.isNotEmpty(branch)) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.hasBranch(branch));
            } else {
                specification = specification.and(CustomSpecification.hasBranch(branch));
            }
        }

        if (Utils.isNotEmpty(baseFilter.getId())) {
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("id", baseFilter.getId()));
            } else {
                specification = specification.and(CustomSpecification.attribute("id", baseFilter.getId()));
            }
        }

        if (Utils.isNotEmpty(baseFilter.getCustomerId())) {
            if (Utils.isEmpty(specification)) {
                specification = Specification
                        .where(CustomSpecification.attribute("customerId", baseFilter.getCustomerId()));
            } else {
                specification = specification
                        .and(CustomSpecification.attribute("customerId", baseFilter.getCustomerId()));
            }
        }

        //oldVehicleBookings = oldVehicleBookingRepository.findAll(specification, sort);

        Sort sort = Sort.by("id").descending();
        long total = 0;
        if (Utils.isNotEmpty(baseFilter.getOffset())) {
            int perPage = Utils.isNotEmpty(baseFilter.getLimit()) ? baseFilter.getLimit() : 50;
            Pageable pageable = PageRequest.of(baseFilter.getOffset(), perPage, sort);
            Page<OldVehicleBooking> page = oldVehicleBookingRepository.findAll(specification, pageable);
            oldVehicleBookings = page.getContent();
            total = page.getTotalElements();
        } else {
            oldVehicleBookings = oldVehicleBookingRepository.findAll(specification, sort);
        }

        if (Utils.isEmpty(oldVehicleBookings)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }
        OldVehicleBookingInqRes bookingInqRes = getOldVehicleBookingInqRes(oldVehicleBookings);
        bookingInqRes.setCount(oldVehicleBookings.size());
        bookingInqRes.setTotalCount(Integer.valueOf(total + ""));
        return bookingInqRes;
    }

    /**
     * @param oldVehicleBookings
     * @return
     */
    private OldVehicleBookingInqRes getOldVehicleBookingInqRes(List<OldVehicleBooking> oldVehicleBookings) {

        OldVehicleBookingInqRes oldVehicleBookingInqRes = new OldVehicleBookingInqRes();

        if (Utils.isEmpty(oldVehicleBookings)) {
            oldVehicleBookingInqRes.setStatus(Constants.DATA_NOT_FOUND);
            oldVehicleBookingInqRes.setStatusCode(HttpStatus.NO_CONTENT.toString());
            return oldVehicleBookingInqRes;
        }

        // getUniversal ids Map
        Map<String, Person> universalIdsmap = new HashMap<>();
        for (OldVehicleBooking oldVehicleBooking : oldVehicleBookings) {
            if (Utils.isNotEmpty(oldVehicleBooking.getCustomerId())) {
                universalIdsmap.put(String.valueOf(oldVehicleBooking.getCustomerId()), null);
            }
        }
        //  call to DMS Lead get customerInformation based on universial ids
        customerInfoHelper.personInfo(universalIdsmap);

        List<OldVehicleBookingInqInfo> oldVehicleBookingInfos = oldVehicleBookings.stream().map(vehicleBooking -> {

            OldVehicleBookingInqInfo oldVehicleBookingInfo = new OldVehicleBookingInqInfo();

            BeanUtils.copyProperties(vehicleBooking, oldVehicleBookingInfo);

            Optional<InventoryUsedCar> inventoryUsedCarOptional = inventoryUsedCarRepository
                    .findByUsedCarId(Long.valueOf(oldVehicleBookingInfo.getUsedCarId()));

            InventoryUsedCar inventoryUsedCar = inventoryUsedCarOptional.isPresent() ? inventoryUsedCarOptional.get()
                    : new InventoryUsedCar();

            InventoryUsedCarInfo inventoryUsedCarInfo = new InventoryUsedCarInfo();
            BeanUtils.copyProperties(inventoryUsedCar, inventoryUsedCarInfo);

            if (Utils.isNotEmpty(vehicleBooking.getCustomerId())) {

                String customerId = String.valueOf(vehicleBooking.getCustomerId());
                Person person = universalIdsmap.get(customerId);
                if (Utils.isEmpty(person)) {
                    person = new Person();
                    person.setId(customerId);
                }
                oldVehicleBookingInfo.setCustomerInfo(person);
            }
            oldVehicleBookingInfo.setInventoryUsedCarInfo(inventoryUsedCarInfo);
            OldVehicleDetails oldVehicleDetails =
                    invokeOldCarInfoService(String.valueOf(oldVehicleBookingInfo.getOrganizationId()));
            oldVehicleBookingInfo.setBrandInfo(getBrandDetails(oldVehicleDetails, oldVehicleBookingInfo.getBrandId()));
            oldVehicleBookingInfo.setModelInfo(getModelDetails(oldVehicleDetails, oldVehicleBookingInfo.getModelId()));
            return oldVehicleBookingInfo;

        }).collect(Collectors.toList());

        oldVehicleBookingInqRes.setOldVehicleBookingInfos(oldVehicleBookingInfos);

        oldVehicleBookingInqRes.setStatus(Constants.SUCCESS);
        oldVehicleBookingInqRes.setStatusCode(HttpStatus.OK.toString());

        return oldVehicleBookingInqRes;
    }

    /**
     * @param orgId
     * @return
     */
    private OldVehicleDetails invokeOldCarInfoService(String orgId) {

        OldVehicleDetails oldVehicleDetails = new OldVehicleDetails();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("orgId", orgId);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<OldVehicleDetails> OldVehicleDetails = restTemplate.exchange(oldCarInfoApi, HttpMethod.GET,
                    entity, OldVehicleDetails.class);
            return OldVehicleDetails.getBody();
        } catch (Exception e) {
            return oldVehicleDetails;
        }
    }

    /**
     * @param oldVehicleDetails
     * @param brandId
     * @return
     */
    private BrandInfo getBrandDetails(OldVehicleDetails oldVehicleDetails, Integer brandId) {
        BrandInfo brandInfo = new BrandInfo();
        if (Utils.isEmpty(oldVehicleDetails) || Utils.isEmpty(oldVehicleDetails.getBrand())) {
            return brandInfo;
        }
        return oldVehicleDetails.getBrand().stream().filter(brand -> Utils.isNotEmpty(brand.getId())
                && brand.getId().equals(brandId)).findAny().orElse(brandInfo);

    }

    /**
     * @param oldVehicleDetails
     * @param modelId
     * @return
     */
    private ModelInfo getModelDetails(OldVehicleDetails oldVehicleDetails, Integer modelId) {
        ModelInfo modelInfo = new ModelInfo();
        if (Utils.isEmpty(oldVehicleDetails) || Utils.isEmpty(oldVehicleDetails.getModel())) {
            return modelInfo;
        }
        return oldVehicleDetails.getModel().stream().filter(model -> Utils.isNotEmpty(model.getId())
                && model.getId().equals(modelId)).findAny().orElse(modelInfo);

    }

}
