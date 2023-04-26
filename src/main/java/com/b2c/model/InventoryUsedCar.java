package com.b2c.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "inventory.inventory_used_car")
public class InventoryUsedCar {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "model_id")
    private Long modelId;

    @Column(name = "varient_id")
    private Long varientId;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy")
    @Column(name = "making_year")
    private Date makingYear;

    @Column(name = "making_month")
    private String makingMonth;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "bargain_type")
    private String bargainType;

    @Column(name = "color")
    private String color;

    @Column(name = "driven_kms")
    private String drivenKms;

    @Column(name = "rc_number")
    private String rcNumber;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "registration_date")
    private Date registrationDate;

    @Column(name = "no_of_owners")
    private String noOfOwners;

    @Column(name = "insurence_type")
    private String insuranceType;

    @Column(name = "customer_id")
    private String customerId;


    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "insurence_validity")
    private Date insuranceValidity;

    @Column(name = "images")
    private String images;

    @Column(name = "created_by")
    private Long createdBy;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "created_datetime")
    private Date createdDatetime;

    @Column(name = "modified_by")
    private Long modifiedBy;

    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "modified_date")
    private Date modifiedDate;

}

