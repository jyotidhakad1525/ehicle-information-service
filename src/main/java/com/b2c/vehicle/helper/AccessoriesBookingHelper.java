package com.b2c.vehicle.helper;

import com.b2c.model.AccessoriesBooking;
import com.b2c.model.VehicleDetails;
import com.b2c.repository.AccessoriesBookingRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.util.SendEmailSms;
import com.b2c.vehicle.accessories.booking.Accessories;
import com.b2c.vehicle.accessories.booking.AccessoriesBookingRequest;
import com.b2c.vehicle.accessories.booking.AccessoriesBookingResponse;
import com.b2c.vehicle.common.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class AccessoriesBookingHelper {

    private final AccessoriesBookingRepository repository;
    private final VehicleDetailsRepository vehicleRepository;
    private final CustomerInfoHelper customerInfoHelper;
    private final SendEmailSms sendEmailSms;

    public AccessoriesBookingHelper(AccessoriesBookingRepository repository,
                                    VehicleDetailsRepository vehicleRepository, CustomerInfoHelper customerInfoHelper,
                                    SendEmailSms sendEmailSms) {
        this.repository = repository;
        this.vehicleRepository = vehicleRepository;
        this.customerInfoHelper = customerInfoHelper;
        this.sendEmailSms = sendEmailSms;
    }

    public BaseResponse accessoriesBookingSave(AccessoriesBookingRequest request) {
        AccessoriesBooking booking = request.getAccessoriesBooking();
        AccessoriesBooking accessoriesBooking = repository.save(booking);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(accessoriesBooking.getId() + "");
        return successResponse;

    }

    public BaseResponse accessoriesBookingUpdate(AccessoriesBookingRequest request) {
        AccessoriesBooking booking = request.getAccessoriesBooking();
        AccessoriesBooking accessoriesBooking = repository.save(booking);
        String status = accessoriesBooking.getStatus();
        if ("Success".equalsIgnoreCase(status)) {
            sendEmailSms.sendEmailSmsNotification(accessoriesBooking.getOrganizationId(),
                    accessoriesBooking.getBranchId(), accessoriesBooking.getCustomerId(),
                    accessoriesBooking.getVehicleId(), null, accessoriesBooking.getAmount(), "ACCESSORIES");
        }


        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(accessoriesBooking.getId() + "");
        return successResponse;
    }

    public AccessoriesBookingResponse getaccessoriesBooking(int id) {
        AccessoriesBookingResponse response = new AccessoriesBookingResponse();
        AccessoriesBooking accessoriesBooking = repository.findById(id).get();
        if (Utils.isEmpty(accessoriesBooking)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        // getUniversal ids Map
        Map<String, Person> universalIdsmap = new HashMap<>();
        if (Utils.isNotEmpty(accessoriesBooking.getCustomerId())) {
            universalIdsmap.put(String.valueOf(accessoriesBooking.getCustomerId()), null);
        }
        //  call to DMS Lead get customerInformation based on universial ids
        customerInfoHelper.personInfo(universalIdsmap);

        response.setAccessoriesInfo(mapBooingInfo(accessoriesBooking, universalIdsmap));
        return Utils.constructSuccessResponse(response);
    }

    public BaseResponse accessoriesBookingDelete(int id) {
        repository.deleteById(id);
        BaseResponse successResponse = Utils.SuccessResponse("Statutory " + id + " Deleted Successfully");
        return successResponse;
    }

    public AccessoriesBookingResponse getaccessoriesBookings(BaseFilter request) {
        AccessoriesBookingResponse response = new AccessoriesBookingResponse();
        BigInteger orgId = request.getOrgId();
        BigInteger branch = request.getBranch();
        String customerId = request.getCustomerId();
        List<AccessoriesBooking> accessoriesBookings = null;
        Specification<AccessoriesBooking> specification = null;
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
            Page<AccessoriesBooking> page = repository.findAll(specification, pageable);
            accessoriesBookings = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            accessoriesBookings = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(accessoriesBookings)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }

        response.setAccessories(mappVehicleBookings(accessoriesBookings));
        Utils.constructSuccessResponse(response);
        response.setCount(accessoriesBookings.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }

    private List<Accessories> mappVehicleBookings(List<AccessoriesBooking> bookings) {
        List<Accessories> bookingDTOs = new ArrayList<>();

        // getUniversal ids Map
        Map<String, Person> universalIdsmap = new HashMap<>();
        for (AccessoriesBooking accessoriesBooking : bookings) {
            if (Utils.isNotEmpty(accessoriesBooking.getCustomerId())) {
                universalIdsmap.put(String.valueOf(accessoriesBooking.getCustomerId()), null);
            }
        }
        //  call to DMS Lead get customerInformation based on universial ids
        customerInfoHelper.personInfo(universalIdsmap);

        for (AccessoriesBooking booking : bookings) {
            bookingDTOs.add(mapBooingInfo(booking, universalIdsmap));
        }

        return bookingDTOs;
    }

    private Accessories mapBooingInfo(AccessoriesBooking booking, Map<String, Person> universalIdsmap) {
        int vehicleId = booking.getVehicleId();
        Accessories bookingDTO = new Accessories();
        bookingDTO.setAccessoriesBooking(booking);
        Optional<VehicleDetails> optional = vehicleRepository.findById(vehicleId);
        if (optional.isPresent()) {
            VehicleDetails vehicleDetails = optional.get();
            vehicleDetails.setVarients(null);
            vehicleDetails.setVehicleEdocuments(null);
            vehicleDetails.setGallery(null);
            bookingDTO.setVehicleDetails(vehicleDetails);
        }

        if (Utils.isNotEmpty(booking.getCustomerId())) {

            String customerId = String.valueOf(booking.getCustomerId());
            Person person = universalIdsmap.get(customerId);
            if (Utils.isEmpty(person)) {
                person = new Person();
                person.setId(customerId);
            }
            bookingDTO.setCustomerInfo(person);
        }


        return bookingDTO;
    }

}
