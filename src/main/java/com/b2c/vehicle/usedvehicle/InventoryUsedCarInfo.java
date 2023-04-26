package com.b2c.vehicle.usedvehicle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@JsonInclude(Include.NON_NULL)
public class InventoryUsedCarInfo {

    private Long id;

    private Long organizationId;

    private Long branchId;

    private Long brandId;

    private Long modelId;

    private Long varientId;

    private Date makingYear;

    private String makingMonth;

    private BigDecimal price;

    private String bargainType;

    private String color;

    private String drivenKms;

    private String rcNumber;

    private Date registrationDate;

    private String noOfOwners;

    private String insuranceType;

    private String customerId;

    private Date insuranceValidity;

    private String images;

    private Long createdBy;

    private Date createdDatetime;

    private Long modifiedBy;

    private Date modifiedDate;

}
