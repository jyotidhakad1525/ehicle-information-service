package com.b2c.vehicle.usedvehicle;

import com.b2c.vehicle.common.Person;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class OldVehicleBookingInqInfo {

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

    private InventoryUsedCarInfo inventoryUsedCarInfo;

    private BrandInfo brandInfo;
    private ModelInfo modelInfo;

    private Person customerInfo;
}