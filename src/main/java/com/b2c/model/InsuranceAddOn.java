package com.b2c.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "insurance_add_on")
public class InsuranceAddOn implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "organization_id")
    private BigInteger organization_id;
    @Column(name = "vehicle_id")
    private BigInteger vehicle_id;
    @Convert(converter = JpaJsonDocumentsListConverter.class)
    @Column(name = "add_on_price", columnDefinition = "TEXT", nullable = true)
    private List<HashMap<String, Object>> add_on_price;
    @Column(name = "varient_id")
    private Integer varient_id;
}
