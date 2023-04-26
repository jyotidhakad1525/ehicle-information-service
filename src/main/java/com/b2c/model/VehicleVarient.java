package com.b2c.model;

import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "vehicle_varient_new")
@Data
public class VehicleVarient implements Serializable {

    private static final long serialVersionUID = -4198136128926450791L;
    @Id
    @Column(name = "id")
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "org_id")
    private Integer organizationId;
    @Column(name = "name")
    private String name;
    @Column(name = "vehicle_id")
    private Integer vehicleId;
    @Column(name = "fuel_type")
    private String fuelType;
    @Column(name = "transmission_type")
    private String transmission_type;
    @Column(name = "mileage")
    private String mileage;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    @Column(name = "enginecc")
    private String enginecc;
    @Column(name = "bhp")
    private String bhp;

    @OneToMany(targetEntity = VehicleImage.class, cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "varient_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Set<VehicleImage> vehicleImages;
}
