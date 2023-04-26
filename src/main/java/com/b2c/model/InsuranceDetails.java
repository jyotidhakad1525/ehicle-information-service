package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;

@Setter
@Getter
@Entity
@Table(name = "insurance_details")
public class InsuranceDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "policy_name")
    private String policy_name;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @Column(name = "organization_id")
    private BigInteger organizationId;

}
