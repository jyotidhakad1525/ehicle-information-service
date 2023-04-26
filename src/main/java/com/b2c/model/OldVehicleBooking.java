package com.b2c.model;

import com.b2c.vehicle.common.JpaJsonConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "old_vehicle_booking")
@NamedQuery(name = "OldVehicleBooking.findAll", query = "SELECT o FROM OldVehicleBooking o")
public class OldVehicleBooking implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "branch_id")
    private int branchId;

    @Column(name = "brand_id")
    private int brandId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "insurence_add_on")
    @Convert(converter = JpaJsonConverter.class)
    private String insurenceAddOn;

    @Column(name = "insurence_amount")
    private BigDecimal insurenceAmount;

    @Column(name = "insurence_comapny")
    private String insurenceComapny;

    @Column(name = "model_id")
    private int modelId;

    @Column(name = "organization_id")
    private int organizationId;

    @Column(name = "used_car_id")
    private int usedCarId;

    private String varient;

}