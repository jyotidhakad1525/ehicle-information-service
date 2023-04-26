package com.b2c.vehicle.usedvehicle;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OldVehicleBookingInfo {

    private int id;

    private int branchId;

    private int brandId;

    private int customerId;

    private BigDecimal finalPrice;

    private String insurenceAddOn;

    private BigDecimal insurenceAmount;

    private String insurenceComapny;

    private int modelId;

    private int organizationId;

    private int usedCarId;

    private String varient;
}