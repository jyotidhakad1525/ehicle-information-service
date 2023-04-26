package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Setter
@Getter
@Entity
@Table(name = "car_booking")
@NamedQuery(name = "CarBooking.findAll", query = "SELECT c FROM CarBooking c")
public class CarBooking implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "branch_id")
    private int branchId;

    @Column(name = "color_id")
    private int colorId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "essential_kit")
    private BigDecimal essentialKit;

    @Column(name = "ex_showroom_price")
    private BigDecimal exShowroomPrice;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "handling_charges")
    private BigDecimal handlingCharges;

    @Column(name = "insurence_add_on")
    private String insurenceAddOn;

    @Column(name = "insurence_amount")
    private BigDecimal insurenceAmount;

    @Column(name = "insurence_comapny")
    private String insurenceComapny;

    @Column(name = "is_this_frist_car")
    private boolean isThisFristCar;

    private String offers;

    @Column(name = "organization_id")
    private int organizationId;

    @Column(name = "registration_charges")
    private BigDecimal registrationCharges;

    @Column(name = "road_tax")
    private BigDecimal roadTax;

    private BigDecimal tcs;

    @Column(name = "type_of_customer")
    private String typeOfCustomer;

    @Column(name = "varient_id")
    private int varientId;

    @Column(name = "vehicle_id")
    private int vehicleId;

    @Column(name = "warranty_amount")
    private BigDecimal warrantyAmount;

    @Column(name = "warranty_id")
    private int warrantyId;

    @Column(name = "fast_tag")
    private double fastTag;

    private String accessories;

}