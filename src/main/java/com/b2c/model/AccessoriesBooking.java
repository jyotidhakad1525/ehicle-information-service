package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;


@Setter
@Getter
@Entity
@Table(name = "accessories_booking")
@NamedQuery(name = "AccessoriesBooking.findAll", query = "SELECT a FROM AccessoriesBooking a")
public class AccessoriesBooking implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String accessories;

    private double amount;

    @Column(name = "branch_id")
    private int branchId;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "organization_id")
    private int organizationId;

    private String status;

    @Column(name = "vehicle_id")
    private int vehicleId;

}