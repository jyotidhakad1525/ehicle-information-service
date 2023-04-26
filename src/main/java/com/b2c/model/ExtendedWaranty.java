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
@Table(name = "extended_warranty")
public class ExtendedWaranty implements Serializable {

    private static final long serialVersionUID = -434819751845591516L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "organization_id")
    private BigInteger organization_id;
    @Column(name = "vehicle_id")
    private BigInteger vehicle_id;
    @Column(name = "varient_id")
    private Integer varient_id;
    @Convert(converter = JpaJsonDocumentsListConverter.class)
    @Column(name = "warranty", columnDefinition = "TEXT", nullable = true)
    private List<HashMap<String, Object>> warranty;
}
