package com.b2c.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Map;

@Setter
@Getter
@JsonIgnoreProperties(value = {"status", "brochure"})
@Entity
@Table(name = "road_tax")
public class RoadTax implements Serializable {

    private static final long serialVersionUID = 6590817921472593861L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "organization_id")
    private Integer organization_id;

    @Convert(converter = JpaJsonDocumentsMapConverter.class)
    @Column(name = "tax_calculation", columnDefinition = "TEXT", nullable = true)
    private Map<String, Object> tax_calculation;
}
