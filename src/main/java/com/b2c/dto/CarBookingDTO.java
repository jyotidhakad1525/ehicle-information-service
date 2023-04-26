package com.b2c.dto;

import com.b2c.vehicle.carbooking.VehicleBookinginfo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class CarBookingDTO {


    private Integer id;

    private Integer organizationId;

    private Integer customerId;

    private Integer vehicleId;

    private Integer branchId;

    private int varientId;

    private int colorId;

    private String typeOfCustomer;

    private Integer isThisFristCar;

    private BigDecimal exShowroomPrice;

    private BigDecimal roadTax;

    private BigDecimal registrationCharges;

    private String insurenceComapny;

    private BigDecimal insurenceAmount;

    private Integer warrantyId;

    private BigDecimal warrantyAmount;

    private BigDecimal handlingCharges;

    private BigDecimal essentialKit;

    private BigDecimal tcs;

    private BigDecimal finalPrice;

    private String insurenceAddOn;

    private String offers;

    private VehicleBookinginfo vehicleInfo;

}
