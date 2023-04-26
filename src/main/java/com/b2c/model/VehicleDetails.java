package com.b2c.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "vehicle_details_new")
public class VehicleDetails implements Serializable {
    private static final long serialVersionUID = 4895908114629386216L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vehicleId;
    @Column(name = "organization_id")
    private Integer organizationId;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(name = "model")
    private String model;
    @Column(name = "maker")
    private String maker;
    @Column(name = "maker_id")
    private Integer makerId;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "created_date")
    private String createdDate;
    @Column(name = "created_by")
    private Integer createdBy;
    @Column(name = "modified_by")
    private Integer modifiedBy;
    @Column(name = "modified_date")
    private String modifiedDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private VehicleStatus status;
    @Column(name = "booking_amount")
    private Integer booking_amount;
    @OneToMany(targetEntity = VehicleEdocuments.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    @Fetch(FetchMode.JOIN)
    private Set<VehicleEdocuments> vehicleEdocuments;
    @OneToMany(targetEntity = Gallery.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    @Fetch(FetchMode.JOIN)
    private Set<Gallery> gallery;
    @OneToMany(targetEntity = VehicleVarient.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    @Fetch(FetchMode.JOIN)
    private Set<VehicleVarient> varients;
    @Column(name = "waiting_period")
    private String waiting_period;
    @Column(name = "description")
    private String description;
    @Column(name = "type_category")
    @Enumerated(EnumType.STRING)
    private TypeCategory typeCategory;
    @Column(name = "price_range")
    private String priceRange;

     public enum TypeCategory {
        Passenger,
        Cargo,
        SPV_Customization,
        Hatchback,
        Sedan,
        Mini_Car,
        SUV
    }

}
