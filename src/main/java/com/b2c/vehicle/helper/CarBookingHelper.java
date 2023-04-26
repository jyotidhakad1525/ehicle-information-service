package com.b2c.vehicle.helper;

import com.b2c.dto.CarBookingDTO;
import com.b2c.model.CarBooking;
import com.b2c.model.VehicleDetails;
import com.b2c.model.VehicleImage;
import com.b2c.model.VehicleVarient;
import com.b2c.repository.CarBookingRepository;
import com.b2c.repository.VehicleDetailsRepository;
import com.b2c.repository.VehicleVarientRepository;
import com.b2c.util.SendEmailSms;
import com.b2c.vehicle.carbooking.CarBookingRequest;
import com.b2c.vehicle.carbooking.CarBookingResponse;
import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import com.b2c.vehicle.common.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

@Service
public class CarBookingHelper {

    private final CarBookingRepository repository;
    private final VehicleDetailsRepository vehicleRepository;
    private final VehicleVarientRepository varientRepository;
    private final SendEmailSms sendEmailSms;
    private final CustomerInfoHelper customerInfoHelper;

    public CarBookingHelper(CarBookingRepository repository, VehicleDetailsRepository vehicleRepository,
                            VehicleVarientRepository varientRepository, SendEmailSms sendEmailSms,
                            CustomerInfoHelper customerInfoHelper) {
        this.repository = repository;
        this.vehicleRepository = vehicleRepository;
        this.varientRepository = varientRepository;
        this.sendEmailSms = sendEmailSms;
        this.customerInfoHelper = customerInfoHelper;
    }

    public BaseResponse saveCarBooking(CarBookingRequest request) {
        CarBooking booking = request.getBooking();
        CarBooking carBooking = repository.save(booking);
        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(carBooking.getId() + "");
        return successResponse;
    }

    public BaseResponse updateCarBooking(CarBookingRequest request) {
        CarBooking booking = request.getBooking();
        CarBooking carBooking = repository.save(booking);
        sendEmailSms.sendEmailSmsNotification(carBooking.getOrganizationId(), carBooking.getBranchId(),
                carBooking.getCustomerId(), carBooking.getVehicleId(), carBooking.getVarientId(),
                carBooking.getFinalPrice().doubleValue(), "CAR");

        BaseResponse successResponse = Utils.SuccessResponse();
        successResponse.setConfirmationId(carBooking.getClass() + "");
        return successResponse;
    }

    public CarBookingResponse getCarBooking(int id) {
        CarBookingResponse response = new CarBookingResponse();
        CarBooking carBooking = repository.findById(id).get();
        if (Utils.isEmpty(carBooking)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }
        // getUniversal ids Map
        Map<String, Person> universalIdsmap = new HashMap<>();
        if (Utils.isNotEmpty(carBooking.getCustomerId())) {
            universalIdsmap.put(String.valueOf(carBooking.getCustomerId()), null);
        }
        // call to DMS Lead get customerInformation based on universial ids
        customerInfoHelper.personInfo(universalIdsmap);

        response.setBooking(mapBooingInfo(carBooking, universalIdsmap));
        return Utils.constructSuccessResponse(response);
    }

    public CarBookingResponse getCarBookings(BaseFilter request) {
        CarBookingResponse response = new CarBookingResponse();
        BigInteger orgId = request.getOrgId();
        BigInteger branch = request.getBranch();
        List<CarBooking> bookings = null;
        Specification<CarBooking> specification = null;
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
            if (Utils.isEmpty(specification)) {
                specification = Specification.where(CustomSpecification.attribute("id", request.getId()));
            } else {
                specification = specification.and(CustomSpecification.attribute("id", request.getId()));
            }
        }

        if (Utils.isNotEmpty(request.getCustomerId())) {
            if (Utils.isEmpty(specification)) {
                specification = Specification
                        .where(CustomSpecification.attribute("customerId", request.getCustomerId()));
            } else {
                specification = specification.and(CustomSpecification.attribute("customerId", request.getCustomerId()));
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
            Page<CarBooking> page = repository.findAll(specification, pageable);
            bookings = page.getContent();
            response.setTotalCount(Integer.valueOf(page.getTotalElements() + ""));
            total = page.getTotalElements();
        } else {
            bookings = repository.findAll(specification, sort);
        }

        if (Utils.isEmpty(bookings)) {
            throw new BaseException(ErrorMessages.DATA_NOT_FOUND);
        }
        // Map Vehicle booing info
        List<CarBookingDTO> vehicleBookings = mappVehicleBookings(bookings);

        response.setBookings(vehicleBookings);
        Utils.constructSuccessResponse(response);
        response.setCount(bookings.size());
        if (response.getCount() > 0 && response.getTotalCount() == 0) {
            response.setTotalCount(Integer.valueOf(total + ""));
        }
        return response;
    }

    private List<CarBookingDTO> mappVehicleBookings(List<CarBooking> bookings) {

        // getUniversal ids Map
        Map<String, Person> universalIdsmap = new HashMap<>();
        for (CarBooking carBooking : bookings) {
            if (Utils.isNotEmpty(carBooking.getCustomerId())) {
                universalIdsmap.put(String.valueOf(carBooking.getCustomerId()), null);
            }
        }
        // call to DMS Lead get customerInformation based on universial ids
        customerInfoHelper.personInfo(universalIdsmap);

        List<CarBookingDTO> bookingDTOs = new ArrayList<>();
        for (CarBooking booking : bookings) {
            bookingDTOs.add(mapBooingInfo(booking, universalIdsmap));
        }

        return bookingDTOs;
    }

    private CarBookingDTO mapBooingInfo(CarBooking booking, Map<String, Person> universalIdsmap) {
        int vehicleId = booking.getVehicleId();
        int varientId = booking.getVarientId();
        int colorId = booking.getColorId();
        CarBookingDTO bookingDTO = new CarBookingDTO();
        BeanUtils.copyProperties(booking, bookingDTO);

        VehicleBookinginfo vehicleBookinginfo = new VehicleBookinginfo();

        VehicleDetails vehicleDetails = vehicleRepository.findById(vehicleId).get();
        vehicleDetails.setVarients(null);
        vehicleDetails.setVehicleEdocuments(null);
        vehicleDetails.setGallery(null);

        vehicleBookinginfo.setVehicleDetails(vehicleDetails);

        if (Utils.isNotEmpty(booking.getCustomerId())) {

            String customerId = String.valueOf(booking.getCustomerId());
            Person person = universalIdsmap.get(customerId);
            if (Utils.isEmpty(person)) {
                person = new Person();
                person.setId(customerId);
            }
            vehicleBookinginfo.setCustomerInfo(person);
        }

        VehicleVarient vehicleVarient = varientRepository.findById(varientId).get();
        Set<VehicleImage> vehicleImages = vehicleVarient.getVehicleImages();

        VehicleImage colorInfo = new VehicleImage();

        for (VehicleImage image : vehicleImages) {
            if (image.getVehicleImageId().intValue() == colorId && image.getVarient_id().intValue() == varientId
                    && image.getVehicleId().intValue() == vehicleId) {
                colorInfo = image;
                break;
            }
        }
        vehicleBookinginfo.setColorInfo(colorInfo);
        vehicleVarient.setVehicleImages(null);
        vehicleBookinginfo.setVarient(vehicleVarient);
        bookingDTO.setVehicleInfo(vehicleBookinginfo);

        return bookingDTO;
    }
}
