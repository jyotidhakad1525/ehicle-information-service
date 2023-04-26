package com.b2c.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "vehicle_on_road_price")
public class VehicleOnRoadPrice implements Serializable {

    private static final long serialVersionUID = -2568008079221113408L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "organization_id")
    private Integer organization_id;
    @Column(name = "vehicle_id")
    private Integer vehicle_id;
    @Column(name = "varient_id")
    private Integer varient_id;
    @Column(name = "ex_showroom_price")
    private BigDecimal ex_showroom_price;
    @Column(name = "ex_showroom_price_csd")
    private BigDecimal ex_showroom_price_csd;
    @Column(name = "hypothecation_price")
    private BigDecimal hypothecation_price;
    @Column(name = "registration_charges")
    private BigDecimal registration_charges;
    @Column(name = "handling_charges")
    private BigDecimal handling_charges;
    @Column(name = "tcs_percentage")
    private BigDecimal tcs_percentage;
    @Column(name = "tcs_amount")
    private BigDecimal tcs_amount;
    @Column(name = "essential_kit")
    private BigDecimal essential_kit;

    @Column(name = "fast_tag")
    private BigDecimal fast_tag;

    @Column(name = "vehicle_road_tax")
    private BigDecimal vehicle_road_tax;
    @Column(name = "cess_percentage")
    private BigDecimal cess_percentage;
    
    @OneToMany(targetEntity = RoadTax.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "organization_id", referencedColumnName = "organization_id", insertable = false, updatable =
            false)
    @Fetch(FetchMode.JOIN)
    private Set<RoadTax> roadtax;
    
    @Column(name = "registration", columnDefinition = "json")
    @Convert(converter = JpaJsonDocumentsMapConverter.class)
    private Map<String, Object> registration = new HashMap<>();

    @OneToMany(targetEntity = ExtendedWaranty.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "vehicle_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Set<ExtendedWaranty> extended_waranty;

    @OneToMany(targetEntity = InsuranceVarientMapping.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "varient_id", referencedColumnName = "varient_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Set<InsuranceVarientMapping> insurance_vareint_mapping;

    @OneToMany(targetEntity = InsuranceAddOn.class, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "varient_id", referencedColumnName = "varient_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Set<InsuranceAddOn> insuranceAddOn;
}
